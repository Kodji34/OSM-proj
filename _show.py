import pathlib 
txt = pathlib.Path('src/main/resources/templates/dashboard.html').read_text(encoding='utf-8').splitlines() 
print('\n'.join(f'{i+1}: {line}' for i,line in enumerate(txt) if 'filters-panel' in line))
