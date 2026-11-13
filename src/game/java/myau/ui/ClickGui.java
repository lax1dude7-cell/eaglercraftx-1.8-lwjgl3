package myau.ui;

import myau.Myau;
import myau.font.FontManager;
import myau.font.UIFont;
import myau.module.Category;
import myau.module.Module;
import myau.module.modules.ClickGUIModule;
import myau.ui.components.CategoryComponent;
import myau.util.Compat;
import myau.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;

/** 1:1 port of the OpenMyau+ default ClickGUI: five draggable panels, right-click a module for settings. */
public class ClickGui extends GuiScreen {
    private static ClickGui instance;
    private static final String STORE_KEY = "myau_clickgui";
    private final ArrayList<CategoryComponent> categories = new ArrayList<CategoryComponent>();

    // default panel positions measured from the reference screenshot (GUI scale 2)
    private static final int[][] DEFAULT_POS = {{74, 86}, {187, 91}, {293, 98}, {401, 107}, {536, 121}};

    public ClickGui() {
        int i = 0;
        for (Category c : Category.values()) {
            CategoryComponent cc = new CategoryComponent(c.displayName, Myau.moduleManager.byCategory(c));
            cc.setX(DEFAULT_POS[i][0]);
            cc.setY(DEFAULT_POS[i][1]);
            cc.setOpened(true);
            categories.add(cc);
            i++;
        }
        load();
    }

    public static ClickGui getInstance() {
        if (instance == null) instance = new ClickGui();
        return instance;
    }

    public static void resetInstance() { instance = null; }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        FontManager.updateScale(new ScaledResolution(mc));
        RenderUtil.drawRect(0, 0, width, height, 0x64000000);

        UIFont f = Theme.title();
        int blue = 0xFF3CA2FD;
        f.drawStringWithShadow(Myau.NAME + " " + Myau.VERSION, 4, height - 3 - f.getLineHeight() * 2 + 1, blue);
        f.drawStringWithShadow(Myau.CREDIT, 4, height - 3 - f.getLineHeight() + 1, blue);

        long now = System.currentTimeMillis();
        for (CategoryComponent c : categories) {
            c.render(now);
            c.handleDrag(mx, my, width, height);
            for (myau.ui.Component m : c.getModules()) m.update(mx, my);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Compat.eventWheel();
        if (wheel != 0) {
            int dir = wheel > 0 ? 1 : -1;
            int mx = net.lax1dude.eaglercraft.v1_8.Mouse.getEventX() * width / mc.displayWidth;
            int my = height - net.lax1dude.eaglercraft.v1_8.Mouse.getEventY() * height / mc.displayHeight - 1;
            for (CategoryComponent c : categories) c.onScroll(mx, my, dir);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        for (CategoryComponent c : categories) {
            if (button == 0 && c.insideHeader(x, y)) {
                if (c.onToggleButton(x, y)) c.setOpened(!c.isOpened());
                else { c.dragging = true; c.dragX = x - c.getX(); c.dragY = y - c.getY(); }
            }
            if (c.isOpened() && !c.getModules().isEmpty()) {
                for (myau.ui.Component m : c.getModules()) m.mouseDown(x, y, button);
            }
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int button) {
        for (CategoryComponent c : categories) {
            if (button == 0) c.dragging = false;
            if (c.isOpened()) for (myau.ui.Component m : c.getModules()) m.mouseReleased(x, y, button);
        }
    }

    @Override
    protected void keyTyped(char ch, int key) {
        boolean capturing = false;
        for (CategoryComponent c : categories) {
            if (!c.isOpened()) continue;
            for (myau.ui.Component m : c.getModules()) if (m.isCapturingInput()) capturing = true;
        }
        Module gui = Myau.moduleManager.getModule("ClickGui");
        if (!capturing && (key == 1 || (gui != null && key == gui.getKey()))) {
            mc.displayGuiScreen(null);
            return;
        }
        for (CategoryComponent c : categories) {
            if (!c.isOpened()) continue;
            for (myau.ui.Component m : c.getModules()) m.keyTyped(ch, key);
        }
    }

    @Override
    public void onGuiClosed() {
        save();
        Module gui = Myau.moduleManager.getModule(ClickGUIModule.class);
        if (gui != null) gui.setEnabled(false);
    }

    @Override
    public boolean doesGuiPauseGame() { return false; }

    private void save() {
        StringBuilder sb = new StringBuilder();
        for (CategoryComponent c : categories) {
            sb.append(c.getName()).append(',').append(c.getX()).append(',').append(c.getY()).append(',').append(c.isOpened() ? 1 : 0).append(';');
        }
        Compat.storageSet(STORE_KEY, sb.toString());
    }

    private void load() {
        String s = Compat.storageGet(STORE_KEY);
        if (s == null) return;
        try {
            for (String entry : s.split(";")) {
                String[] p = entry.split(",");
                if (p.length < 4) continue;
                for (CategoryComponent c : categories) {
                    if (c.getName().equals(p[0])) {
                        c.setX(Integer.parseInt(p[1]));
                        c.setY(Integer.parseInt(p[2]));
                        c.setOpened(p[3].equals("1"));
                    }
                }
            }
        } catch (Throwable t) { }
    }
}
