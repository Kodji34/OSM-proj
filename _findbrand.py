import pathlib 
lines=pathlib.Path('src/main/resources/templates/tenant-public.html').read_text(encoding='utf-8',errors='ignore').splitlines() 
for i,l in enumerate(lines,1): 
	if 'brand' in l and 'align-items' in l: 
		print(i, l) 
