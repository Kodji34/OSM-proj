import pathlib,sys 
sys.stdout.reconfigure(encoding='utf-8') 
lines=pathlib.Path('src/main/resources/templates/dashboard.html').read_text(encoding='utf-8',errors='replace').splitlines() 
for i,l in enumerate(lines): 
    if 'Aucun etablissement selectionne pour le deploiement' in l: 
        for j in range(i-4,i+8): 
            print(f'{j+1}: {lines[j]}') 
        break
