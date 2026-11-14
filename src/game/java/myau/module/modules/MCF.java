package myau.module.modules;

import myau.Myau;
import myau.events.KeyEvent;
import myau.module.Module;
import myau.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class MCF extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public MCF() {
        super("MCF", false, true);
    
    }

    @Override
    public void onKey(KeyEvent event) {
        if (this.isEnabled() && event.getKey() == -98) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                String hitName = mc.objectMouseOver.entityHit.getName();
                if (!Myau.friendManager.isFriend(hitName)) {
                    Myau.friendManager.add(hitName);
                    ChatUtil.sendFormatted(Myau.clientName + "Added &o" + hitName + "&r to your friend list&r");
                } else {
                    Myau.friendManager.remove(hitName);
                    ChatUtil.sendFormatted(Myau.clientName + "Removed &o" + hitName + "&r from your friend list&r");
                }
            }
        }
    }
}
