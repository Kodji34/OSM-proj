from pathlib import Path  
text = Path('src/main/resources/templates/tenant-app.html').read_text(encoding='utf-8', errors='replace').splitlines()
targets = ('openSubjectModal', 'renderSelectedSubjects', 'subjectModal', 'modal-overlay', 'icon-btn', 'action-icons')
matches = [i for i, line in enumerate(text) if any(t in line for t in targets)]
if not matches:
    print('No matches found.')
else:
    shown = set()
    for idx in matches:
        start = max(0, idx - 20)
        end = min(len(text), idx + 21)
        for i in range(start, end):
            if i in shown:
                continue
            shown.add(i)
            line = text[i].encode('ascii', 'backslashreplace').decode('ascii')
            print(str(i + 1) + ': ' + line)
