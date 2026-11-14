import re, sys, os

SRC='/home/claude/omp/OpenMyau-Plus-main/src/main/java/myau/module/modules/'
DST='/home/claude/pkg/src/main/java/myau/module/modules/'

EV = {
 'TickEvent':('onTick','TICK'), 'UpdateEvent':('onUpdate','UPDATE'), 'PlayerUpdateEvent':('onPlayerUpdate','PLAYER_UPDATE'),
 'PostMotionEvent':('onPostMotion','POST_MOTION'), 'PacketEvent':('onPacket','PACKET'), 'AttackEvent':('onAttack','ATTACK'),
 'LeftClickMouseEvent':('onLeftClick','LEFT_CLICK'), 'RightClickMouseEvent':('onRightClick','RIGHT_CLICK'),
 'KeyEvent':('onKey','KEY'), 'MoveInputEvent':('onMoveInput','MOVE_INPUT'), 'StrafeEvent':('onStrafe','STRAFE'),
 'KnockbackEvent':('onKnockback','KNOCKBACK'), 'LivingUpdateEvent':('onLivingUpdate','LIVING_UPDATE'),
 'LoadWorldEvent':('onLoadWorld','LOAD_WORLD'), 'Render3DEvent':('onRender3D','RENDER_3D'), 'PickEvent':('onPick','PICK'),
 'RaytraceEvent':('onRaytrace','RAYTRACE'), 'HitBlockEvent':('onHitBlock','HIT_BLOCK'), 'CancelUseEvent':('onCancelUse','CANCEL_USE'),
 'SafeWalkEvent':('onSafeWalk','SAFE_WALK'), 'JumpEvent':('onJump','JUMP'), 'SwapItemEvent':('onSwapItem','SWAP_ITEM'),
 'WindowClickEvent':('onWindowClick','WINDOW_CLICK'), 'Render2DEvent':('onRender2D','RENDER_2D'), 'ResizeEvent':('onResize','RESIZE'), 'RenderLivingEvent':('onRenderLiving','RENDER_LIVING'),
}
PRIO={'HIGHEST':0,'HIGH':1,'MEDIUM':2,'LOW':3,'LOWEST':4}

def match_paren(s,i,open_='(',close=')'):
    depth=0;k=i
    while k<len(s):
        c=s[k]
        if c=='"':
            k+=1
            while s[k]!='"':
                if s[k]=='\\':k+=1
                k+=1
        elif c=="'":
            k+=1
            while s[k]!="'":
                if s[k]=='\\':k+=1
                k+=1
        elif c==open_:depth+=1
        elif c==close:
            depth-=1
            if depth==0:return k
        k+=1
    raise Exception('unbalanced')

