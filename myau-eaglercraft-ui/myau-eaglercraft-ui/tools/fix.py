import re, sys
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

ALL=['AimAssist','AntiFireball','AutoClicker','BackTrack','BlockHit','ClickAssits','Criticals','HitBox','HitSelect','KillAura','LagRange','MoreKB','NoHitDelay','Reach','Refill','ServerLag','SprintReset','TargetStrafe','ThrowAura','Velocity','Wtap']
# ---- global regex fixes
for n in ALL:
    s=load(n)
    s=re.sub(r'([\w.]+)\.getValue\(\)\.toString\(\)',r'String.valueOf(\1.getValue())',s)
    s=re.sub(r'IAccessorRenderManager (\w+) = \(IAccessorRenderManager\) mc\.getRenderManager\(\);',r'net.minecraft.client.renderer.entity.RenderManager \1 = mc.getRenderManager();',s)
    s=re.sub(r'Acc\.getRenderPos([XYZ])\(([^()]*(?:\([^()]*\))?[^()]*)\)',r'\2.viewerPos\1',s)
    s=s.replace('.getRenderPosX()','.viewerPosX').replace('.getRenderPosY()','.viewerPosY').replace('.getRenderPosZ()','.viewerPosZ')
    save(n,s)

# ---- AimAssist
R('AimAssist','''        List<EntityPlayer> inRange = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityPlayer).map(entity -> (EntityPlayer) entity)
                .filter(this::isValidTarget)
                .sorted(Comparator.comparingDouble(RotationUtil::distanceToEntity))
                .collect(Collectors.toList());
        if (inRange.isEmpty()) return null;
        if (inRange.stream().anyMatch(this::isInReach)) inRange.removeIf(entityPlayer -> !this.isInReach(entityPlayer));
        return inRange.get(0);''','''        List<EntityPlayer> inRange = new java.util.ArrayList<EntityPlayer>();
        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
            net.minecraft.entity.Entity entity = mc.theWorld.loadedEntityList.get(i);
            if (entity instanceof EntityPlayer && this.isValidTarget((EntityPlayer) entity)) inRange.add((EntityPlayer) entity);
        }
        java.util.Collections.sort(inRange, new java.util.Comparator<EntityPlayer>() {
            public int compare(EntityPlayer a, EntityPlayer b) {
                return Double.compare(RotationUtil.distanceToEntity(a), RotationUtil.distanceToEntity(b));
            }
        });
        if (inRange.isEmpty()) return null;
        boolean anyReach = false;
        for (int i = 0; i < inRange.size(); i++) if (this.isInReach(inRange.get(i))) anyReach = true;
        if (anyReach) {
            for (int i = inRange.size() - 1; i >= 0; i--) if (!this.isInReach(inRange.get(i))) inRange.remove(i);
        }
        return inRange.get(0);''')
R('AimAssist','import java.util.stream.Collectors;\n','')

# ---- AntiFireball
R('AntiFireball','''            List<EntityFireball> fireballs = mc.theWorld
                    .loadedEntityList
                    .stream()
                    .filter(entity -> entity instanceof EntityFireball)
                    .map(entity -> (EntityFireball) entity)
                    .collect(Collectors.toList());
            this.farList.removeIf(entityFireball -> !fireballs.contains(entityFireball));
            this.nearList.removeIf(entityFireball -> !fireballs.contains(entityFireball));''','''            List<EntityFireball> fireballs = new java.util.ArrayList<EntityFireball>();
            for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
                net.minecraft.entity.Entity entity = mc.theWorld.loadedEntityList.get(i);
                if (entity instanceof EntityFireball) fireballs.add((EntityFireball) entity);
            }
            for (int i = this.farList.size() - 1; i >= 0; i--) if (!fireballs.contains(this.farList.get(i))) this.farList.remove(i);
            for (int i = this.nearList.size() - 1; i >= 0; i--) if (!fireballs.contains(this.nearList.get(i))) this.nearList.remove(i);''')
