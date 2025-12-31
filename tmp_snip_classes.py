import pathlib
import sys

lines = pathlib.Path("src/main/resources/templates/tenant-app.html").read_text(
    encoding="utf-8", errors="ignore"
).splitlines()

start = 840
end = 1150
for i in range(start - 1, end):
    line = f"{i+1}: {lines[i]}"
    sys.stdout.buffer.write(line.encode("utf-8", "ignore") + b"\n")
