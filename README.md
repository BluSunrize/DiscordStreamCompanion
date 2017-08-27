# DiscordStreamCompanion
This software is a rather basic Selfbot for Discord. This means that it's a bot running through your existing Discord client account.
[How to set it up](#setup)

## On the topic of Selfbots
There are some things to keep in mind when using this software:
Selfbots are a grey area to the Discord team. They are not forbidden, but if they cause problems, spam a lot or violate TOS you risk an *account ban*.
Anything you do with this bot, any trouble you cause and any consequences you receive are *solely on you*. *I am not responsible for your stupidity*. You can read the full Discord Terms of Service [here](https://discordapp.com/terms).
Also, some users may simply not like Selfbots. If you should encounter such users, stay friendly and respectful and either turn the bot off temporarily or at least stop using it in public.
Additionally, anyone wanting to get angry with me for making this, save your breath. I have worked in the Minecraft modding community, anything you can throw at me I've probably seen before.
All in all, respect Wheaton's Law and you should be fine.

## What it does
Currently, DSC's featureset is very limited. It was created in order to show the users in a voicechannel without needing to use the Streamkit (it's annoying and randomly joins you into channels when opening OBS >_>)
ATM, that's all it does:
 * Basic display of users in the currently active voicechannel
   * Configurable in Alignment (left/right), Colours, Avatars (y/n) and Channel Name (y/n)
   * Known issue: Won't highlight users speaking (no way to track that, due to lack of access to the clients AudioSocket, wontfix)
 * An Icon in the SystemTray that allows access to the basic commands of the bot
 * Commands that can be run in Discord and will then be deleted after (all start with "!dsc.")

## Requirements
* Java 8, because I use lambdas

## What it might do in future
This is mostly a hobby project / for shits and giggles, but I have some ideas what to do with it in the future.
* Set the "Playing..." display, to custom messages
  * possibly integration with Spotify, if I can figure it out
  * possibly integration with Mixer, if I can figure it out
* Livestream announcements, proclaiming a link to your stream

## Configuration
When starting the bot for the first time, it will open a config window which requires you to enter your Token. You may also set other options there.
This information is saved in a config file, located in
*../Users/[your user]/AppData/Roaming/DiscordStreamCompanion*
I may consider a custom datafolder for this in the future, to allow portable installs and stuff.
Currently, the png output of the Voicechannel display and the log files are also located there. This may become a config option in the future as well.

### Token
To get your Discord Client token for this bot do the following steps:
1. In your browser or Discord client on your PC, hold 'Ctrl-Shift-I'. That should bring up website debug stuffs on the right side.
2. Go to the **Application** tab at the top (may be hidden behind the '>>'' ) and navigate to **Storage** > **LocalStorage** > **discordapp.com**.
3. Find the key 'token', the value behind it is your client token. **KEEP THIS TO YOURSELF!** This token offers full access to your Discord account so don't share it with **anyone**. The configuration window of this bot specifically treats the Token field as a Password field and will obscure it. It is plaintext in the config however, so **be careful**.
4. Paste that token, without the surrounding quotation marks into the config window (or config file, if it already exists) of your DSC, and you should be good to go!

## Commands
Current commands that this bot knows: (all command messages are deleted after execution, to not leave spam in channels)
 * !dsc.ping : Makes the bot respond with "Pong" and a countdown timer of 3 seconds, after which the message is deleted
 * !dsc.config or !dsc.cfg : Brings up the bots configuration window
 * !dsc.shutdown : Stops the bot

## Bugs, Errors, Feature Suggestions
Bugs go on the issue tracker on this here Github page.
Feature Suggestions also go there, feel free to put [Suggestion] or something in the title to make it more obvious. I won't guarantee that I can or will add any of them. But I love brainstorming, so hit me up ^^

## License of the Code
It's Apache, you can do a lot with it, provided you give credit and stuff. I won't help you if you break it though, that's on you.