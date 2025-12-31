import pathlib 
txt=pathlib.Path('src\\main\\resources\\templates\\dashboard.html').read_text(encoding='utf-8').splitlines() 
for i,line in enumerate(txt,1): 
