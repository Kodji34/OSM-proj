from pathlib import Path 
text=Path('src/main/resources/templates/dashboard.html').read_text(encoding='utf-8',errors='ignore').splitlines() 
import sys 
for i,line in enumerate(text): 
    if 'th:remove=\"duplicate\"' in line: 
        print('{:4d}: {}'.format(i+1,line)) 