R('AntiFireball','''                this.target = this.farList.stream().filter(this::isValidTarget).min(Comparator.comparingDouble(RotationUtil::distanceToEntity)).orElse(null);''','''                this.target = null;
                double best = Double.MAX_VALUE;
                for (int i = 0; i < this.farList.size(); i++) {
                    EntityFireball fb = this.farList.get(i);
                    if (this.isValidTarget(fb)) {
                        double d = RotationUtil.distanceToEntity(fb);
                        if (d < best) { best = d; this.target = fb; }
                    }
                }''')
R('AntiFireball','import java.util.stream.Collectors;\n','')

# ---- AutoClicker
R('AutoClicker','''        return mc.theWorld
                .loadedEntityList
                .stream()
                .filter(e -> e instanceof EntityPlayer)
                .map(e -> (EntityPlayer) e)
                .anyMatch(this::isValidTarget);''','''        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
            net.minecraft.entity.Entity e = mc.theWorld.loadedEntityList.get(i);
            if (e instanceof EntityPlayer && this.isValidTarget((EntityPlayer) e)) return true;
        }
        return false;''')
R('AutoClicker','String.format("%d-%d", this.minCPS.getValue(), this.maxCPS.getValue())','this.minCPS.getValue() + "-" + this.maxCPS.getValue()')

# ---- BackTrack
R('BackTrack','candidates.sort((a, b) -> Float.compare(RotationUtil.angleToEntity(a), RotationUtil.angleToEntity(b)));','''java.util.Collections.sort(candidates, new java.util.Comparator<EntityLivingBase>() {
            public int compare(EntityLivingBase a, EntityLivingBase b) {
                return Float.compare(RotationUtil.angleToEntity(a), RotationUtil.angleToEntity(b));
            }
        });''')

# ---- ClickAssits (Robot -> ClickBot)
R('ClickAssits','import java.awt.AWTException;\n','')
R('ClickAssits','import java.awt.Robot;\n','')
R('ClickAssits','private Robot bot;','private ClickBot bot;')
R('ClickAssits','!Mouse.isButtonDown(0)','!myau.util.Compat.isMouseDown(0)')
R('ClickAssits','!Mouse.isButtonDown(1)','!myau.util.Compat.isMouseDown(1)')
R('ClickAssits','''        try {
            this.bot = new Robot();
        } catch (AWTException e) {
            this.setEnabled(false);
        }''','''        this.bot = new ClickBot();''')
R('ClickAssits','this.leftClicks.removeIf(time -> currentTime - time > 1000L);','for (java.util.Iterator<Long> it = this.leftClicks.iterator(); it.hasNext(); ) { if (currentTime - it.next() > 1000L) it.remove(); }')
R('ClickAssits','this.rightClicks.removeIf(time -> currentTime - time > 1000L);','for (java.util.Iterator<Long> it = this.rightClicks.iterator(); it.hasNext(); ) { if (currentTime - it.next() > 1000L) it.remove(); }')
s=load('ClickAssits'); i=s.rindex('}')
s=s[:i]+'''
    /**
     * Replacement for java.awt.Robot: a browser cannot synthesise OS mouse events, so the extra click is injected
     * through the vanilla key-binding queue (KeyBinding.onTick + pressed flag). mask 16 = left, 4 = right.
     */
    private static final class ClickBot {
        private net.minecraft.client.settings.KeyBinding key(int mask) {
            return mask == 16 ? mc.gameSettings.keyBindAttack : mc.gameSettings.keyBindUseItem;
        }
        void mousePress(int mask) {
            net.minecraft.client.settings.KeyBinding kb = key(mask);
            net.minecraft.client.settings.KeyBinding.onTick(kb.getKeyCode());
            Acc.setPressed(kb, true);
        }
        void mouseRelease(int mask) {
            Acc.setPressed(key(mask), false);
        }
    }
}
'''
if 'import myau.util.Acc;' not in s: s=s.replace('\nimport ','\nimport myau.util.Acc;\nimport ',1)
save('ClickAssits',s)

