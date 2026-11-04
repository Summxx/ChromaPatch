<h1 align="center"><b>Chroma Patch: Maintaining</b></h1>

<p align="center">
  <a href="README.md">README</a> |
  <a href="LICENSE.txt">License</a>
</p>

---

# Building

Requires JDK 17. Gradle and Minecraft are not needed.

```sh
./build.sh          # writes dist/chromapatch-<version>-<loader>-<mc>.jar for the three loaders
```

The loader code is compiled against minimal stand-in classes in `stubs/`, which are not shipped.

Builds are reproducible: the same source produces the same jar bytes.

Release public keys are stored in `keys/<id>.pub` and embedded in the jar at build time. A build without a key has updates disabled.

---

# Patch format

Each shaderpack is a profile, `data/chromapatch/<id>.chroma`, listed in `data/chromapatch/profiles.properties`.

- Original Eclipse lines are stored only as 12-character SHA-1 hashes. Added lines are stored in full.
- Each block of changes is located by these hashes: exact match first, then ignoring indentation, then with reduced context (fuzz 2).
- `.properties` and `.lang` files fall back to key-by-key operations when a block cannot be placed.
- On any failure no pack is written, and `<output>.log` lists the failed parts.
- The patched zip contains `shaders/chroma_patch.properties` (patch version and SHA-1 of the source zip), so unchanged packs are skipped.

## `@info` header

| Key | Description |
| --- | --- |
| `id`, `version` | Profile name and patch version |
| `engine` | Required patch format level. Data with a higher level requires a newer jar |
| `base` | Eclipse version the patch was made against |
| `detect.file`, `detect.contains` | File and text used to recognise the shaderpack |
| `detect.excludes`, `detect.excludes.in` | Text marking an already patched pack, and the files to search |
| `output` | File name of the patched zip |

---

# Version notice

The only online file is a signed version notice in `updates/` (`manifest.properties` and `manifest.sig`). The mod never downloads patch data or jars.

1. Files are downloaded from `raw.githubusercontent.com/Summxx/ChromaPatch/main/updates/` only, over HTTPS, without redirects, with size limits (manifest 64 KB, signature 512 bytes), and only after the player allowed it.
2. `manifest.sig` (ECDSA P-256 / SHA-256) is verified against the keys in the jar before the manifest is parsed. The DER encoding is validated separately (CVE-2022-21449).
3. The manifest `serial` must not be lower than the highest one already accepted (`config/chromapatch/state.properties`).
4. `mod.version` and `mod.changelog` only produce a notice on the title screen. Any other entry is ignored.

`java -cp chromapatch-<version>-<loader>.jar obs07.chroma.update.Check updates` runs the same checks on the `updates/` folder.

## Untrusted zips

Every zip in `shaderpacks/` is read, including packs unrelated to Eclipse.

- A single text file read from a zip is limited to 16 MB, whatever size the zip declares.
- A patched pack is limited to 1 GB of uncompressed content.
- The marker file of an existing patched pack is limited to 64 KB.
- Unreadable or oversized zips are logged and skipped. A failed pack leaves no temporary file.
- Output names reject path characters, leading or trailing spaces and Windows reserved names (`CON`, `NUL`, `COM1`...).

## Testing

Before a release, the following cases are tested offline with a throwaway key:

- parser fuzzing (random mutations of a `.chroma` file): only clean rejections, no crash
- random, forged and zero signatures, signatures from another key
- modified notice, notice from another key, older notice (rollback), only the two notice files are requested
- zip bombs, `../` entry names, broken zips
- byte-for-byte comparison of the patched pack with the reference pack
