# Changelog

## Chroma Patch 1.0.3
- GTNH: GregTech multiblock effects (space elevator, black hole, Godforge...) stay in place with shader packs that render at a lower resolution (FSR)
- GTNH: text on signs and Nuclear Control panels stays on its block with FSR too (font antialiasing is turned off while the world is drawn)
- The 1.7.10 jar now has its own GTNH patch, so GTNH changes no longer touch the 1.20.1 and 1.21.1 packs
- Includes Eclipse GTNH patch 1.0.3 (1.7.10) and Eclipse patch 1.0.3 (1.20.1, 1.21.1)

## Eclipse GTNH patch 1.0.3
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

## Eclipse patch 1.0.3
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

## Chroma Patch 1.0.2
- GTNH: glowing blocks, microblocks and lamps light their surroundings again (Angelica ignored their ids since 1.0.1)
- Coloured lamps now light in their own colour without a preset (they were orange)
- Lamp light wells no longer flicker and are softer in dark places
- New GTNH glowing blocks by KuramA, glowing microblocks and ArchitectureCraft shapes
- Includes Eclipse patch 1.0.2

## Eclipse patch 1.0.2
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

## Chroma Patch 1.0.1
- Fixed a crash when selecting the patched pack on Forge 1.20.1 and NeoForge 1.21.1
- New preset: KuraBalanced by KuramA
- Includes Eclipse patch 1.0.1
- Fixed jar builds depending on the JDK version

## Eclipse patch 1.0.1
- Fixed a crash when selecting the patched pack on Forge 1.20.1 and NeoForge 1.21.1 (GTNH block ids are now only used on 1.7.10)
- New preset: KuraBalanced by KuramA
- Presets now reset the Atmosphere settings, so switching presets no longer keeps values from the previous one

## Chroma Patch 1.0.0
- First release for Forge 1.7.10, Forge 1.20.1 and NeoForge 1.21.1
- Applies the OBS07 edit to the official Eclipse zip in shaderpacks
- Optional new version notice, disabled until allowed in game. The mod never downloads anything else
- Changelog shown in game after each new version
- Welcome page on first launch: patch result, what the patch adds, Discord link

## Eclipse patch 1.0.0
- Atmosphere page: lamp-lit night fog, godrays, dust, lamp light wells, glass light
- Neon blocks glow in their colour and light their surroundings
- Coloured light for ProjectRed, A Port Needed and coloured candle lamps
- GTNH glowing blocks sorted by colour, including Chisel antiblocks and GregTech coils
- CodeChicken microblock shading and light
- A Port Needed shapes let coloured light through, tinted glass works like stained glass
- Rain only wets surfaces exposed to the sky
- Presets: OBS07 Ultimate, Normal and Performance, with or without fog