# ---- HitBox
R('HitBox','''            List<EntityLivingBase> entities = mc.theWorld.loadedEntityList
                    .stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(this::shouldShowEntity)
                    .collect(Collectors.toList());''','''            List<EntityLivingBase> entities = new java.util.ArrayList<EntityLivingBase>();
            for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
                net.minecraft.entity.Entity entity = mc.theWorld.loadedEntityList.get(i);
                if (entity instanceof EntityLivingBase && this.shouldShowEntity((EntityLivingBase) entity)) entities.add((EntityLivingBase) entity);
            }''')
R('HitBox','import java.util.stream.Collectors;\n','')
R('HitBox','String.format("%.1fx", this.multiplier.getValue())','TextUtil.fmt(this.multiplier.getValue(), 1, 1) + "x"')
s=load('HitBox')
if 'import myau.util.TextUtil;' not in s: s=s.replace('\nimport ','\nimport myau.util.TextUtil;\nimport ',1)
save('HitBox',s)

# ---- LagRange
R('LagRange','''                        List<EntityPlayer> players = mc.theWorld
                                .loadedEntityList
                                .stream()
                                .filter(entity -> entity instanceof EntityPlayer)
                                .map(entity -> (EntityPlayer) entity)
                                .filter(this::isValidTarget)
                                .collect(Collectors.toList());''','''                        List<EntityPlayer> players = new java.util.ArrayList<EntityPlayer>();
                        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
                            net.minecraft.entity.Entity entity = mc.theWorld.loadedEntityList.get(i);
                            if (entity instanceof EntityPlayer && this.isValidTarget((EntityPlayer) entity)) players.add((EntityPlayer) entity);
                        }''')
R('LagRange','import java.util.stream.Collectors;\n','')
R('LagRange','String.format("%dms", this.delay.getValue())','this.delay.getValue() + "ms"')

# ---- Reach
R('Reach','private static final DecimalFormat df = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.US));\n','')
R('Reach','import java.text.DecimalFormat;\n','')
R('Reach','import java.text.DecimalFormatSymbols;\n','')
R('Reach','df.format(this.range.getValue())','TextUtil.fmt(this.range.getValue(), 1, 2)')
s=load('Reach')
if 'import myau.util.TextUtil;' not in s: s=s.replace('\nimport ','\nimport myau.util.TextUtil;\nimport ',1)
save('Reach',s)

# ---- Velocity formats
R('Velocity',r'String\.format\(\s*"%sExplosion \(&otick: %d, x: %\.2f, y: %\.2f, z: %\.2f&r\)&r",\s*Myau\.clientName,\s*mc\.thePlayer\.ticksExisted,\s*(.*?),\s*(.*?),\s*(.*?)\s*\)\s*\)',
  lambda_ if False else r'Myau.clientName + "Explosion (&otick: " + mc.thePlayer.ticksExisted + ", x: " + TextUtil.fmt(\1, 2, 2) + ", y: " + TextUtil.fmt(\2, 2, 2) + ", z: " + TextUtil.fmt(\3, 2, 2) + "&r)&r")',regex=True)
R('Velocity',r'String\.format\(\s*"%sVelocity \(&otick: %d, x: %\.2f, y: %\.2f, z: %\.2f&r\)&r",\s*Myau\.clientName,\s*mc\.thePlayer\.ticksExisted,\s*(.*?),\s*(.*?),\s*(.*?)\s*\)\s*\)',
  r'Myau.clientName + "Velocity (&otick: " + mc.thePlayer.ticksExisted + ", x: " + TextUtil.fmt(\1, 2, 2) + ", y: " + TextUtil.fmt(\2, 2, 2) + ", z: " + TextUtil.fmt(\3, 2, 2) + "&r)&r")',regex=True)
