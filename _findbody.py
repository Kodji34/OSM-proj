import io  
path=r'C:/Users/hp/.vscode/appOSMJava/src/main/resources/templates/tenant-app.html'  
lines=open(path,'r',encoding='utf-8',errors='ignore').read().splitlines()  
for i,l in enumerate(lines):  
  if '<body' in l: print(i+1, l)  
