import io  
path=r'C:/Users/hp/.vscode/appOSMJava/src/main/resources/templates/tenant-app.html'  
lines=open(path,'r',encoding='utf-8',errors='ignore').read().splitlines()  
i=788  
while i<806:  
  print(str(i+1)+':'+lines[i])  
  i+=1  
