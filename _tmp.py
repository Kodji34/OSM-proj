# coding: cp1252 
from pathlib import Path 
lines=Path(r'src/main/resources/templates/tenant-app.html').read_text(encoding='utf-8', errors='replace').splitlines() 
for i,l in enumerate(lines,1): 
    if 'icon-btn' in l or 'action-icons' in l or 'pointer-events' in l: 
        print(i, l.encode('ascii','replace').decode()) 
