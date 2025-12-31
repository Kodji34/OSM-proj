import pathlib,re 
t=pathlib.Path('src/main/resources/templates/tenant-app.html').read_text(encoding='utf-8') 
for i,l in enumerate(t.splitlines(),1): 
 s=l.lower() 
 if 'subject' in s or 'mati' in s: 
  safe=l.encode('ascii','backslashreplace').decode('ascii') 
  print(str(i)+':'+safe) 
