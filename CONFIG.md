# Config

The configuration file is located at `plugins/TelegramBridge/config.yml` and it's created when the plugin is loaded for the first time.

The default values are:

```yml
# Bot token from @BotFather
botToken: 'YOUR_BOT_TOKEN'

# Groups to bridge
chats:
  # Message ID of the pinned message (not meant to be edited from config file) (optional)
  - pinnedMessageId: null
    # Chat ID from @ScanIDBot
    id: 'YOUR_CHAT_ID'
    # Thread ID (not meant to be edited from config file) (optional)
    thread: null

# Pinned message that will be showed on telegram
pinned: 'Hey welcome to the chat!\nThere are %count% players online%players%'

# If messages should be sent to the chat
sendToChat: true
# If messages should be sent to the bot
sendToTelegram: true

# Log player join and leave in telegram chat
joinAndLeaveEvent: true
# Log player death in telegram chat
deathEvent: true
# Log player advancement in telegram chat
advancementEvent: true
# Log player sleep in telegram chat
sleepEvent: false
# Log when server starts and stops in telegram chat
serverStartStop: false
```
