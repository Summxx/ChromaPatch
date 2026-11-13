<p align="center">
  <img src="docs/logo.png" alt="Chroma Patch" width="60%"/>
</p>

<p align="center">
  <img src="docs/screenshots/gtnh-1.png" alt="Chroma Patch preview" width="100%"/>
</p>

<p align="center">
  <a href="https://github.com/Summxx/ChromaPatch/releases">Latest release</a> |
  <a href="LICENSE.txt">License</a> |
  <a href="#roadmap">Roadmap</a> |
  <a href="#contributing">Contributing</a> |
  <a href="https://discord.gg/ZgXeQqePy3">Discord</a>
</p>

<div align="center">
  <a href="https://github.com/Summxx/ChromaPatch/releases">
    <img src="https://img.shields.io/github/v/release/Summxx/ChromaPatch?style=flat-square&color=blue" alt="Latest Release" />
  </a>
  <img src="https://img.shields.io/static/v1?label=Minecraft&message=1.7.10%20%7C%201.20.1%20%7C%201.21.1&style=flat-square&color=2ea44f" alt="Minecraft versions" />
  <img src="https://img.shields.io/static/v1?label=Loader&message=Forge%20%7C%20NeoForge&style=flat-square&color=orange" alt="Loaders" />
  <a href="https://discord.gg/ZgXeQqePy3">
    <img src="https://img.shields.io/static/v1?label=Discord&message=Join&style=flat-square&logo=discord&logoColor=white&color=5865F2" alt="Discord" />
  </a>
</div>

---

# About

Chroma Patch is the OBS07 edit of the **Eclipse** shader, distributed as a mod.

Edited versions of Eclipse cannot be redistributed, so the edit is applied locally. Eclipse is downloaded from its official page, and at startup the mod writes a patched copy next to it in `shaderpacks/`. The original zip is not modified.

The edit adds an atmosphere pass (lit night fog, godrays, dust, lamp light wells) and support for modded builds: microblocks, coloured lamps, GregTech glowing blocks, A Port Needed shapes and glass.

> [!NOTE]
> Chroma Patch is an unofficial edit, not affiliated with or endorsed by the Eclipse authors. **Eclipse is not included in the mod.** The patch is applied to the copy downloaded by the user.

### Credits