R('Velocity','packet.func_149147_e(, 2, 2) + "&r)&r")\n                            );','packet.func_149147_e(), 2, 2) + "&r)&r"\n                            );')
s=load('Velocity')
if 'import myau.util.TextUtil;' not in s: s=s.replace('\nimport ','\nimport myau.util.TextUtil;\nimport ',1)
save('Velocity',s)

# ---- KillAura
R('KillAura','private static final DecimalFormat df = new DecimalFormat("+0.0;-0.0", new DecimalFormatSymbols(Locale.US));\n','')
R('KillAura','import java.text.DecimalFormat;\n','')
R('KillAura','import java.text.DecimalFormatSymbols;\n','')
for var in ('packet','diff'):
    R('KillAura',r'String\.format\(\s*"%sHealth: %s&l%s&r \(&otick: %d&r\)&r",\s*Myau\.clientName,\s*'+var+r' > 0\.0F \? "&a" : "&c",\s*df\.format\('+var+r'\),\s*mc\.thePlayer\.ticksExisted\s*\)',
      'Myau.clientName + "Health: " + ('+var+' > 0.0F ? "&a" : "&c") + "&l" + TextUtil.signed('+var+', 1) + "&r (&otick: " + mc.thePlayer.ticksExisted + "&r)&r"',regex=True)
R('KillAura','''        return mc.theWorld
                .loadedEntityList
                .stream()
                .anyMatch(
                        entity -> entity instanceof EntityLivingBase
                                && this.isValidTarget((EntityLivingBase) entity)
                                && this.isInBlockRange((EntityLivingBase) entity)
                );''','''        for (int i = 0; i < mc.theWorld.loadedEntityList.size(); i++) {
            Entity entity = mc.theWorld.loadedEntityList.get(i);
            if (entity instanceof EntityLivingBase
                    && this.isValidTarget((EntityLivingBase) entity)
                    && this.isInBlockRange((EntityLivingBase) entity)) return true;
        }
        return false;''')
def prune(pred):
    return '''                        boolean any%s = false;
                        for (int i = 0; i < validTargets.size(); i++) if (this.%s(validTargets.get(i))) any%s = true;
                        if (any%s) {
                            for (int i = validTargets.size() - 1; i >= 0; i--) if (!this.%s(validTargets.get(i))) validTargets.remove(i);
                        }''' % (pred,pred,pred,pred,pred)
for pred in ('isInSwingRange','isInAttackRange','isPlayerTarget'):
    R('KillAura','''                        if (validTargets.stream().anyMatch(this::%s)) {
                            validTargets.removeIf(entityLivingBase -> !this.%s(entityLivingBase));
                        }'''%(pred,pred),prune(pred))
R('KillAura','''validTargets.sort(
                                (entityLivingBase1, entityLivingBase2) -> {''','''java.util.Collections.sort(validTargets, new java.util.Comparator<EntityLivingBase>() {
                                public int compare(EntityLivingBase entityLivingBase1, EntityLivingBase entityLivingBase2) {''')
R('KillAura','''                                }
                        );
                        if (needsNewTarget) {''','''                                } });
                        if (needsNewTarget) {''')
R('KillAura','switch (this.sort.getValue()) {','switch (KillAura.this.sort.getValue()) {')
R('KillAura','''    @Getter
    public static class AttackData {''','''    public static class AttackData {''')
R('KillAura','''        public AttackData(EntityLivingBase entityLivingBase, KillAura killAura) {''','''        public EntityLivingBase getEntity() { return entity; }
        public KillAura getKillAura() { return killAura; }
        public AxisAlignedBB getBox() { return box; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }

        public AttackData(EntityLivingBase entityLivingBase, KillAura killAura) {''')
s=load('KillAura')
if 'import myau.util.TextUtil;' not in s: s=s.replace('\nimport ','\nimport myau.util.TextUtil;\nimport ',1)
save('KillAura',s)

R('BackTrack','super("Backtrack"','super("BackTrack"')
print('MISSES:')
for m in miss: print('  ',m)
