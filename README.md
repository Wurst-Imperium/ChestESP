# ChestESP

This mod highlights nearby chests so you can see them through walls. It's based on a Wurst Client feature of the same name.

![A screenshot showing all of the different containers that ChestESP supports](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/5b77efdd-4a6b-49ea-8fed-1b1c18d13d7a)

## Downloads (for users)
[![Download ChestESP](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/d1cdd39d-9bda-45cf-98bb-5c6a2ae01789)](https://www.wimods.net/chestesp/download/?utm_source=GitHub&utm_medium=ChestESP&utm_campaign=README.md&utm_content=Download+ChestESP)

## Setup (for developers)
(This assumes that you are using Windows with [Eclipse](https://www.eclipse.org/downloads/) and [Java Development Kit 17](https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot) already installed.)

1. Clone / download the repository.

2. Run these two commands in PowerShell:

   ```powershell
   ./gradlew.bat genSources
   ./gradlew.bat eclipse
   ```

3. In Eclipse, go to `Import...` > `Existing Projects into Workspace` and select this project.

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
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
- [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)
- [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

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
At the moment, ChestESP only supports Fabric and is only available for Minecraft 1.19.4. Support for Forge and older Minecraft versions might be added in the future, depending on demand.
