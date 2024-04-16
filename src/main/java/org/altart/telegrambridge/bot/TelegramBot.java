package org.altart.telegrambridge.bot;

import org.altart.telegrambridge.config.Config;
import org.altart.telegrambridge.TelegramBridge;
import org.altart.telegrambridge.bot.commands.*;
import org.altart.telegrambridge.bot.feature.MessageListener;
import org.altart.telegrambridge.bot.feature.PinMessage;
import org.altart.telegrambridge.bot.feature.SentMedia;
import org.altart.telegrambridge.bot.feature.UserAutocomplete;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.LinkPreviewOptions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.logging.Logger;

public class TelegramBot extends TelegramLongPollingBot {
    private final Logger log = TelegramBridge.log;
    private final Config config;
    private final Plugin plugin;
    private final Map<String, TelegramCommandExecutor> commands = new HashMap<>();

    private final List<TelegramFeature> features = new ArrayList<>();

    public final PinMessage pinMessageFeature = new PinMessage(this);
    public final UserAutocomplete userAutocompleteFeature = new UserAutocomplete(this);
    public final MessageListener messageListenerFeature = new MessageListener(this);
    public final SentMedia sentMediaFeature = new SentMedia(this);

    public TelegramBot(Plugin plugin) {
        super(Config.getInstance().getBotToken());
        this.config = Config.getInstance();
        this.plugin = plugin;
        commands.put("/online", new OnlineCommand());
        commands.put("/time", new TimeCommand());
        commands.put("/setpin", new SetPinCommand());
        commands.put("/unsetpin", new UnsetPinCommand());
        commands.put("/setthread", new SetThreadCommand());

        features.add(pinMessageFeature);
        features.add(userAutocompleteFeature);
        features.add(messageListenerFeature);
        features.add(sentMediaFeature);
    }

    private void onCommand(String command_text, Message message) {
        String[] command_args = command_text.split(" ");
        int atIndex = command_args[0].indexOf("@");
        String command = command_args[0].substring(0, atIndex == -1 ? command_args[0].length() : atIndex);
        TelegramCommandExecutor executor = commands.get(command);
        if (executor != null) {
            if (executor.requirePermission && isNotAdmin(message.getChatId().toString(), message.getFrom().getId())) {
                reply("You are not an admin", message.getChatId().toString(), message.getMessageId());
                return;
            }
            CommandSender sender = new CommandSender(message, plugin, this);
            executor.onCommand(sender, Arrays.copyOfRange(command_args, 1, command_args.length));
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Message message = update.getMessage();
        if (message == null) return;
        if (message.getFrom().getIsBot()) return;
        String messageChatId = message.getChatId().toString();
        List<Config.Chat> chats = config.getChats();
        if (chats.stream().noneMatch(chat -> chat.id.equals(messageChatId))) return;

        features.parallelStream().forEach(feature -> feature.onUpdateReceived(update));

        if (!message.hasText()) return;
        if (message.getText().startsWith("/")) {
            onCommand(message.getText(), message);
        }
    }

    private boolean isNotAdmin(String chatId, Long userId) {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(chatId);
        try {
            List<ChatMember> chatAdministrators = execute(getChatAdministrators);
            return chatAdministrators.stream().noneMatch(chatMember -> chatMember.getUser().getId().equals(userId));
        } catch (TelegramApiException e) {
            log.severe("Error getting chat administrators: " + e.getMessage());
            return true;
        }
    }

    @Override
    public String getBotUsername() {
        return "TelegramBridgeBot";
    }

    public void sendMessage(String message, String chatId, @Nullable Integer threadId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        sendMessage.setChatId(chatId);
        if (threadId != null) {
            sendMessage.setMessageThreadId(threadId);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.severe("Error sending message: " + e.getMessage());
        }
    }

    public void reply(@NotNull String message, @NotNull String chatId, @NotNull Integer replyMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(replyMessageId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.severe("Error sending reply: " + e.getMessage());
        }
    }

    public Message sendSystemMessage(String message, String chatId, @Nullable Integer threadId) {
        SendMessage sendMessage = new SendMessage();
        LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
        sendMessage.setLinkPreviewOptions(linkPreviewOptions);
        linkPreviewOptions.setIsDisabled(true);
        sendMessage.setText(message);
        sendMessage.setParseMode("HTML");
        sendMessage.setChatId(chatId);
        if (threadId != null) {
            sendMessage.setMessageThreadId(threadId);
        }
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            log.severe("Error sending system message: " + e.getMessage());
            return null;
        }
    }

    public void broadcastMessage(String message) {
        List<Config.Chat> chats = config.getChats();
        for (Config.Chat chat : chats) {
            sendMessage(message, chat.id, chat.thread);
        }
    }

    public void pinMessage(String chatId, Integer messageId) {
        try {
            execute(new PinChatMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.severe("Error pinning message: " + e.getMessage());
        }
    }

    public void unpinMessage(String chatId, Integer messageId) {
        try {
            execute(new UnpinChatMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.severe("Error unpinning message: " + e.getMessage());
        }
    }

    public void editSystemMessage(String message, String chatId, Integer messageId) {
        EditMessageText editMessageText = new EditMessageText();
        LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
        linkPreviewOptions.setIsDisabled(true);
        editMessageText.setLinkPreviewOptions(linkPreviewOptions);
        editMessageText.setText(message);
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setParseMode("HTML");
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.severe("Error editing message: " + e.getMessage());
        }
    }

    public void deleteMessage(String chatId, Integer messageId) {
        try {
            execute(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            log.severe("Error deleting message: " + e.getMessage());
        }
    }

    public class CommandSender {
        public Plugin plugin;
        public Message message;
        public TelegramBot bot;

        private CommandSender(Message message, Plugin plugin, TelegramBot bot) {
            this.message = message;
            this.plugin = plugin;
            this.bot = bot;
        }

        public void sendMessage(String text) {
            String chatId = message.getChatId().toString();
            Integer replyMessageId = message.getMessageId();
            TelegramBot.this.reply(text, chatId, replyMessageId);
        }
    }
}
