#!/usr/bin/env python3
import pathlib, urllib.request, time

ROOT = pathlib.Path(__file__).resolve().parents[1]
SOURCES = ROOT / "sources" / "sources.txt"
OUT = ROOT / "output" / "all.txt"

lines = set()
for raw in SOURCES.read_text(encoding="utf-8").splitlines():
    url = raw.strip()
    if not url or url.startswith("#"):
        continue
    try:
        req = urllib.request.Request(url, headers={"User-Agent": "AliVPN-Collector/0.1"})
        with urllib.request.urlopen(req, timeout=20) as r:
            text = r.read().decode("utf-8", errors="ignore")
        for line in text.splitlines():
            line = line.strip()
            if line and not line.startswith("#"):
                lines.add(line)
        print(f"OK: {url}")
    except Exception as exc:
        print(f"SKIP: {url} :: {exc}")

OUT.parent.mkdir(parents=True, exist_ok=True)
content = "\n".join(sorted(lines))
OUT.write_text((content + "\n") if content else "", encoding="utf-8")
print(f"Wrote {len(lines)} unique configs to {OUT}")
