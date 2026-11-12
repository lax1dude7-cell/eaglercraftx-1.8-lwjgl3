package myau.ui;

import java.util.concurrent.atomic.AtomicInteger;

public interface Component {
    void draw(AtomicInteger offset);
    void update(int mouseX, int mouseY);
    void mouseDown(int x, int y, int button);
    void mouseReleased(int x, int y, int button);
    void keyTyped(char c, int key);
    void setComponentStartAt(int newOffsetY);
    int getHeight();
    boolean isVisible();
    /** true while the component is waiting for typed input (bind / text box) so ESC doesn't close the GUI. */
    boolean isCapturingInput();
}
