package myau.event;

import myau.Myau;
import myau.event.events.Event;
import myau.event.types.EventType;
import myau.events.*;
import myau.module.Kinds;
import myau.module.Module;

import java.util.List;

/** Reflection-free event dispatcher. Same call site as OpenMyau+: EventManager.call(new XEvent(...)). */
public final class EventManager {
    private EventManager() {}

    public static void call(Event e) {
        if (Myau.moduleManager == null) return;
        int kind = kindOf(e);
        if (kind < 0) return;
        managers(kind, e);
        List<Module> all = Myau.moduleManager.all();
        int n = all.size();
        for (int p = 0; p <= 4; p++) {
            for (int i = 0; i < n; i++) {
                Module m = all.get(i);
                if (m.isEnabled() && m.getPriority(kind) == p) dispatch(m, kind, e);
            }
        }
    }

    private static int kindOf(Event e) {
        if (e instanceof TickEvent) return Kinds.TICK;
        if (e instanceof UpdateEvent) return Kinds.UPDATE;
        if (e instanceof PlayerUpdateEvent) return Kinds.PLAYER_UPDATE;
        if (e instanceof PostMotionEvent) return Kinds.POST_MOTION;
        if (e instanceof PacketEvent) return Kinds.PACKET;
        if (e instanceof AttackEvent) return Kinds.ATTACK;
        if (e instanceof LeftClickMouseEvent) return Kinds.LEFT_CLICK;
        if (e instanceof RightClickMouseEvent) return Kinds.RIGHT_CLICK;
        if (e instanceof KeyEvent) return Kinds.KEY;
        if (e instanceof MoveInputEvent) return Kinds.MOVE_INPUT;
        if (e instanceof StrafeEvent) return Kinds.STRAFE;
        if (e instanceof KnockbackEvent) return Kinds.KNOCKBACK;
        if (e instanceof LivingUpdateEvent) return Kinds.LIVING_UPDATE;
        if (e instanceof LoadWorldEvent) return Kinds.LOAD_WORLD;
        if (e instanceof Render3DEvent) return Kinds.RENDER_3D;
        if (e instanceof PickEvent) return Kinds.PICK;
        if (e instanceof RaytraceEvent) return Kinds.RAYTRACE;
        if (e instanceof HitBlockEvent) return Kinds.HIT_BLOCK;
        if (e instanceof CancelUseEvent) return Kinds.CANCEL_USE;
        if (e instanceof SafeWalkEvent) return Kinds.SAFE_WALK;
        if (e instanceof JumpEvent) return Kinds.JUMP;
        if (e instanceof SwapItemEvent) return Kinds.SWAP_ITEM;
        if (e instanceof WindowClickEvent) return Kinds.WINDOW_CLICK;
        if (e instanceof ResizeEvent) return Kinds.RESIZE;
        if (e instanceof RenderLivingEvent) return Kinds.RENDER_LIVING;
        return -1;
    }

    /** Managers run first (they were Priority.HIGHEST listeners in OpenMyau+). */
    private static void managers(int kind, Event e) {
        if (kind == Kinds.TICK) {
            Myau.rotationManager.onTick((TickEvent) e);
            Myau.blinkManager.onTick((TickEvent) e);
            Myau.lagManager.onTick((TickEvent) e);
            Myau.playerStateManager.onTick((TickEvent) e);
        } else if (kind == Kinds.PLAYER_UPDATE) {
            Myau.floatManager.onPlayerUpdate((PlayerUpdateEvent) e);
        } else if (kind == Kinds.RENDER_3D) {
            Myau.rotationManager.onRender3D((Render3DEvent) e);
        } else if (kind == Kinds.PACKET) {
            Myau.blinkManager.onPacket((PacketEvent) e);
            Myau.lagManager.onPacket((PacketEvent) e);
        }
    }

    private static void dispatch(Module m, int k, Event e) {
        switch (k) {
            case Kinds.TICK: m.onTick((TickEvent) e); break;
            case Kinds.UPDATE: m.onUpdate((UpdateEvent) e); break;
            case Kinds.PLAYER_UPDATE: m.onPlayerUpdate((PlayerUpdateEvent) e); break;
            case Kinds.POST_MOTION: m.onPostMotion((PostMotionEvent) e); break;
            case Kinds.PACKET: m.onPacket((PacketEvent) e); break;
            case Kinds.ATTACK: m.onAttack((AttackEvent) e); break;
            case Kinds.LEFT_CLICK: m.onLeftClick((LeftClickMouseEvent) e); break;
            case Kinds.RIGHT_CLICK: m.onRightClick((RightClickMouseEvent) e); break;
            case Kinds.KEY: m.onKey((KeyEvent) e); break;
            case Kinds.MOVE_INPUT: m.onMoveInput((MoveInputEvent) e); break;
            case Kinds.STRAFE: m.onStrafe((StrafeEvent) e); break;
            case Kinds.KNOCKBACK: m.onKnockback((KnockbackEvent) e); break;
            case Kinds.LIVING_UPDATE: m.onLivingUpdate((LivingUpdateEvent) e); break;
            case Kinds.LOAD_WORLD: m.onLoadWorld((LoadWorldEvent) e); break;
            case Kinds.RENDER_3D: m.onRender3D((Render3DEvent) e); break;
            case Kinds.PICK: m.onPick((PickEvent) e); break;
            case Kinds.RAYTRACE: m.onRaytrace((RaytraceEvent) e); break;
            case Kinds.HIT_BLOCK: m.onHitBlock((HitBlockEvent) e); break;
            case Kinds.CANCEL_USE: m.onCancelUse((CancelUseEvent) e); break;
            case Kinds.SAFE_WALK: m.onSafeWalk((SafeWalkEvent) e); break;
            case Kinds.JUMP: m.onJump((JumpEvent) e); break;
            case Kinds.SWAP_ITEM: m.onSwapItem((SwapItemEvent) e); break;
            case Kinds.WINDOW_CLICK: m.onWindowClick((WindowClickEvent) e); break;
            case Kinds.RESIZE: m.onResize((ResizeEvent) e); break;
            case Kinds.RENDER_LIVING: m.onRenderLiving((RenderLivingEvent) e); break;
            default: break;
        }
    }
}
