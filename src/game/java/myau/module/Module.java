package myau.module;

import myau.events.*;
import myau.property.Property;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Same API as OpenMyau+'s Module, but events are plain virtual methods (no reflection / annotations -> TeaVM safe).
 * Only ENABLED modules receive events. Override the onXxx methods you need.
 */
public class Module {
    protected final String name;
    protected final String description;
    protected final boolean defaultEnabled;
    protected final int defaultKey;
    protected final boolean defaultHidden;
    protected boolean enabled;
    protected int key;
    protected boolean hidden;
    private Category category;
    private final List<Property> properties = new ArrayList<Property>();
    private final int[] priorities = new int[Kinds.COUNT];

    public Module(String name, boolean enabled) { this(name, enabled, false, ""); }
    public Module(String name, boolean enabled, boolean hidden) { this(name, enabled, hidden, ""); }
    public Module(String name, boolean enabled, boolean hidden, String description) {
        this.name = name;
        this.description = description;
        this.enabled = this.defaultEnabled = enabled;
        this.key = this.defaultKey = 0;
        this.hidden = this.defaultHidden = hidden;
        for (int i = 0; i < priorities.length; i++) priorities[i] = 2; // MEDIUM
    }

    /** Convenience constructor used by the first UI delivery (HUD / DynamicIsland / ClickGui / stubs). */
    public Module(String name, Category category, boolean enabled, boolean hidden) {
        this(name, enabled, hidden, "");
        this.category = category;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public void setCategory(Category c) { this.category = c; }
    public boolean isEnabled() { return enabled; }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean h) { hidden = h; }
    public int getKey() { return key; }
    public void setKey(int k) { key = k; }
    public String[] getSuffix() { return new String[0]; }
    public boolean shouldKeepSprint() { return false; }
    public void verifyValue(String s) {}

    /** Registers a setting so the ClickGui shows it (call in the constructor, in display order). */
    protected void add(Property p) { properties.add(p); }
    public List<Property> getProperties() { return properties; }

    public void setPriority(int kind, int priority) { priorities[kind] = priority; }
    public int getPriority(int kind) { return priorities[kind]; }

    /** Sets the enabled flag WITHOUT calling onEnabled (safe at startup, before a world exists). */
    public void setEnabledSilently(boolean e) { enabled = e; }

    public boolean toggle() { setEnabled(!enabled); return true; }

    public void setEnabled(boolean e) {
        if (e == enabled) return;
        enabled = e;
        if (e) onEnabled(); else onDisabled();
    }

    public void onEnabled() {}
    public void onDisabled() {}

    // ---- 2D HUD hook (called every frame while enabled) ----
    public void onRender2D(float partialTicks, ScaledResolution sr) {}

    // ---- event handlers ----
    public void onTick(TickEvent e) {}
    public void onUpdate(UpdateEvent e) {}
    public void onPlayerUpdate(PlayerUpdateEvent e) {}
    public void onPostMotion(PostMotionEvent e) {}
    public void onPacket(PacketEvent e) {}
    public void onAttack(AttackEvent e) {}
    public void onLeftClick(LeftClickMouseEvent e) {}
    public void onRightClick(RightClickMouseEvent e) {}
    public void onKey(KeyEvent e) {}
    public void onMoveInput(MoveInputEvent e) {}
    public void onStrafe(StrafeEvent e) {}
    public void onKnockback(KnockbackEvent e) {}
    public void onLivingUpdate(LivingUpdateEvent e) {}
    public void onLoadWorld(LoadWorldEvent e) {}
    public void onRender3D(Render3DEvent e) {}
    public void onPick(PickEvent e) {}
    public void onRaytrace(RaytraceEvent e) {}
    public void onHitBlock(HitBlockEvent e) {}
    public void onCancelUse(CancelUseEvent e) {}
    public void onSafeWalk(SafeWalkEvent e) {}
    public void onJump(JumpEvent e) {}
    public void onSwapItem(SwapItemEvent e) {}
    public void onWindowClick(WindowClickEvent e) {}
    public void onResize(ResizeEvent e) {}
    public void onRenderLiving(RenderLivingEvent e) {}
}
