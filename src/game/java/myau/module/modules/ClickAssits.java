package myau.module.modules;

import myau.util.Acc;
import myau.property.Condition;
import myau.event.types.EventType;
import myau.event.types.Priority;
import myau.events.LeftClickMouseEvent;
import myau.events.RightClickMouseEvent;
import myau.events.TickEvent;
import myau.module.Module;
import myau.util.ItemUtil;
import myau.property.properties.BooleanProperty;
import myau.property.properties.FloatProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldSettings.GameType;
import org.lwjgl.input.Mouse;

import java.util.LinkedList;

public class ClickAssits extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private ClickBot bot;
    private boolean ignoreNextLeft = false;
    private boolean ignoreNextRight = false;

    private final LinkedList<Long> leftClicks = new LinkedList<>();
    private final LinkedList<Long> rightClicks = new LinkedList<>();

    public final BooleanProperty leftClick = new BooleanProperty("left-click", true);
    public final FloatProperty chanceLeft = new FloatProperty("chance-left", 80.0F, 0.0F, 100.0F, new Condition() { public boolean test() { return ClickAssits.this.leftClick.getValue(); } });
    public final BooleanProperty weaponOnly = new BooleanProperty("weapon-only", true, new Condition() { public boolean test() { return ClickAssits.this.leftClick.getValue(); } });
    public final BooleanProperty onlyWhileTargeting = new BooleanProperty("only-while-targeting", false, new Condition() { public boolean test() { return ClickAssits.this.leftClick.getValue(); } });
    public final BooleanProperty aboveCPSLeft = new BooleanProperty("above-5-cps-left", false, new Condition() { public boolean test() { return ClickAssits.this.leftClick.getValue(); } });

    public final BooleanProperty rightClick = new BooleanProperty("right-click", false);
    public final FloatProperty chanceRight = new FloatProperty("chance-right", 80.0F, 0.0F, 100.0F, new Condition() { public boolean test() { return ClickAssits.this.rightClick.getValue(); } });
    public final BooleanProperty blocksOnly = new BooleanProperty("blocks-only", true, new Condition() { public boolean test() { return ClickAssits.this.rightClick.getValue(); } });
    public final BooleanProperty aboveCPSRight = new BooleanProperty("above-5-cps-right", false, new Condition() { public boolean test() { return ClickAssits.this.rightClick.getValue(); } });

    public final BooleanProperty disableInCreative = new BooleanProperty("disable-in-creative", true);

    public ClickAssits() {
        super("ClickAssits", false);
    
        add(leftClick);
        add(chanceLeft);
        add(weaponOnly);
        add(onlyWhileTargeting);
        add(aboveCPSLeft);
        add(rightClick);
        add(chanceRight);
        add(blocksOnly);
        add(aboveCPSRight);
        add(disableInCreative);
        setPriority(myau.module.Kinds.LEFT_CLICK, 1);
        setPriority(myau.module.Kinds.RIGHT_CLICK, 1);
    }

    private int getLeftCPS() {
        long currentTime = System.currentTimeMillis();
        for (java.util.Iterator<Long> it = this.leftClicks.iterator(); it.hasNext(); ) { if (currentTime - it.next() > 1000L) it.remove(); }
        return this.leftClicks.size();
    }

    private int getRightCPS() {
        long currentTime = System.currentTimeMillis();
        for (java.util.Iterator<Long> it = this.rightClicks.iterator(); it.hasNext(); ) { if (currentTime - it.next() > 1000L) it.remove(); }
        return this.rightClicks.size();
    }

    private boolean shouldDoubleClickLeft() {
        if (!this.leftClick.getValue()) {
            return false;
        }

        if (this.chanceLeft.getValue() == 0.0F) {
            return false;
        }

        if (this.aboveCPSLeft.getValue() && this.getLeftCPS() <= 5) {
            return false;
        }

        if (this.weaponOnly.getValue() && !ItemUtil.isHoldingSword()) {
            return false;
        }

        if (this.onlyWhileTargeting.getValue()) {
            if (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null) {
                return false;
            }
        }

        if (this.chanceLeft.getValue() < 100.0F) {
            double chance = Math.random();
            if (chance >= this.chanceLeft.getValue() / 100.0F) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldDoubleClickRight() {
        if (!this.rightClick.getValue()) {
            return false;
        }

        if (this.chanceRight.getValue() == 0.0F) {
            return false;
        }

        if (this.aboveCPSRight.getValue() && this.getRightCPS() <= 5) {
            return false;
        }

        if (this.blocksOnly.getValue()) {
            ItemStack item = mc.thePlayer.getHeldItem();
            if (item == null || !(item.getItem() instanceof ItemBlock)) {
                return false;
            }
        }

        if (this.chanceRight.getValue() < 100.0F) {
            double chance = Math.random();
            if (chance >= this.chanceRight.getValue() / 100.0F) {
                return false;
            }
        }

        return true;
    }

    private void fixLeftButton() {
        if (this.ignoreNextLeft && !myau.util.Compat.isMouseDown(0)) {
            this.bot.mouseRelease(16);
            this.ignoreNextLeft = false;
        }
    }

    private void fixRightButton() {
        if (this.ignoreNextRight && !myau.util.Compat.isMouseDown(1)) {
            this.bot.mouseRelease(4);
            this.ignoreNextRight = false;
        }
    }

    @Override
    public void onTick(TickEvent event) {
        if (event.getType() == EventType.PRE) {
            this.fixLeftButton();
            this.fixRightButton();
        }
    }

    @Override
    public void onLeftClick(LeftClickMouseEvent event) {
        if (this.disableInCreative.getValue() && mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            return;
        }

        if (mc.currentScreen != null) {
            this.fixLeftButton();
            this.fixRightButton();
            return;
        }

        // Track for CPS if needed
        if (this.aboveCPSLeft.getValue()) {
            this.leftClicks.add(System.currentTimeMillis());
        }

        if (!this.isEnabled() || event.isCancelled()) {
            return;
        }

        // If this is the extra click we injected, ignore it
        if (this.ignoreNextLeft) {
            this.ignoreNextLeft = false;
            return;
        }

        // Check if we should inject an extra click
        if (this.shouldDoubleClickLeft()) {
            this.bot.mouseRelease(16);
            this.bot.mousePress(16);
            this.ignoreNextLeft = true;
        }
    }

    @Override
    public void onRightClick(RightClickMouseEvent event) {
        if (this.disableInCreative.getValue() && mc.playerController.getCurrentGameType() == GameType.CREATIVE) {
            return;
        }

        if (mc.currentScreen != null) {
            this.fixLeftButton();
            this.fixRightButton();
            return;
        }

        // Track for CPS if needed
        if (this.aboveCPSRight.getValue()) {
            this.rightClicks.add(System.currentTimeMillis());
        }

        if (!this.isEnabled() || event.isCancelled()) {
            return;
        }

        // If this is the extra click we injected, ignore it
        if (this.ignoreNextRight) {
            this.ignoreNextRight = false;
            return;
        }

        // Check if we should inject an extra click
        if (this.shouldDoubleClickRight()) {
            this.bot.mouseRelease(4);
            this.bot.mousePress(4);
            this.ignoreNextRight = true;
        }
    }

    public void onEnable() {
        this.bot = new ClickBot();
        this.ignoreNextLeft = false;
        this.ignoreNextRight = false;
        this.leftClicks.clear();
        this.rightClicks.clear();
    }

    public void onDisable() {
        this.ignoreNextLeft = false;
        this.ignoreNextRight = false;
        this.leftClicks.clear();
        this.rightClicks.clear();
        this.bot = null;
    }

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
