# Translation

You can change the language or format of the messages that are sent to the telegram chat by creating a
file `plugins/TelegramBridge/translations.yml`.

The default values are:

```yml
# Month names for the /time command
months: [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ]
# Media types for the type of media that is sent to the chat
mediaTypes: [ 'an image', 'a video', 'a document', 'an audio', 'a voice', 'a sticker', 'a contact', 'a location', 'a poll', 'a media' ]

# When a player joins the server
join: 'Player %playername% joined the game!'
# When a player leaves the server
leave: 'Player %playername% left the game!'
# When a player dies
death: 'Player %playername% died! %deathmessage%'
# When a player sleeps
sleep: 'Player %playername% went to bed!'

# When a minecraft player sends a message
chatMessage: '[%playername%]: %message%'
# When a telegram user sends a message
telegramMessage: '§7[§bTelegram§7] §f[%user%] %message%'
# When a telegram user sends a media
telegramMedia: '§7[§bTelegram§7] §f[%user%] sent %type%%caption%'
# When player or telegram user sends a reply
telegramReply: '- §breply to §7%user%: %message%§r -\n'

# /online command
online: 'There are %count% players online%players%'
# /time command
time: 'Time is %time% %emoji%\nDate is %month% %day%, Year %year%'
```
