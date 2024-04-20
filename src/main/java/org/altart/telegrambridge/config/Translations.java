package org.altart.telegrambridge.config;

import org.altart.telegrambridge.TelegramBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Translations {
    public String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public String[] mediaTypes = new String[]{"an image", "a video", "a document", "an audio", "a voice", "a sticker", "a contact", "a location", "a poll", "a media"};

    public String join = "Player %playername% joined the game!";
    public String leave = "Player %playername% left the game!";
    public String death = "Player %playername% died! %deathmessage%";
    public String sleep = "Player %playername% went to bed!";

    public String chatMessage = "[%playername%]: %message%";
    public String telegramMessage = "§7[§bTelegram§7] §f[%user%] %message%";
    public String telegramMedia = "§7[§bTelegram§7] §f[%user%] sent %type%%caption%";
    public String telegramReply = "- §breply to §7%user%: %message%§r -\n";

    public String online = "There are %count% players online%players%";
    public String time = "Time is %time% %emoji%\nDate is %month% %day%, Year %year%";
    public String pinned = "Hey welcome to the chat!\nThere are %count% players online%players%";

    public Translations() {
        File translationsFile = new File(TelegramBridge.plugin.getDataFolder().getAbsoluteFile(), "translations.yml");
        if (translationsFile.exists()) {
            FileConfiguration translations = YamlConfiguration.loadConfiguration(translationsFile);
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    if (translations.contains(field.getName()) && field.getType().equals(String.class)) {
                        field.set(this, translations.get(field.getName()));
                    }
                } catch (Exception e) {
                    TelegramBridge.log.severe("Error loading translations: " + e.getMessage());
                    Arrays.stream(e.getStackTrace()).forEach(line -> TelegramBridge.log.severe(line.toString()));
                }
            }
        }
    }
}
