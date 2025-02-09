[![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)
[![JDA](https://img.shields.io/badge/JDA-%235865F2?style=for-the-badge&logo=discord&logoColor=white)](https://jda.wiki/)
[![Java](https://img.shields.io/badge/java-21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://adoptium.net/)

# ServerSeekerV2 Discord Bot

This is the code for a Discord bot to communicate with the [ServerSeekerV2 API](https://git.funtimes909.xyz/ServerSeekerV2/ServerSeekerV2-PyAPI), if you are looking for the project that scans Minecraft servers and adds them to a database, the code is available [here](https://git.funtimes909.xyz/Funtimes909/ServerSeekerV2)

ServerSeekerV2-Discord-Bot is a Discord bot to fetch results about Minecraft servers from the ServerSeekerV2 API.

There is currently no public instance of this bot, you can ask for support hosting your own scanner and discord bot [here](https://matrix.to/#/#projects:funtimes909.xyz)

This is the official repository, a mirror of the repository can be found [here](https://github.com/Funtimes909/ServerSeekerV2-Discord-Bot) on GitHub where it is synced every few hours.
Pull requests will only be accepted here, not on GitHub.

## Commands

Below is a list of every currently implemented command and what it does

### /search

Searches for servers and return all that match specified parameters, supports combining advanced filters to precisely pinpoint exact servers.

**Example:**
/search country:NZ version:1.21.4 whitelist:false cracked:true software:paper

### /ping

Takes a server address and (optionally) a port as parameters, pings the requested server and displays it's status.

**Example:**
/ping address:192.168.1.1 port:25565

### /playerhistory

Displays playerhistory for either a server, or individual player.

**Example:**
/playerhistory player:Funtimes909

## Commands with no parameters

### /random

Returns a random server.

### /info

Displays useful information about thew bot, links to this GitHub repository.

### /stats

Shows stats about the discord bot, including unique players found, unique servers found and eventually more.

## Authenticated Commands

These are commands exclusive to "trusted users", users the owner of the bot has said can be trusted with potentially dangerous commands.

### /track

Receive Discord webhook updates on player activity across scanned servers

**Example:**
/track player:Funtimes909 webhook:<Webhook URL>

### /blacklist

Add/Remove a user from the blacklist, preventing them from using the bot

**Example:**
/blacklist operation:add user:@funtimes909

### /takedown

Prevent a server from being scanned in the future, also optionally delete all records of it from the database (default: false)

**Example:**
/takedown address:192.168.1.1 remove-entries:true

## Setup

Docker usage is always encouraged since it always provides the same enviroment

### Docker

Create a file named `ServerSeekerV2-Discord.json` following the format in [here](https://raw.githubusercontent.com/Funtimes909/ServerSeekerV2-Discord-Bot/refs/heads/main/config.json)

NOTE: You should run this in the directory that the scanner is in to that takedown actually works

Run:

```sh
docker run --mount type=bind,src=./ServerSeekerV2-Discord.json,dst=/usr/src/app/config.json --mount type=bind,src=./exclude.txt,dst=/usr/src/app/exclude.txt -d nucceteere/serverseekerv2-discord-bot
```

### Manual

<!-- TODO -->
