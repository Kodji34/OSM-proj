import pathlib 
lines=pathlib.Path('src/main/resources/templates/tenant-dashboard.html').read_text(encoding='utf-8',errors='ignore').splitlines() 
import sys 
for i in range(1,120): 
	print('{:4d}: {}'.format(i, lines[i-1])) 
