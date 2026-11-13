package myau.module.modules;

import myau.module.Category;

/** PLACEHOLDER until the Player category is ported: exists so the Combat modules that reference it compile and run. */
public class Scaffold extends StubModule {
    public Scaffold() { super("Scaffold", Category.PLAYER, false, false, new String[0]); }
}
