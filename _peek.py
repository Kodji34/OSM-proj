import pathlib,sys 
sys.stdout.reconfigure(encoding='utf-8') 
lines=pathlib.Path('src/main/resources/templates/dashboard.html').read_text(encoding='utf-8',errors='replace').splitlines() 
print('\n'.join(f'{i+1}: {lines[i]}' for i in range(1310,1365)))