def port(name):
    s=open(SRC+name+'.java').read()
    flags=[]
    # ---- imports
    s=re.sub(r'import myau\.event\.EventTarget;\n','',s)
    s=re.sub(r'import java\.awt\.\*;\n','import myau.util.Color;\n',s)
    s=re.sub(r'import java\.awt\.Color;\n','import myau.util.Color;\n',s)
    s=re.sub(r'import myau\.mixin\.IAccessor\w+;\n','',s)
    s=re.sub(r'import lombok\.\w+;\n','',s)
    s=re.sub(r'import org\.lwjgl\.util\.Color;\n','import myau.util.Color;\n',s)
    if 'Acc.' not in s: pass
    # ---- this.X::getValue conditions
    s=re.sub(r'this\.(\w+)::getValue',lambda m:'new Condition() { public boolean test() { return '+name+'.this.'+m.group(1)+'.getValue(); } }',s)
    s=re.sub(r'(?<=, )(?<![\w.])([a-z]\w*)::getValue\)',lambda m:'new Condition() { public boolean test() { return '+name+'.this.'+m.group(1)+'.getValue(); } })',s)
    # ---- CaseFormat / queues / GL11
    s=re.sub(r'CaseFormat\.UPPER_UNDERSCORE\.to\(CaseFormat\.UPPER_CAMEL, ([^;]*?)\)\}',lambda m:'TextUtil.camel('+m.group(1)+')}',s)
    s=s.replace('import com.google.common.base.CaseFormat;\n','import myau.util.TextUtil;\n')
    s=s.replace('import java.util.concurrent.ConcurrentLinkedQueue;','import java.util.LinkedList;').replace('ConcurrentLinkedQueue','LinkedList')
    s=re.sub(r'\n\s*import org\.lwjgl\.opengl\.GL11;','',s)
    s=re.sub(r'\n\s*GL11\.glLineWidth\([^)]*\);','',s)
    # ---- accessors ((IAccessorX) EXPR).method(args)
    while True:
        i=s.find('((IAccessor')
        if i<0:break
        j=s.index(')',i+2)                    # end of cast
        outer_end=match_paren(s,i)            # matches the outer '('
        expr=s[j+1:outer_end].strip()
        rest=s[outer_end+1:]
        m=re.match(r'\s*\.(\w+)\(',rest)
        if not m: flags.append('accessor-nomatch'); s=s[:i]+'/*ACC?*/'+s[i+2:]; continue
        meth=m.group(1)
        po=outer_end+1+m.end()-1
        pc=match_paren(s,po)
        args=s[po+1:pc].strip()
        s=s[:i]+'Acc.'+meth+'('+expr+(', '+args if args else '')+')'+s[pc+1:]
        if 'import myau.util.Acc;' not in s:
            s=s.replace('\nimport ','\nimport myau.util.Acc;\nimport ',1)
    # ---- visibility lambdas: ', () -> expr)' inside property constructors
    cls=name
    while True:
        i=s.find('() ->')
        if i<0:break
        # is it inside a "new XxxProperty(" call?  find the nearest preceding 'new \w*Property(' before i without crossing ';'
        k=s.rfind(';',0,i)
        seg=s[k+1:i]
        if not re.search(r'new \w*Property\(',seg):
            flags.append('lambda@'+s[max(0,i-60):i+40].replace('\n',' '))
            s=s[:i]+'()#->'+s[i+5:]
            continue
        # expression runs until the ')' closing the enclosing call
        depth=0;e=i+5
        while True:
            c=s[e]
            if c=='"':
                e+=1
                while s[e]!='"':
                    if s[e]=='\\':e+=1
                    e+=1
            elif c=='(':depth+=1
            elif c==')':
                if depth==0:break
                depth-=1
            e+=1
        expr=s[i+5:e].strip()
        expr=re.sub(r'(?<![\w.])this\.',cls+'.this.',expr)
        if expr.startswith('{'):
            flags.append('block-lambda'); s=s[:i]+'()#->'+s[i+5:]; continue
        new='new Condition() { public boolean test() { return '+expr+'; } }'
        s=s[:i]+new+s[e:]
    s=s.replace('()#->','() ->')
    if 'new Condition()' in s and 'import myau.property.Condition;' not in s:
        s=s.replace('\nimport ','\nimport myau.property.Condition;\nimport ',1)
    # ---- event handlers
    handlers=[]
    pat=re.compile(r'@EventTarget(?:\(Priority\.(\w+)\))?\s*public void (\w+)\((\w+) (\w+)\)')
    def rep(m):
        prio,mname,et,var=m.groups()
        if et not in EV:
            flags.append('unknown event '+et); return m.group(0)
        canon,kind=EV[et]
        handlers.append((kind,prio))
        return '@Override\n    public void %s(%s %s)'%(canon,et,var)
    s=pat.sub(rep,s)
    if '@EventTarget' in s: flags.append('EventTarget left')
    kinds=[k for k,_ in handlers]
    for k in set(kinds):
        if kinds.count(k)>1: flags.append('DUPLICATE handler kind '+k)
    # ---- constructor: register settings + priorities
    props=re.findall(r'(?m)^\s*(?:public|private|protected)?\s*(?:final\s+)?(?:\w*Property)\s+(\w+)\s*(?:=|;)',s)
    cm=re.search(r'public '+name+r'\(\)\s*\{',s)
    if not cm:
        flags.append('NO CONSTRUCTOR')
    else:
        end=match_paren(s,cm.end()-1,'{','}')
        add=''
        if props:
            add+='\n        '+'\n        '.join('add(%s);'%p for p in props)
        for kind,prio in handlers:
            if prio and PRIO[prio]!=2:
                add+='\n        setPriority(myau.module.Kinds.%s, %d);'%(kind,PRIO[prio])
        s=s[:end]+add+'\n    '+s[end:]
    nprop=len(re.findall(r'new \w*Property\(',s))
    if nprop!=len(props): flags.append('property count mismatch: new=%d fields=%d'%(nprop,len(props)))
    # ---- shared rewrites (all categories)
    s=re.sub(r'([\w.]+)\.getValue\(\)\.toString\(\)',r'String.valueOf(\1.getValue())',s)
    s=re.sub(r'IAccessor(\w+) (\w+) = \(IAccessor\w+\) ([\w.]+);',lambda m:'/*acc-local*/ Object '+m.group(2)+' = '+m.group(3)+';',s)
    s=re.sub(r'IAccessorRenderManager (\w+) = \(IAccessorRenderManager\) mc\.getRenderManager\(\);',r'net.minecraft.client.renderer.entity.RenderManager \1 = mc.getRenderManager();',s)
    s=re.sub(r'Acc\.getRenderPos([XYZ])\(([^()]*(?:\([^()]*\))?[^()]*)\)',r'\2.viewerPos\1',s)
    s=s.replace('.getRenderPosX()','.viewerPosX').replace('.getRenderPosY()','.viewerPosY').replace('.getRenderPosZ()','.viewerPosZ')
    s=re.sub(r'import org\.lwjgl\.input\.(Keyboard|Mouse);\n','',s)
    s=s.replace('Keyboard.isKeyDown(','myau.util.Compat.isKeyDown(').replace('Mouse.isButtonDown(','myau.util.Compat.isMouseDown(')
    # ---- boxed value calls
    s=re.sub(r'([\w.]+)\.getValue\(\)\.(double|float|int|long)Value\(\)',lambda m:'((%s) %s.getValue())'%(m.group(2),m.group(1)),s)
    s=s.replace('modules.get(','modules.get(')
    # ---- flags for leftovers
    for pat_,label in [(r'->','arrow'),(r'stream\(|Collectors|::','stream/methodref'),(r'\bRobot\b|AWTException','Robot'),(r'GL11|GL12','GL11'),
                       (r'\bMouse\.|\bKeyboard\.','LWJGL input'),(r'DecimalFormat','DecimalFormat'),(r'CaseFormat','CaseFormat'),(r'@Getter','lombok'),
                       (r'String\.format','String.format'),(r'IAccessor','IAccessor'),(r'net\.minecraftforge','forge'),(r'ConcurrentLinked','concurrent'),
                       (r'Supplier<','Supplier'),(r'\bLoader\.','forge Loader'),(r'Myau\.\w+Manager\.modules\.(values|entrySet|keySet)','modules iter')]:
        for m in re.finditer(pat_,s):
            ln=s.count('\n',0,m.start())+1
            flags.append('%s @%d: %s'%(label,ln,s[s.rfind('\n',0,m.start())+1:s.find('\n',m.start())].strip()[:110]))
    open(DST+name+'.java','w').write(s)
    return flags

if __name__=='__main__':
    for n in sys.argv[1:]:
        fl=port(n)
        print('=== %s: %d flags'%(n,len(fl)))
        for f in fl: print('   ',f)
