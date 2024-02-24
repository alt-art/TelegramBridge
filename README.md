This plugin adds a bridge between telegram and your minecraft sever.

## Usage

- Install the plugin and restart your server this will create a config file for you to proceed
- Create a bot with `@BotFather` on telegram and disable “Group Privacy” on bot settings
- Get your `chat_id` from your group with the `@ScanIDBot`
- Now you can change the configuration to put your `chat_id` and `bot_token`
- Restart your server

## Permissions

|key|Description|
|-|-|
|telegrambridge.receive|If players should receive message from telegram|
|telegrambridge.send|If message of the players should be sent to telegram|
|telegrambridge.commands.reply|Ability to reply to messages from telegram on minecraft|
|telegrambridge.commands.mention|Command that make auto complete on users from telegram to mention them|

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
messages:
  join: 'Player %playername% joined the game!'
  leave: 'Player %playername% left the game!'
  death: 'Player %playername% died! %deathmessage%'
  sleep: 'Player %playername% is sleeping!'
  telegram: '[%playername%]: %message%'
  chat: '§7[§bTelegram§7] §f[%user%] %message%'
  time: 'Time is %time% %emoji%\nDate is %month% %day%, Year %year%'
  online: 'There are %count% players online\n%players%'
```
