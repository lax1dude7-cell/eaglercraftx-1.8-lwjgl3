import re,glob,os
os.chdir('/home/claude/pkg/src/main/java')
have=set(p[:-5].replace('/','.') for p in glob.glob('myau/**/*.java',recursive=True))
print('--- missing myau imports')
for p in glob.glob('myau/**/*.java',recursive=True):
    s=open(p).read()
    for m in re.finditer(r'^import (static )?(myau\.[\w.]+?)(\.\*)?;',s,re.M):
        imp=m.group(2)
        if m.group(3):
            if not any(h.startswith(imp+'.') for h in have): print(imp+'.*','<-',p.split('/')[-1])
        elif imp not in have: print(imp,'<-',p.split('/')[-1])
print('--- module classes referenced but absent')
mods={os.path.basename(p)[:-5] for p in glob.glob('myau/module/modules/*.java')}
refs={}
for p in glob.glob('myau/**/*.java',recursive=True):
    s=open(p).read()
    for m in re.finditer(r'\b([A-Z]\w+)\.class\b',s):
        n=m.group(1)
        if n not in mods and n not in have and not any(h.endswith('.'+n) for h in have): refs.setdefault(n,set()).add(os.path.basename(p))
for k,v in sorted(refs.items()):
    if k not in ('Class','Object','String'): print(k,sorted(v))
print('--- util members')
classes=['PlayerUtil','PacketUtil','KeyBindUtil','ItemUtil','MoveUtil','TeamUtil','RotationUtil','TimerUtil','RandomUtil','ChatUtil','BlockUtil','ServerUtil','RenderUtil','Acc','TextUtil','ColorUtil','Compat']
defs={}
for c in classes:
    s=open('myau/util/%s.java'%c).read()
    defs[c]=set(re.findall(r'public static [\w<>\[\], ?.]+ (\w+)\(',s))|set(re.findall(r'public static (?:final )?[\w<>\[\], ?.]+ (\w+)\s*[=;]',s))
bad={}
for p in glob.glob('myau/**/*.java',recursive=True):
    s=open(p).read()
    for c in classes:
        for m in re.finditer(r'\b'+c+r'\.(\w+)\b',s):
            n=m.group(1)
            if n not in defs[c] and n!='class' and n[0].islower() and n not in ('mc',): bad.setdefault((c,n),set()).add(p.split('/')[-1])
for k,v in sorted(bad.items()): print(k,sorted(v))
print('--- Myau./moduleManager./other managers')
mem=set(re.findall(r'public static [\w<>]+ (\w+)',open('myau/Myau.java').read()))
for p in glob.glob('myau/**/*.java',recursive=True):
    s=open(p).read()
    for m in re.finditer(r'\bMyau\.(\w+)',s):
        if m.group(1) not in mem: print('Myau.'+m.group(1),p.split('/')[-1])
