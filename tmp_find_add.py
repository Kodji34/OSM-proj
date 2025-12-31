from pathlib import Path

path = Path("src/main/resources/templates/tenant-app.html")
lines = path.read_text(encoding="utf-8", errors="ignore").splitlines()
target = "openSubjectModal"
for i, line in enumerate(lines, start=1):
    if target in line:
        print(f"{i}: {line}")
