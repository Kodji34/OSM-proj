import re  
path=r'C:/Users/hp/.vscode/appOSMJava/src/main/java/com/example/school/controller/TenantSiteController.java'  
text=open(path,'r',encoding='utf-8',errors='ignore').read()  
m=re.search(r'@PostMapping\(\"/tenant/dashboard/users\"\)[\s\S]*?@PostMapping\(\"/tenant/dashboard/users/reset\"\)',text)  
open(r'C:/Users/hp/.vscode/appOSMJava/_snippet.txt','w',encoding='utf-8').write(m.group(0) if m else 'NOT_FOUND')  
