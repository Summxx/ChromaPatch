#!/bin/sh
# Builds the three loader jars into dist/
set -e
cd "$(dirname "$0")"
VERSION=$(cat mod.version)
# README changelog section follows CHANGELOG.md, and the mod changelog must exist
python3 -I tools/changelog.py sync
python3 -I tools/changelog.py section "Chroma Patch $VERSION" >/dev/null
# release public keys: keys/<id>.pub = base64 X.509
KEYS=""
for K in keys/*.pub; do
	[ -f "$K" ] || continue
	ID=$(basename "$K" .pub)
	KEYS="$KEYS${KEYS:+;}$ID=$(tr -d ' \n' < "$K")"
done
[ -n "$KEYS" ] || echo "warning: no keys/*.pub, updates are disabled in this build" >&2
mkdir -p build dist
find build -mindepth 1 -delete

for L in forge1710:1.7.10 forge1201:1.20.1 neoforge1211:1.21.1; do
	NAME=${L%%:*}; MC=${L#*:}
	OUT=build/$NAME; SRC=build/src-$NAME
	mkdir -p "$OUT" "$SRC" build/stubs-$NAME
	# compile-only stand-ins for the loader and Minecraft classes, never shipped
	javac -nowarn -Xlint:-options --release 8 -d build/stubs-$NAME $(find stubs/$NAME -name '*.java')
	cp -r core/. loaders/$NAME/. "$SRC"/
	find "$SRC" -name '*.java' -exec sed -i "s/@VERSION@/$VERSION/g" {} +
	[ -n "$KEYS" ] && sed -i "s|@KEYS@|$KEYS|" "$SRC/obs07/chroma/update/Trust.java"
	javac -nowarn -Xlint:-options --release 8 -cp build/stubs-$NAME -d "$OUT" $(find "$SRC" -name '*.java')
	# a loader can carry its own patch data (data-forge1710 = GTNH)
	DATA=data; [ -d "data-$NAME" ] && DATA="data-$NAME"
	cp -r "$DATA"/. jar/. meta/$NAME/. "$OUT"/
	cp CHANGELOG.md "$OUT"/chromapatch/CHANGELOG.md
	find "$OUT" \( -name '*.toml' -o -name '*.info' \) -exec sed -i "s/@VERSION@/$VERSION/g" {} +
	[ -f LICENSE.txt ] && cp LICENSE.txt "$OUT"/
	printf 'Manifest-Version: 1.0\nAutomatic-Module-Name: chromapatch\nImplementation-Version: %s\nCreated-By: Chroma Patch build.sh\n' "$VERSION" > build/manifest-$NAME
	JAR="dist/chromapatch-$VERSION-$NAME-$MC.jar"
	rm -f "$JAR"
	# fixed dates and sorted entries: same source, same jar bytes
	(cd "$OUT" && find . -type f | LC_ALL=C sort | sed 's|^\./||') > build/files-$NAME
	jar --create --date 2026-01-01T00:00:00Z --file "$JAR" --manifest build/manifest-$NAME $(sed "s|^|-C $OUT |" build/files-$NAME)
	echo "$JAR"
done
