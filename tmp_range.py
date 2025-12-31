f='src/main/resources/templates/tenant-app.html'
lines=open(f,encoding='utf-8', errors='replace').read().splitlines()
o=open('tmp_range.txt','w',encoding='utf-8')
start=2200
end=2345
for i in range(start-1, end):
    o.write(str(i+1)+': '+lines[i]+'\\n')
o.close()
