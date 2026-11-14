import re
D='/home/claude/pkg/src/main/java/myau/module/modules/'
miss=[]
def load(n): return open(D+n+'.java').read()
def save(n,s): open(D+n+'.java','w').write(s)
def R(n,old,new,regex=False):
    s=load(n)
    if regex:
        s2,c=re.subn(old,new,s,flags=re.S)
        if c==0: miss.append((n,old[:60]))
        s=s2
    else:
        if old not in s: miss.append((n,old[:60])); return
        s=s.replace(old,new)
    save(n,s)
def need(n,imp):
    s=load(n)
    if 'import '+imp+';' not in s: s=s.replace('\nimport ','\nimport '+imp+';\nimport ',1)
    save(n,s)

R('Eagle','String.format("%d-%d", this.minDelay.getValue(), this.maxDelay.getValue())','this.minDelay.getValue() + "-" + this.maxDelay.getValue()')
R('Jesus','private static final DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));\n','')
R('Jesus','import java.text.DecimalFormat;\n','')
R('Jesus','import java.text.DecimalFormatSymbols;\n','')
R('Jesus','df.format(this.speed.getValue())','TextUtil.fmt(this.speed.getValue(), 0, 2)')
need('Jesus','myau.util.TextUtil')
R('LongJump','String.format("%s%s: &cNo fireball found in your hotbar!&r", Myau.clientName, this.getName())','Myau.clientName + this.getName() + ": &cNo fireball found in your hotbar!&r"')
R('NoFall','String.format("%s%s: &cFailed player check!&r", Myau.clientName, this.getName())','Myau.clientName + this.getName() + ": &cFailed player check!&r"')
R('NoFall','String.format("%s%s: &cFailed void check!&r", Myau.clientName, this.getName())','Myau.clientName + this.getName() + ": &cFailed void check!&r"')
R('Timer','String.format("%.1fx", this.speed.getValue())','TextUtil.fmt(this.speed.getValue(), 1, 1) + "x"')
need('Timer','myau.util.TextUtil')
R('NoSlow','/*acc-local*/ Object accessor = mc.playerController;\n','')
R('NoSlow','accessor.getIsHittingBlock()','Acc.getIsHittingBlock(mc.playerController)')
R('Eagle','import org.apache.commons.lang3.RandomUtils;\n','')
R('Eagle','RandomUtils.nextInt(this.minDelay.getValue(), this.maxDelay.getValue() + 1)','RandomUtil.nextInt(this.minDelay.getValue(), this.maxDelay.getValue())')
need('Eagle','myau.util.RandomUtil')
print('MISSES:',miss)
