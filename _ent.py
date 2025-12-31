import pathlib 
lines=pathlib.Path('src/main/java/com/example/school/entity/Establishment.java').read_text(encoding='utf-8',errors='ignore').splitlines() 
import sys 
for i,line in enumerate(lines,1): 
	if 'public' in line.lower() or 'logo' in line.lower(): 
		print('{:4d}: {}'.format(i, line)) 
