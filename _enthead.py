import pathlib 
lines=pathlib.Path('src/main/java/com/example/school/entity/Establishment.java').read_text(encoding='utf-8',errors='ignore').splitlines() 
for i in range(1,60): 
	print('{:4d}: {}'.format(i, lines[i-1])) 
