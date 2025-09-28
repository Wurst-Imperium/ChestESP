# ChestESP

This mod highlights nearby chests so you can see them through walls. It's based on a Wurst Client feature of the same name.

![A screenshot showing all of the different containers that ChestESP supports](https://img.wimods.net/github.com/Wurst-Imperium/ChestESP?to=https://images.wurstclient.net/_media/update/chestesp/chestesp_1.3_540p.webp)

## Downloads

[![Download ChestESP](https://wurst.wiki/_media/icon/chestesp/download_chestesp_326x80.png)](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://www.wimods.net/chestesp/download/?utm_source=GitHub&utm_medium=ChestESP&utm_campaign=README.md&utm_content=Download+ChestESP)

## Installation

> [!IMPORTANT]
> Always make sure that your modloader and all of your mods are made for the same Minecraft version. Your game will crash if you mix different versions.

### Installation using Fabric

1. Install [Fabric Loader](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://fabricmc.net/use/installer/).
2. Add [Fabric API](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://modrinth.com/mod/fabric-api) to your mods folder.
3. Add ChestESP to your mods folder.

> [!NOTE]
> Older ChestESP versions also required that you add [Cloth Config](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://modrinth.com/mod/cloth-config) and [ModMenu](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://modrinth.com/mod/modmenu) to your mods folder. As of ChestESP 1.2, you no longer need to do this.

### Installation using NeoForge

1. Install [NeoForge](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://neoforged.net/).
2. Add ChestESP to your mods folder.

## Usage

Once installed, ChestESP will automatically highlight any nearby chests.

The mod also comes with an in-game settings menu that can be accessed through ModMenu in Fabric or the built-in mod list in NeoForge.

![A screenshot showing ChestESP's settings menu, powered by Cloth Config](https://github.com/Wurst-Imperium/ChestESP/assets/10100202/3bb121ed-eb5d-49b1-ad62-3bcec3d6d488)

In the settings menu, you can:
- Change the style of the highlights (boxes, lines, or both).
- Customize the color of each container type.
- Toggle on/off specific container types.
- Enable/disable the entire mod.

There is also a "Toggle ChestESP" entry in the Options > Controls > Key Binds menu that allows you to quickly enable/disable the mod at the press of a button. This feature is not bound to any key by default.

## Supported containers

ChestESP supports the following container types:
- Chests
- Trapped chests
- Ender chests
- Barrels
- Shulker boxes
- Decorated pots
- Chest minecarts
- Chest boats
- Hopper minecarts
- Hoppers
- Droppers
- Dispensers
- Crafters
- Furnaces
- Blast furnaces
- Smokers

Not all of these containers are enabled by default to prevent cluttering your screen. Be sure to check the settings menu and enable all the ones you want.

## Supported languages

Only English for now.

## Development Setup

> [!IMPORTANT]
> Make sure you have [Java Development Kit 21](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://adoptium.net/?variant=openjdk21&jvmVariant=hotspot) installed. It won't work with other versions.

### Development using Eclipse

1. Clone the repository:

   ```pwsh
   git clone https://github.com/Wurst-Imperium/ChestESP.git
   cd ChestESP
   ```

2. Generate the sources:

   In Fabric versions:
   ```pwsh
   ./gradlew genSources eclipse
   ```

   In NeoForge versions:
   ```pwsh
   ./gradlew eclipse
   ```

3. In Eclipse, go to `Import...` > `Existing Projects into Workspace` and select this project.

4. **Optional:** Right-click on the project and select `Properties` > `Java Code Style`. Then under `Clean Up`, `Code Templates`, `Formatter`, import the respective files in the `codestyle` folder.

### Development using VSCode / Cursor

> [!TIP]
> You'll probably want to install the [Extension Pack for Java](https://go.wimods.net/from/github.com/Wurst-Imperium/ChestESP?to=https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) to make development easier.

1. Clone the repository:

   ```pwsh
   git clone https://github.com/Wurst-Imperium/ChestESP.git
   cd ChestESP
   ```

2. Generate the sources:

   In Fabric versions:
   ```pwsh
   ./gradlew genSources vscode
   ```

   In NeoForge versions:
   ```pwsh
   ./gradlew eclipse
   ```
   (That's not a typo. NeoForge doesn't have `vscode`, but `eclipse` works fine.)

3. Open the `ChestESP` folder in VSCode / Cursor.

4. **Optional:** In the VSCode settings, set `java.format.settings.url` to `https://raw.githubusercontent.com/Wurst-Imperium/ChestESP/master/codestyle/formatter.xml` and `java.format.settings.profile` to `Wurst-Imperium`.

### Development using IntelliJ IDEA

I don't use or recommend IntelliJ, but the commands to run would be:

```pwsh
git clone https://github.com/Wurst-Imperium/ChestESP.git
cd ChestESP
./gradlew genSources idea
```

**Note:** IntelliJ IDEA is not yet fully compatible with configuration cache (see <https://github.com/FabricMC/fabric-loom/issues/1349>). You might need to set `org.gradle.configuration-cache=false` in `gradle.properties`. If you do, please remember to change it back to `true` before committing!
