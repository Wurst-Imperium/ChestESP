**A Minecraft 1.21-compatible build of this mod is available to Early Access members.  
Join now to download this version!**

[![Download via Ko-fi](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/70d5fda1-46e7-465b-9424-cb9a32609ee5)](https://ko-fi.com/Post/Early-Access-ChestESP-Mod-for-Minecraft-1-21-X8X1ZOM62)

# ChestESP

This mod highlights nearby chests so you can see them through walls. It's based on a Wurst Client feature of the same name.

![A screenshot showing all of the different containers that ChestESP supports](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/5b77efdd-4a6b-49ea-8fed-1b1c18d13d7a)

## Downloads (for users)
[![Download ChestESP](https://wurst.wiki/_media/icon/chestesp/download_chestesp_326x80.png)](https://www.wimods.net/chestesp/download/?utm_source=GitHub&utm_medium=ChestESP&utm_campaign=README.md&utm_content=Download+ChestESP)

## Setup (for developers)
(This assumes that you are using Windows with [Eclipse](https://www.eclipse.org/downloads/) and [Java Development Kit 21](https://adoptium.net/?variant=openjdk21&jvmVariant=hotspot) already installed.)

1. Run this command in PowerShell:

   ```pwsh
   ./gradlew.bat genSources eclipse --no-daemon
   ```

2. In Eclipse, go to `Import...` > `Existing Projects into Workspace` and select this project.

## Features
- Highlights different types of chests and other containers with colored boxes, colored lines, or both
- See highlighted chests even when they are hidden behind other blocks
- Useful for finding hidden dungeons, buried treasure, and secret player bases
- Highly customizable with Cloth Config integration, allowing you to modify colors, styles, and which container types to show
- Supports a wide variety of container types, including chests, trapped chests, ender chests, chest minecarts, barrels, shulker boxes, hoppers, hopper minecarts, droppers, dispensers, furnaces, blast furnaces, and smokers
- Quickly toggle the entire mod with a keybind or through the settings menu
- Lightweight and optimized for performance

## Required Dependencies
ChestESP only works if the following other mods are also installed:
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [ModMenu](https://modrinth.com/mod/modmenu)
- [Cloth Config](https://modrinth.com/mod/cloth-config)

## How to Use
Once installed, ChestESP will automatically highlight any nearby chests.

The mod also comes with an in-game settings menu that can be accessed through ModMenu.

![A screenshot showing ChestESP's settings menu, powered by Cloth Config](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/3bb121ed-eb5d-49b1-ad62-3bcec3d6d488)

In the settings menu, you can:
- Change the style of the highlights (boxes, lines, or both).
- Customize the color of each container type.
- Toggle on/off specific container types.
- Enable/disable the entire mod.

Additionally, there's a "Toggle ChestESP" entry in the Options > Controls > Key Binds menu that allows you to quickly enable/disable the mod at the press of a button. This feature is not bound to any key by default.

## Supported Versions
- 1.21 Fabric ([early access](https://ko-fi.com/Post/Early-Access-ChestESP-Mod-for-Minecraft-1-21-X8X1ZOM62))
- 1.20.6 Fabric
- 1.20.5 Fabric
- 1.20.4 Fabric
- 1.20.3 Fabric
- 1.20.2 Fabric
- 1.20.1 Fabric
- 1.20 Fabric
- 1.19.4 Fabric
