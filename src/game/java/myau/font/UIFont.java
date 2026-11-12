package myau.font;

/** Common font interface used by every UI/HUD class. y passed to draw methods is the TOP OF CAPITAL LETTERS. */
public interface UIFont {
    /** @return drawn width in GUI units */
    float drawString(String s, float x, float y, int color);
    float drawStringWithShadow(String s, float x, float y, int color);
    float getStringWidth(String s);
    /** Cap height in GUI units (use this to vertically centre text in a row). */
    float getHeight();
    /** Full line height in GUI units. */
    float getLineHeight();
}
