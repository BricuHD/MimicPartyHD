# Mimic Party — Minecraft Java 1.21.11

A first playable starter version of a multiplayer party-game mod for Fabric.

## Included in v0.1
- `/mimicparty start` — start a party and reset scores (operator permission required).
- `/mimicparty next` — skip to the next challenge (operator permission required).
- `/mimicparty point <player>` — award one point (operator permission required).
- `/mimicparty scores` — show the current scoreboard.
- `/mimicparty stop` — end the party and show final scores (operator permission required).
- Five rotating party challenges; each round lasts 60 seconds.

## Requirements
- Minecraft Java Edition **1.21.11**
- Fabric Loader
- Fabric API for 1.21.11
- JDK 21

## Build
1. Open this folder in IntelliJ IDEA (or another Gradle Java IDE).
2. Let Gradle sync and download dependencies.
3. Run `gradle build` (or `./gradlew build` if you add the Gradle wrapper scripts).
4. Find the mod jar in `build/libs/`.
5. Put the remapped jar in your Minecraft `mods` folder alongside Fabric API.

## Play
Install the same mod and Fabric API on the server. An operator starts the game with `/mimicparty start`.
Players complete the announced challenge; the host awards points using `/mimicparty point PlayerName`.

This is an original fan-made project inspired by party-game mechanics. It is not affiliated with or a copy of the original Mimic Party game. The current build is a playable framework; custom menus, maps, sound packs and fully automated minigames can be added in later versions.

## Build without installing Gradle locally (GitHub Actions)
1. Create a new GitHub repository and upload the contents of this project.
2. Open the repository's **Actions** tab.
3. Select **Build Mimic Party** and click **Run workflow** (or push to `main`).
4. When the run finishes, open it and download the `MimicParty-1.21.11` artifact.
5. The downloaded artifact contains the built `.jar`; put that JAR in your Minecraft `mods` folder.

The source has not been compiled in this environment because external downloads are unavailable here. The workflow compiles it on GitHub's build runner and reports any build errors.
