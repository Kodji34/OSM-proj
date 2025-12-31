f='src/main/resources/templates/tenant-app.html'  
lines=open(f,encoding='utf-8').read().splitlines()  
o=open('tmp_hits.txt','w',encoding='utf-8')  
[o.write(str(i+1)+': '+line+'\n') for i,line in enumerate(lines) if 'addEventListener' in line]  
o.close()  