- **[Eclipse](https://github.com/Merlin1809/Eclipse-Shader)** by Merlin1809, based on Chocapic13's work.
- **Eclipse edit (KuraEdit)** by Kurama75, including the coloured candle light, Chisel antiblocks and GregTech coil colours.
- **OBS07** for the atmosphere, the modded block support and the mod.

---

# Installation

1. Install **Forge** (1.7.10 or 1.20.1) or **NeoForge** (1.21.1) with a shader mod: **Angelica** on GTNH, **Oculus** on Forge 1.20.1, **Iris** on NeoForge 1.21.1.
2. Put the **Chroma Patch** jar for your version in `mods/`. The 1.7.10 jar carries its own GTNH patch.
3. Download **Eclipse** from its [official GitHub page](https://github.com/Merlin1809/Eclipse-Shader) (Code > Download ZIP) and put the zip in `shaderpacks/`, without extracting it.
4. Start the game. **`Eclipse-Shader KuraEdit [OBS07-Chroma Patch].zip`** is created in `shaderpacks/`. On the first launch, a welcome page on the title screen shows whether the patch was applied and what it adds.
5. Select it in the shader menu, then a preset in the profile list (see [Presets](#presets)).
6. On GTNH, add KuramA's resource packs from [GTNH 2.9.X Shader Edits and PBR Packs](https://github.com/KuramA-777/GTNH-2.9.X-Shader-Edits-and-PBR-Packs). They add the neon emission and reflections that make GTNH look its best with the shader.

Patching takes a few seconds on the first launch and only runs again when the Eclipse zip or the patch changes. To update Eclipse, add the new zip to `shaderpacks/` and restart: the newest Eclipse zip is used.

---

# Features

### Lamp-lit night fog

Night fog takes the colour of nearby lamps and moves with the wind. Moonlight and building shadows are visible in it.

<img src="docs/screenshots/1.20.1-4.jpg" alt="Purple lamp in the night fog" width="100%"/>

### Neon glow

Neon blocks glow in their own colour and light the area around them: NeonCraft bars and letters on 1.20.1, Chisel neonite and froglight, Extra Utilities lightgems and other glowing decoration blocks on GTNH. The glow is saturated and reflects on wet ground and water. On GTNH, most neon blocks need the emission maps from [KuramA's resource packs](https://github.com/KuramA-777/GTNH-2.9.X-Shader-Edits-and-PBR-Packs).

<img src="docs/screenshots/gtnh-2.png" alt="Neon strips reflected in the water" width="100%"/>

### Coloured light from modded lamps

ProjectRed lamps, A Port Needed lamps and coloured candles emit light in their own colour. Blocks that glow only through a resource pack (neon strips, modded lamps) also light their surroundings.

<img src="docs/screenshots/1.20.1-2.png" alt="Lamp columns" width="100%"/>

### GregTech and microblocks

Glowing GregTech blocks in GTNH (coils, machines) emit the matching colour. CodeChicken microblocks have correct face shading, and lit microblocks emit light.

On GTNH, about 1600 more mod blocks are supported: leaves and plants wave, mod glass is tinted, mod lamps, torches and candles give coloured light, partial blocks like slabs and cables let light through, and Thaumcraft jars show their content.

### Rain, godrays and dust

Rain only wets surfaces exposed to the sky. Godrays appear at sunrise and sunset, tinted by stained glass, with dust in the light.

<img src="docs/screenshots/1.20.1-3.png" alt="Foggy rain" width="100%"/>

### Lamp light wells and glass

Recessed microblock lamps add a beam or a glow in the fog above them. A Port Needed shapes let coloured light through, its tinted glass behaves like stained glass, and dark glass lets some sunlight through.

<img src="docs/screenshots/1.20.1-1.png" alt="Light strips in the rain" width="100%"/>

All new settings are in the **Atmosphere** page of the shader options. Each effect can be reduced or disabled. Since 1.0.3 the night fog, godrays, dust and light wells are off by default for the frame rate, the **Atmosphere** preset turns them on.

### Presets

| Preset | Description |
| --- | --- |
| **OBS07 Ultimate** | Full quality, all settings at their defaults, used on a fresh install. Base Eclipse look with the OBS07 block and light support. Also restores the base settings after using another preset. |
| **OBS07 Ultimate Vanilla** | Ultimate with only the base Eclipse effects, for the best frame rate. Modded and neon light support stays. |
| **OBS07 Atmosphere** | Ultimate with the OBS07 night fog, godrays, dust and lamp light wells |
| **OBS07 Normal** | Reduced clouds, shadows and reflections |
| **OBS07 Normal (No Fog)** | Normal without night fog and light wells |
| **OBS07 Performance** | For lower-end GPUs and large bases |
| **OBS07 Performance (No Fog)** | Performance without night fog and light wells |
| **KuraBalanced** | KuramA's settings: balanced quality, without the OBS07 atmosphere effects |

---

# Requirements

## Minecraft

| Version | Loader | Shader mod |
| --- | --- | --- |
| 1.7.10 (GT New Horizons) | Forge | Angelica |
| 1.20.1 | Forge | Oculus |
| 1.21.1 | NeoForge | Iris |

## Shader

The official **Eclipse** zip. The patch targets Eclipse Unstable 3c18afc (September 2026) and tolerates small changes in newer ones. When a version differs too much, no pack is written and a `.log` file lists the parts that could not be applied.

---

# Updates

New patches and patch updates come with new versions of the mod. To update, download the new jar from the official pages and replace the old one in `mods/`.

On first launch the mod asks for permission to check GitHub for new versions. If refused, the mod never connects to the network. If allowed, the title screen shows a notice when a new version exists, with its changelog. OK opens the release page (after a confirmation), and the notice can be skipped for that version. Nothing is downloaded or installed by the mod.

Setting: `updates=ask|on|off` in `config/chromapatch.properties`.

## Security

- The mod never downloads code, patch data or jars. The only files read online are a version notice and its signature.
- The notice is signed and verified against the public key included in the mod. A modified notice is rejected, whether it was changed in transit or on GitHub. Older notices are rejected.
- The only host contacted is `raw.githubusercontent.com` (this repository), over HTTPS, and only after permission. No user data is sent.
- Patch data is included in the jar and can only write shader files inside the patched pack in `shaderpacks/`. Worlds, mods and the Eclipse zip are not modified.
- Malformed or oversized files are rejected without stopping the game. This applies to every zip in `shaderpacks/` (size limits on all reads, no partial pack written).
- Output file names are restricted to plain names, including names reserved by Windows.
- GitHub Actions only uses official actions pinned to a fixed commit, with read-only access except for publishing releases.

The source code is public. Release jars are built from it by GitHub Actions, with their hashes in `SHA256SUMS`. Download the mod from the official pages only.

---

# Roadmap

### Automatic light block detection
An experimental option to find light-emitting blocks that are missing from the block lists and give them a colour from their texture, so lamps from other modpacks emit coloured light too. Hand-made lists stay the reference and always take priority. Animated and emissive blocks need more work before this is usable, so it may change a lot or not ship. 1.20.1 and 1.21.1 first.

### More shaders
Patches for other shaders, using the same method: the shader is downloaded from its authors and the mod applies the changes. New patches come with new versions of the mod.
- **Complementary**
- **Photon**

### Patch maintenance
Support for new Eclipse versions, fixes and improvements to the existing effects, delivered with new versions of the mod.

---

# Known Issues

* A much newer Eclipse version may not be supported until a new version of the mod. The `.log` file in `shaderpacks/` lists the parts that failed.
* On Windows the previous patched pack can stay locked while the game runs. Restart the game if the log reports it.
* Eclipse packs that already contain the OBS07 edit are skipped.

---

# Contributing

Missing glowing blocks or wrong colours can be added with a pull request to [`contrib/eclipse-blocks.txt`](contrib/eclipse-blocks.txt). Add a comment line with your name and what the edit adds, then the block ids sorted by colour, or by exact Eclipse id when a block looks better on a specific one:

```
# YourName: Chisel antiblocks and a GregTech coil
white: chisel:antiBlock:0 chisel:antiBlock:8
orange: chisel:antiBlock:1
block.266: gregtech:gt.blockcasings5:21
```

Accepted additions are included in the patch of the next release, and contributors are credited. Eclipse files cannot be posted, so only block ids go in pull requests. For other changes, open an issue or ask on [Discord](https://discord.gg/ZgXeQqePy3).

By opening a pull request, you agree that your contribution is included in Chroma Patch under its [license](LICENSE.txt).

---

# 1.0.3 on GTNH

| OBS07 Ultimate | OBS07 Ultimate Vanilla |
| --- | --- |
| <img src="docs/screenshots/gtnh-1.0.3-ultimate-day.jpg" alt="Ultimate by day" width="100%"/> | <img src="docs/screenshots/gtnh-1.0.3-vanilla-day.jpg" alt="Ultimate Vanilla by day" width="100%"/> |
| <img src="docs/screenshots/gtnh-1.0.3-ultimate-night.jpg" alt="Ultimate at night" width="100%"/> | <img src="docs/screenshots/gtnh-1.0.3-vanilla-night.jpg" alt="Ultimate Vanilla at night" width="100%"/> |

Thaumcraft jars show their essentia again, with clearer glass.

<img src="docs/screenshots/gtnh-1.0.3-jars.jpg" alt="Thaumcraft jars" width="100%"/>

The light of a GregTech super tank no longer blinks with its animated screen.

<img src="docs/screenshots/gtnh-1.0.3-tank.jpg" alt="Super tank in a cave" width="100%"/>

Et Futurum lanterns light their surroundings, also in hand.

<img src="docs/screenshots/gtnh-1.0.3-lantern.jpg" alt="Lantern in a cave" width="100%"/>

---

# Changelog

The same changelog is shown in game: once after each new version, and in the new version notice.

<!-- changelog -->

### Chroma Patch 1.0.3
- GTNH: GregTech multiblock effects (space elevator, black hole, Godforge...) stay in place with shader packs that render at a lower resolution (FSR)
- GTNH: text on signs and Nuclear Control panels stays on its block with FSR too (font antialiasing is turned off while the world is drawn)
- The 1.7.10 jar now has its own GTNH patch, so GTNH changes no longer touch the 1.20.1 and 1.21.1 packs
- Includes Eclipse GTNH patch 1.0.3 (1.7.10) and Eclipse patch 1.0.3 (1.20.1, 1.21.1)

### Eclipse GTNH patch 1.0.3
- About 1600 more GTNH blocks supported: mod leaves and plants wave, mod glass (GregTech, BartWorks, Chisel, Tinkers...) is tinted like stained glass, mod lamps, torches, candles, lanterns, glowing flowers, crystals, fires and portals give light in their colour
- Slabs, stairs, fences, cables, pipes, rails and other partial blocks from mods no longer block coloured light
- Light sources the shader cannot see (invisible light blocks, Nitor, Thaumic Tinkerer gases, fireflies) keep their vanilla light, option Keep Vanilla Block Light
- Lanterns, soul torches, end rods and mod torches and candles light up when held in either hand
- Lanterns and torches worn on the belt light up when Dynamic Lights Shader Force is on in Angelica's video settings
- Thaumcraft jars and jars from other mods show their essentia and brains again, and their glass is clearer
- Glow berries, Biomes O' Plenty glowing coral and Twilight Forest firefly jars give coloured light
- Botania mana glass and elven glass are tinted glass
- Glowing slabs and stairs from resource packs light their surroundings
- White lamps are capped so they no longer drown coloured neon from far away
- The base Eclipse look is back by default: night light fog, fog drift, godrays, dust and lamp light wells are off in Ultimate, about twice the frame rate in our tests
- New preset Atmosphere: our night fog, godrays, dust and light wells, light fog at night only
- New preset Ultimate Vanilla: only the base Eclipse effects (no coloured translucent shadows, no glass light) for the best frame rate, modded and neon light support stays
- Lamp light wells are skipped by day, faster with Atmosphere
- Brighter nights: stronger moonlight and outdoor minimum light
- Less fog in Ultimate (fog density 0.85)
- Floodfill size 8 by default and in Ultimate, coloured light reaches further
- New option Night Vision Strength, night vision is no longer barely visible (also with fixed exposure presets like KuraBalanced)
- New option Far Lights Glow (Distant Horizons), off by default: far lights no longer show as strong white blobs
- Animated glowing textures (GregTech tank screens) no longer make their light blink
- Glowing microblocks and shapes light 80% as far as a full lamp (was 60%)
- Every preset now also resets coloured translucent shadows and glass light
- The patch version is shown at the top of the shader options, under the Eclipse version

### Eclipse patch 1.0.3
- The base Eclipse look is back by default: night light fog, fog drift, godrays, dust and lamp light wells are off in Ultimate, about twice the frame rate in our tests
- New preset Atmosphere: our night fog, godrays, dust and light wells, light fog at night only
- New preset Ultimate Vanilla: only the base Eclipse effects (no coloured translucent shadows, no glass light) for the best frame rate, modded and neon light support stays
- Lamp light wells are skipped by day, faster with Atmosphere
- Brighter nights: stronger moonlight and outdoor minimum light
- Less fog in Ultimate (fog density 0.85)
- Floodfill size 8 by default and in Ultimate, coloured light reaches further
- New option Night Vision Strength, night vision is no longer barely visible (also with fixed exposure presets like KuraBalanced)
- New option Far Lights Glow (Distant Horizons), off by default: far lights no longer show as strong white blobs
- Animated glowing textures (GregTech tank screens) no longer make their light blink
- Glowing microblocks and shapes light 80% as far as a full lamp (was 60%)
- Every preset now also resets coloured translucent shadows and glass light
- The patch version is shown at the top of the shader options, under the Eclipse version

### Chroma Patch 1.0.2
- GTNH: glowing blocks, microblocks and lamps light their surroundings again (Angelica ignored their ids since 1.0.1)
- Coloured lamps now light in their own colour without a preset (they were orange)
- Lamp light wells no longer flicker and are softer in dark places
- New GTNH glowing blocks by KuramA, glowing microblocks and ArchitectureCraft shapes
- Includes Eclipse patch 1.0.2

### Eclipse patch 1.0.2
- GTNH: block ids work again with Angelica, most GTNH lamps gave no light since 1.0.1
- Coloured candle light is on by default and in every preset
- GTNH glowing blocks by KuramA: Thaumcraft, AE2 crafting units and controller, Galacticraft glowstone torch, Extra Utilities and ArchitectureCraft shapes
- GTNH: ProjectRed lamps light in their own colour, white lamps glow without drowning other colours from far away
- Glowing microblocks and ArchitectureCraft shapes glow like their full block and light their surroundings, a bit less far than a full lamp
- ArchitectureCraft glow shapes give white light, Ztones Lave no longer glows
- ArchitectureCraft slopes are shaded correctly
- Lamp light wells: no more flickering, they pass through slopes, slabs and covers, no well under ceiling panels
- Glowing Blocks Give Light: machines with several glowing faces no longer flicker between colours
- GTNH: Storage Drawers items are no longer dark, no more puddles indoors

### Chroma Patch 1.0.1
- Fixed a crash when selecting the patched pack on Forge 1.20.1 and NeoForge 1.21.1
- New preset: KuraBalanced by KuramA
- Includes Eclipse patch 1.0.1
- Fixed jar builds depending on the JDK version

### Eclipse patch 1.0.1
- Fixed a crash when selecting the patched pack on Forge 1.20.1 and NeoForge 1.21.1 (GTNH block ids are now only used on 1.7.10)
- New preset: KuraBalanced by KuramA
- Presets now reset the Atmosphere settings, so switching presets no longer keeps values from the previous one

### Chroma Patch 1.0.0
- First release for Forge 1.7.10, Forge 1.20.1 and NeoForge 1.21.1
- Applies the OBS07 edit to the official Eclipse zip in shaderpacks
- Optional new version notice, disabled until allowed in game. The mod never downloads anything else
- Changelog shown in game after each new version
- Welcome page on first launch: patch result, what the patch adds, Discord link

### Eclipse patch 1.0.0
- Atmosphere page: lamp-lit night fog, godrays, dust, lamp light wells, glass light
- Neon blocks glow in their colour and light their surroundings
- Coloured light for ProjectRed, A Port Needed and coloured candle lamps
- GTNH glowing blocks sorted by colour, including Chisel antiblocks and GregTech coils
- CodeChicken microblock shading and light
- A Port Needed shapes let coloured light through, tinted glass works like stained glass
- Rain only wets surfaces exposed to the sky
- Presets: OBS07 Ultimate, Normal and Performance, with or without fog

<!-- /changelog -->

---

# License

All Rights Reserved, see [LICENSE.txt](LICENSE.txt). The source code is public for transparency.

Building and verifying a release: [MAINTAINING.md](MAINTAINING.md).
