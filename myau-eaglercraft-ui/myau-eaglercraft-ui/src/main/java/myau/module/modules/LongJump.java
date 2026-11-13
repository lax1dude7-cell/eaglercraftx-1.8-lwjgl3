package myau.module.modules;

import myau.module.Category;

/** PLACEHOLDER until the Movement category is ported: exists so the Combat modules that reference it compile and run. */
public class LongJump extends StubModule {
    public LongJump() { super("LongJump", Category.MOVEMENT, false, false, new String[0]); }
    public boolean isJumping() { return false; }
}
