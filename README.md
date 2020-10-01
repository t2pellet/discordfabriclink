[![Build Status](https://github.drone.dyonb.nl/api/badges/HeyItsMeNobody/discordfabriclink/status.svg)](https://github.drone.dyonb.nl/HeyItsMeNobody/discordfabriclink)

# Discord-Fabric link
Made possible by dzwdz <3

Discord-Fabric link is a mod that links your Minecraft chat and a Discord channel together.

There are multiple other mods like this, but what makes this one special, is that it uses **Discord webhooks** to send player messages, which will display their profile picture and name.

## Setup
First, you need to create a discord bot using the [Discord Developer Portal](https://discord.com/developers)
To do that, go to that website. The *Applications* tab on the left should automatically be selected. If it isn't, click it.

### Creating a Discord bot
Click the `New Application` button (should be at the top right) and a window will appear:
![Application Window](https://i.imgur.com/aU2jVX8.png)

In the text field, type a name for the application. I suggest something like `Discord Fabric Link` or `Minecraft Server`.
Click `Create`.

You will be brought to the Application page, on the left click on `Bot`:
![Discord Bot Application](https://i.imgur.com/jBCVPct.png)

You will be brought to a new section. Click on `Add Bot`, then click on `Yes, do it!`:
![Discord Add Bot](https://i.imgur.com/S3OS9b3.png)

From here, you can choose a username and icon for your bot!
![Discord Bot Settings](https://i.imgur.com/7iBnF4y.png)

**Keep this tab open in your browser, you will need it for later**.

### Installing the mod
Follow the guide at the [FabricMC Website](https://fabricmc.net/) to create a server and put the latest release of this mod (can be obtained from [here](https://github.com/HeyItsMeNobody/discordfabriclink/releases/latest)) in your server `mods` folder.

Next, start up the server, wait for it to fully start, and stop it.
The mod should generate a config file named `discordfabriclink.json5` inside the `config` folder. It will generate it after you launch the server with the mod installed for the first time.

### Configuring the mod
A file called `discordfabriclink.json5` should be created in a directory named `config` in your server folder, and it should look like this:
```json5
{ 
	// Bot token
	"discordBotToken": "",
	// Chatting channel ID
	"chatChannelId": "",
	// The message keys that will be sent to the public chat
	"publicKeys": [ 
		"death",
		"multiplayer",
		"chat.type.text",
		"chat.type.advancement",
		"chat.type.announcement"
	],
	// Log the keys
	"logKeys": false,
	// Webhook name
	"webhookName": "Discord Fabric Link",
	// The format to use in the Minecraft chat
	"minecraftChatFormat": "<%s> %s",
	// UUID face API
	"uuidFaceApi": "https://crafatar.com/avatars/%s?overlay&size=128"
}
```

Now, go to the bot application and click the `Copy` button:
![Discord Token Copy Button](https://i.imgur.com/HMDTYcV.png)

This will copy the bot token to your clipboard.
**Important: Do not share this token with ANYBODY.**
Once you copied the token, paste it into the `"discordBotToken": "<--HERE",` section. For example, if my token was `NzYxMjI1MjkzOTg5NDEyOTI0.X3XgTA.J0Eq5byFhx4YTMncaImgwcmLKpI`, the config file should now look like this:
```json5
{ 
	// Bot token
	"discordBotToken": "NzYxMjI1MjkzOTg5NDEyOTI0.X3XgTA.J0Eq5byFhx4YTMncaImgwcmLKpI",
	// Chatting channel ID
	"chatChannelId": "",
	// The message keys that will be sent to the public chat
	"publicKeys": [ 
		"death",
		"multiplayer",
		"chat.type.text",
		"chat.type.advancement",
		"chat.type.announcement"
	],
	// Log the keys
	"logKeys": false,
	// Webhook name
	"webhookName": "Discord Fabric Link",
	// The format to use in the Minecraft chat
	"minecraftChatFormat": "<%s> %s",
	// UUID face API
	"uuidFaceApi": "https://crafatar.com/avatars/%s?overlay&size=128"
}
```

Next, you need to [Enable Developer mode on Discord](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-).
Once you've done that, right click on the channel you want the messages to go in (in Discord) and select `Copy ID`.
Paste this ID into the `"chatChannelId": "<--HERE"` section, so if my channel ID was `123323247436436`, the config file would look like this:
```json5
{ 
	// Bot token
	"discordBotToken": "NzYxMjI1MjkzOTg5NDEyOTI0.X3XgTA.J0Eq5byFhx4YTMncaImgwcmLKpI",
	// Chatting channel ID
	"chatChannelId": "123323247436436",
	// The message keys that will be sent to the public chat
	"publicKeys": [ 
		"death",
		"multiplayer",
		"chat.type.text",
		"chat.type.advancement",
		"chat.type.announcement"
	],
	// Log the keys
	"logKeys": false,
	// Webhook name
	"webhookName": "Discord Fabric Link",
	// The format to use in the Minecraft chat
	"minecraftChatFormat": "<%s> %s",
	// UUID face API
	"uuidFaceApi": "https://crafatar.com/avatars/%s?overlay&size=128"
}
```

Finally, you can customize the `minecraftChatFormat`. For example, you can set it to `<%s from Discord> %s`.
That makes it so that when a user sends a message in that discord channel, on the server it would appear as `<USERNAME from Discord> MESSAGE`.

### Getting the bot into your server
On the bot application, go into the OAuth2 tab on the left, select the following options and click the `Copy` button:
![Discord Bot Perms](https://i.imgur.com/Wxt6J4z.png)

Open a new tab in your browser and paste the URL (*Note: You must have adminstrator permissions on the discord server you're trying to link*).
Select the correct Discord server, click `Next`, then `Authorize`, then verify you're not a bot.
The bot should join the discord server.

Start up the Minecraft server and everything should work!
