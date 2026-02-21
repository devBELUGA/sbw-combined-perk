# SBW Combined Perk

Addon mod for **Superb Warfare (SBW)** on Minecraft 1.20.1.

## Overview

This mod adds perk combination and split workflows for SBW:

- combine same-type perks into one **Combined Perk**
- combine different-type perks into a **Perk Folder**
- apply a **Perk Folder** to a gun in an anvil
- split a **Perk Folder** or **Combined Perk** back into separate perks
- apply a **Combined Perk** on the Reforging Table

## GIF Preview

### 1) Split Perk Folder

![Split Perk Folder](https://cdn.modrinth.com/data/BGK8soKH/images/eb11e000ff38d18090d3636445ff33696ba78f54.gif)

### 2) Split Combined Perk

![Split Combined Perk](https://cdn.modrinth.com/data/BGK8soKH/images/472d126f1bdbc9127652c208d37c2084bda97077.gif)

### 3) Combine Same-Type Perks

![Combine Same-Type Perks](https://cdn.modrinth.com/data/BGK8soKH/images/dcd2f5fada6b254746c259a74f9970624d3c0d88.gif)

### 4) Combine Different-Type Perks into Folder

![Combine Different-Type Perks into Folder](https://cdn.modrinth.com/data/BGK8soKH/images/350e5cb1d266c4b2f383935d32f0b10a102fa890.gif)

### 5) Apply Perk Folder to Gun (Anvil)

![Apply Perk Folder to Gun](https://cdn.modrinth.com/data/BGK8soKH/images/3b6b8bd860100a077e3aa49d8295762fe67e8d91.gif)

### 6) Apply Combined Perk on Reforging Table

![Apply Combined Perk on Reforging Table](https://cdn.modrinth.com/data/BGK8soKH/images/7f7da44e8cfd27624aaba5da0c48e9a89bf91d4e.gif)

## Gameplay Rules

### Combine in anvil

- `Slot 1` perk + `Slot 2` same-type perk -> **Combined Perk**
- `Slot 1` perk + `Slot 2` different-type perk -> **Perk Folder**

### Apply folder to gun in anvil

- `Slot 1` gun + `Slot 2` **Perk Folder** -> upgraded gun in output
- only **Perk Folder** can be applied to gun in anvil

### Split in anvil

- leave `Slot 1` empty
- put **Perk Folder** or **Combined Perk** into `Slot 2`
- output shows the main split perk
- use **Shift + LMB** on output to extract all split perks
- during split, the mod uses grindstone sound and returns the split XP cost

### Reforging Table

- **Combined Perk** is applied through the matching perk slot on the Reforging Table

## Requirements

- Minecraft `1.20.1`
- Forge `47.4.16` (only)
- Superb Warfare `0.8.8+`
- Geckolib `4.4.6+`
- Curios `5.4.0+`

## Install (Player)

1. Install Forge `47.4.16` for Minecraft `1.20.1`.
2. Put this mod jar into your `mods` folder.
3. Put required dependency mods into `mods` (`Superb Warfare`, `Geckolib`, `Curios`).

## Development

```bash
./gradlew runClient
```

## Build

```bash
./gradlew build
```

Built jars are written to `build/libs/`.

## License

This project is licensed under **GPL-3.0-only**. See `LICENSE`.
