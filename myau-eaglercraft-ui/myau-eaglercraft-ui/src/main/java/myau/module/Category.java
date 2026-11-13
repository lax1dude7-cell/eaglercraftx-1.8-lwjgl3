package myau.module;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), RENDER("Render"), PLAYER("Player"), MISC("Misc");
    public final String displayName;
    Category(String n) { displayName = n; }
}
