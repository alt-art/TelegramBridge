This plugin adds a bridge between telegram and your minecraft sever.

## Usage

- Install the plugin and restart your server this will create a config file for you to proceed
- Create a bot with `@BotFather` on telegram and disable “Group Privacy” on bot settings
- Get your `chat_id` from your group with the `@ScanIDBot`
- Now you can change the configuration to put your `chat_id` and `bot_token`
- Restart your server

## Permissions

| key                             | Description                                                            | Default |
|---------------------------------|------------------------------------------------------------------------|---------|
| telegrambridge.receive          | If player should receive message from telegram                         | true    |
| telegrambridge.send             | If message of the player  should be sent to telegram                   | true    |
| telegrambridge.commands.reply   | Ability to reply to messages from telegram on minecraft                | true    |
| telegrambridge.commands.mention | Command that make auto complete on users from telegram to mention them | true    |
| telegrambridge.commands.reload  | Allows the user to reload the TelegramBridge configuration             | op      |

## Config

This is the example config:

```yml
bot_token: your_token # your bot token from @BotFather remember to disable privacy mode
chats: # your chat list, you can add multiple chats use '-' to separate them
  - id: # your chat id, you can get it from @ScanIDBot
    thread: null # your thread id (optional) if you want to send messages to a specific thread. /setthread on telegram chat can be used to set the thread automatically
send_to_chat: true
send_to_telegram: true
log_join_and_leave_event: true
log_death_event: true
log_sleep_event: false
months: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
media_types: ['an image', 'a video', 'a document', 'an audio', 'a voice', 'a sticker', 'a contact', 'a location', 'a poll', 'a media']
messages:
  join: 'Player %playername% joined the game!'
  leave: 'Player %playername% left the game!'
  death: 'Player %playername% died! %deathmessage%'
  sleep: 'Player %playername% is sleeping!'
  telegram: '[%playername%]: %message%'
  media: '§7[§bTelegram§7] §f[%user%] sent %type%\n %caption%'
  chat: '§7[§bTelegram§7] §f[%user%] %message%'
  reply: '- §breply to §7%user%: %message%§r -\n'
  time: 'Time is %time% %emoji%\nDate is %month% %day%, Year %year%'
  online: 'There are %count% players online\n%players%'
  pinned: 'Hey welcome to the chat!\nThere are %count% players online%players%'
```

## Telegram Commands

- `/setthread` - When using threads, this command will set witch thread the events will be sent to
- `/setpin` - Send a message to the chat and pin it which will show dynamically the online players (bot needs to have pin permission) (beta)
- `/unsetpin` - Unpin the message that was pinned by `/setpin` (bot needs to have pin permission) (beta)
- `/online` - Show the online players on the chat
- `/time` - Show the server time and date

## Minecraft Commands

- `/tbreload` - Reload the TelegramBridge configuration file
- `/tbmention <user> <message>` alias `/tbm` - Auto complete on users from telegram to mention them
