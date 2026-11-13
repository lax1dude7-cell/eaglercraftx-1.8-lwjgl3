package myau.module.modules;

import myau.module.Category;

/** PLACEHOLDER until the Player category is ported: exists so the Combat modules that reference it compile and run. */
public class AutoHeal extends StubModule {
    public AutoHeal() { super("AutoHeal", Category.PLAYER, false, false, new String[0]); }
    public boolean isSwitching() { return false; }
}
