package myau.module.modules;

import myau.util.TextUtil;
import myau.module.Module;
import myau.property.properties.BooleanProperty;
import myau.property.properties.FloatProperty;

import java.util.Locale;

public class Jesus extends Module {
        public final FloatProperty speed = new FloatProperty("speed", 2.5F, 0.0F, 3.0F);
    public final BooleanProperty noPush = new BooleanProperty("no-push", true);
    public final BooleanProperty groundOnly = new BooleanProperty("ground-only", true);

    public Jesus() {
        super("Jesus", false);
    
        add(speed);
        add(noPush);
        add(groundOnly);
    }

    @Override
    public String[] getSuffix() {
        return new String[]{TextUtil.fmt(this.speed.getValue(), 0, 2)};
    }
}
