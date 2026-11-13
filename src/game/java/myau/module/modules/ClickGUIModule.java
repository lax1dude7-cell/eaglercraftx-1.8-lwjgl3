package myau.module.modules;

import myau.module.Category;
import myau.module.Module;
import myau.ui.ClickGui;
import net.minecraft.client.Minecraft;

public class ClickGUIModule extends Module {
    public ClickGUIModule() {
        super("ClickGui", Category.RENDER, false, true);
        setKey(54); // RSHIFT
    }

    @Override
    public void onEnabled() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) mc.displayGuiScreen(ClickGui.getInstance());
    }
}
