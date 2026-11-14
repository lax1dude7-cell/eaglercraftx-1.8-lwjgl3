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

# ---- AutoBlockIn
R('AutoBlockIn','supports.sort(Comparator.comparingDouble(a -> a.distance));','''java.util.Collections.sort(supports, new Comparator<BlockData>() {
            public int compare(BlockData a, BlockData b) { return Double.compare(a.distance, b.distance); }
        });''')
R('AutoBlockIn','String text = String.format("Blocking: %.0f%%", progress * 100.0F);','String text = "Blocking: " + Math.round(progress * 100.0F) + "%";')
R('AutoBlockIn','GL11.glPushMatrix();','GlStateManager.pushMatrix();')
R('AutoBlockIn','GL11.glScaled((double)scale, (double)scale, 0.0);','GlStateManager.scale((double)scale, (double)scale, 1.0);')
R('AutoBlockIn','GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);','GlStateManager.blendFunc(770, 771);')
R('AutoBlockIn','GL11.glPopMatrix();','GlStateManager.popMatrix();')
# ---- AutoGapple
R('AutoGapple','import myau.mixin.IAccessorKeyBinding; // Import IAccessorKeyBinding\n','')
# ---- formats
R('ChestStealer','String.format("%s%s: &cYour inventory is full!&r", Myau.clientName, this.getName())','Myau.clientName + this.getName() + ": &cYour inventory is full!&r"')
R('FastPlace','private static final DecimalFormat df = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.US));\n','')
R('FastPlace','import java.text.DecimalFormat;\n','')
R('FastPlace','import java.text.DecimalFormatSymbols;\n','')
R('FastPlace','df.format(this.delay.getValue())','TextUtil.fmt(this.delay.getValue(), 1, 2)')
need('FastPlace','myau.util.TextUtil')
R('MCF','String.format("%sAdded &o%s&r to your friend list&r", Myau.clientName, hitName)','Myau.clientName + "Added &o" + hitName + "&r to your friend list&r"')
R('MCF','String.format("%sRemoved &o%s&r from your friend list&r", Myau.clientName, hitName)','Myau.clientName + "Removed &o" + hitName + "&r from your friend list&r"')
R('SpeedMine','String.format("%d%%", this.speed.getValue())','this.speed.getValue() + "%"')
# ---- InvWalk
R('InvWalk','''        this.movementKeys.keySet().stream()
                .filter(key -> !skipSneak || key != mc.gameSettings.keyBindSneak)
                .forEach(key -> KeyBindUtil.updateKeyState(key.getKeyCode()));''','''        for (KeyBinding key : this.movementKeys.keySet()) {
            if (!skipSneak || key != mc.gameSettings.keyBindSneak) KeyBindUtil.updateKeyState(key.getKeyCode());
        }''')
R('InvWalk','this.movementKeys.replaceAll((k, v) -> false);','''for (Map.Entry<KeyBinding, Boolean> e : this.movementKeys.entrySet()) e.setValue(Boolean.FALSE);''')
R('InvWalk','return this.movementKeys.values().stream().anyMatch(Boolean::booleanValue);','''for (Boolean b : this.movementKeys.values()) if (b.booleanValue()) return true;
        return false;''')
R('InvWalk','this.movementKeys.replaceAll((k, v) -> KeyBindUtil.isKeyDown(k.getKeyCode()));','''for (Map.Entry<KeyBinding, Boolean> e : this.movementKeys.entrySet()) e.setValue(Boolean.valueOf(KeyBindUtil.isKeyDown(e.getKey().getKeyCode())));''')
# ---- Scaffold
R('Scaffold','''                positions.sort(
                        Comparator.comparingDouble(
                                o -> o.distanceSqToCenter((double) targetPos.getX() + 0.5, (double) targetPos.getY() + 0.5, (double) targetPos.getZ() + 0.5)
                        )
                );''','''                final BlockPos fTarget = targetPos;
                java.util.Collections.sort(positions, new Comparator<BlockPos>() {
                    public int compare(BlockPos a, BlockPos b) {
                        return Double.compare(
                                a.distanceSqToCenter((double) fTarget.getX() + 0.5, (double) fTarget.getY() + 0.5, (double) fTarget.getZ() + 0.5),
                                b.distanceSqToCenter((double) fTarget.getX() + 0.5, (double) fTarget.getY() + 0.5, (double) fTarget.getZ() + 0.5));
                    }
                });''')
print('MISSES:',miss)
