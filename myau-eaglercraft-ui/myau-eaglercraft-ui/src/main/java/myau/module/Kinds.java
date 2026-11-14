package myau.module;

/** Event kinds (used for per-event priorities). */
public final class Kinds {
    private Kinds() {}
    public static final int TICK = 0, UPDATE = 1, PLAYER_UPDATE = 2, POST_MOTION = 3, PACKET = 4, ATTACK = 5,
            LEFT_CLICK = 6, RIGHT_CLICK = 7, KEY = 8, MOVE_INPUT = 9, STRAFE = 10, KNOCKBACK = 11, LIVING_UPDATE = 12,
            LOAD_WORLD = 13, RENDER_3D = 14, PICK = 15, RAYTRACE = 16, HIT_BLOCK = 17, CANCEL_USE = 18, SAFE_WALK = 19,
            JUMP = 20, SWAP_ITEM = 21, WINDOW_CLICK = 22, RESIZE = 23, RENDER_LIVING = 24, RENDER_2D = 25, COUNT = 26;
}
