import pathlib 
t=pathlib.Path('src/main/java/com/example/school/controller/TenantSiteController.java').read_text(encoding='utf-8').splitlines() 
start=1800;end=1875 
for i in range(start-1,end): 
 l=t[i].encode('ascii','backslashreplace').decode('ascii') 
 print(str(i+1)+':'+l) 
