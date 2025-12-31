import pathlib 
lines=pathlib.Path('src/main/resources/templates/tenant-dashboard.html').read_text(encoding='utf-8',errors='ignore').splitlines() 
start=470; end=760 
for i in range(start-1,end): 
	print('{:4d}: {}'.format(i+1, lines[i])) 
