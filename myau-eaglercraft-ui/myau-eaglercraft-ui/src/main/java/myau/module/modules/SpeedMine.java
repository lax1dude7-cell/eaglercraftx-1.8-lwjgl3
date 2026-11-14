package myau.module.modules;

import myau.util.Acc;
import myau.event.types.EventType;
import myau.events.TickEvent;
import myau.module.Module;
import myau.property.properties.IntProperty;
import myau.property.properties.PercentProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class SpeedMine extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final PercentProperty speed = new PercentProperty("speed", 15);
    public final IntProperty delay = new IntProperty("delay", 0, 0, 4);

    public SpeedMine() {
        super("SpeedMine", false);
    
        add(speed);
        add(delay);
    }

    @Override
    public void onTick(TickEvent event) {
        if (this.isEnabled() && event.getType() == EventType.PRE) {
            if (!mc.playerController.isInCreativeMode()) {
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                    Acc.setBlockHitDelay(mc.playerController, Math.min(Acc.getBlockHitDelay(mc.playerController), this.delay.getValue() + 1));
                    if (Acc.getIsHittingBlock(mc.playerController)) {
                        float curBlockDamageMP = Acc.getCurBlockDamageMP(mc.playerController);
                        float damage = 0.3F * (((float) this.speed.getValue()) / 100.0F);
                        if (curBlockDamageMP < damage) {
                            Acc.setCurBlockDamageMP(mc.playerController, damage);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String[] getSuffix() {
        return new String[]{this.speed.getValue() + "%"};
    }
}
