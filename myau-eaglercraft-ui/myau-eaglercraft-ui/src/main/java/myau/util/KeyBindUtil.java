package myau.util;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindUtil {
    public static String getKeyName(int keyCode) { return Compat.keyName(keyCode); }

    public static boolean isKeyDown(int keyCode) { return Compat.isKeyDown(keyCode); }

    public static void updateKeyState(int keyCode) {
        KeyBindUtil.setKeyBindState(keyCode, Compat.isKeyDown(keyCode));
    }

    public static void setKeyBindState(int keyCode, boolean pressed) { KeyBinding.setKeyBindState(keyCode, pressed); }

    public static void pressKeyOnce(int keyCode) { KeyBinding.onTick(keyCode); }
}
