This plugin adds a bridge between telegram and your minecraft sever making possible to send and receive messages from
telegram to minecraft and vice versa.

## Usage

- Install the plugin and restart your server this will create a config file for you to proceed
- Create a bot with [`@BotFather`](https://t.me/BotFather) on telegram and disable “Group Privacy” on bot settings
- Get your `chat id` from your group with the [`@ScanIDBot`](https://t.me/ScanIDBot)
- Now you can change the configuration file `plugins/TelegramBridge/config.yml` to put your `chat id` and `bot token`
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

To all the configuration options check [`CONFIG.md`](./CONFIG.md)

## Translation

To all the translation options check [`TRANSLATION.md`](./TRANSLATION.md)

## Telegram Commands

- `/setthread` - When using threads, this command will set witch thread the events will be sent to
- `/setpin` - Send a message to the chat and pin it which will show dynamically the online players (bot needs to have
  pin permission)
- `/unsetpin` - Unpin the message that was pinned by `/setpin` (bot needs to have pin permission)
- `/online` - Show the online players on the chat
- `/time` - Show the server time and date

## Minecraft Commands

- `/tbreload` - Reload the TelegramBridge configuration file
- `/tbmention <user> <message>` alias `/tbm` - Auto complete on users from telegram to mention them
