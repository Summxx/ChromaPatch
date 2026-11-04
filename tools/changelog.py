# CHANGELOG.md is the only source of changelogs.
#   changelog.py section "<title>"   prints the lines of "## <title>" (exit 1 if missing)
#   changelog.py sync                copies CHANGELOG.md into the README changelog section
import pathlib, sys

root = pathlib.Path(__file__).resolve().parent.parent
text = (root / "CHANGELOG.md").read_text(encoding="utf-8")


def section(title):
    out, inside = [], False
    for line in text.split("\n"):
        if line.startswith("## "):
            inside = line[3:].strip() == title
            continue
        if inside and line.strip():
            out.append(line.rstrip())
    return out


if sys.argv[1] == "section":
    lines = section(sys.argv[2])
    if not lines:
        sys.exit(f'no "## {sys.argv[2]}" in CHANGELOG.md')
    print("\n".join(lines))
elif sys.argv[1] == "sync":
    readme = root / "README.md"
    r = readme.read_text(encoding="utf-8")
    start, end = "<!-- changelog -->", "<!-- /changelog -->"
    if start not in r or end not in r:
        sys.exit("README.md has no changelog markers")
    body = text.split("\n", 1)[1].strip().replace("## ", "### ")
    new = r[: r.index(start) + len(start)] + "\n\n" + body + "\n\n" + r[r.index(end):]
    if new != r:
        readme.write_text(new, encoding="utf-8")
        print("README changelog updated")
