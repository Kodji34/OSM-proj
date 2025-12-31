import pathlib 
lines=pathlib.Path('src/main/resources/templates/tenant-public.html').read_text(encoding='utf-8',errors='ignore').splitlines() 
for i,l in enumerate(lines,1): 
	if 'logo {' in l: 
		for j in range(i-2, i+10): 
			print('{:4d}: {}'.format(j, lines[j-1])) 
