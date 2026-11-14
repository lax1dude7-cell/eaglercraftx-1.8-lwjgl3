package myau.management;

import myau.module.Module;
import myau.module.ModuleManager;
import myau.property.Property;
import myau.property.properties.BooleanProperty;
import myau.property.properties.ColorProperty;
import myau.property.properties.FloatProperty;
import myau.property.properties.IntProperty;
import myau.property.properties.ModeProperty;
import myau.property.properties.TextProperty;
import myau.util.Compat;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

import java.util.List;

/** Persists module state and settings through Eaglercraft storage. */
public final class ConfigManager {
    private static final String STORAGE_KEY = "myau_config";
    private static final Logger logger = LogManager.getLogger("MyauConfig");
    private final ModuleManager modules;
    private boolean loading;

    public ConfigManager(ModuleManager modules) {
        this.modules = modules;
    }

    public void load() {
        loading = true;
        try {
            List<Module> all = modules.all();
            for (int i = 0; i < all.size(); i++) {
                Module module = all.get(i);
                boolean enabledByDefault = "HUD".equals(module.getName())
                        || "DynamicIsland".equals(module.getName());
                module.setEnabledSilently(enabledByDefault);
            }

            String saved = Compat.storageGet(STORAGE_KEY);
            if (saved == null || saved.length() == 0) return;

            String[] lines = saved.split("\n");
            for (int i = 0; i < lines.length; i++) {
                try {
                    loadEntry(lines[i]);
                } catch (NumberFormatException e) {
                    logger.warn("Skipping invalid Myau config entry " + i);
                }
            }
        } finally {
            loading = false;
        }
    }

    public void save() {
        if (loading) return;

        StringBuilder saved = new StringBuilder();
        List<Module> all = modules.all();
        for (int i = 0; i < all.size(); i++) {
            Module module = all.get(i);
            appendRow(saved, new String[] {
                    "M", module.getName(), module.isEnabled() ? "1" : "0",
                    String.valueOf(module.getKey()), module.isHidden() ? "1" : "0"
            });

            List<Property> properties = module.getProperties();
            for (int j = 0; j < properties.size(); j++) {
                Property property = properties.get(j);
                String type = propertyType(property);
                if (type == null) continue;

                appendRow(saved, new String[] {
                        "P", module.getName(), property.getName(), type, propertyValue(property)
                });
            }
        }

        Compat.storageSet(STORAGE_KEY, saved.toString());
    }

    private void loadEntry(String line) {
        String[] fields = line.split("\t", -1);
        if (fields.length == 0) return;

        if ("M".equals(fields[0]) && fields.length == 5) {
            Module module = modules.getModule(unescape(fields[1]));
            if (module == null) return;
            if (!("0".equals(fields[2]) || "1".equals(fields[2]))
                    || !("0".equals(fields[4]) || "1".equals(fields[4]))) return;
            int key = parseInt(fields[3]);
            module.setEnabledSilently("1".equals(fields[2]));
            module.setKey(key);
            module.setHidden("1".equals(fields[4]));
        } else if ("P".equals(fields[0]) && fields.length == 5) {
            Module module = modules.getModule(unescape(fields[1]));
            if (module == null) return;
            Property property = findProperty(module, unescape(fields[2]));
            if (property == null) return;
            String type = propertyType(property);
            if (type == null || !type.equals(fields[3])) return;
            setPropertyValue(property, unescape(fields[4]));
        }
    }

    private static Property findProperty(Module module, String name) {
        List<Property> properties = module.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            if (property.getName().equals(name)) return property;
        }
        return null;
    }

    private static String propertyType(Property property) {
        if (property instanceof BooleanProperty) return "B";
        if (property instanceof FloatProperty) return "F";
        if (property instanceof IntProperty) return "I";
        if (property instanceof ModeProperty) return "O";
        if (property instanceof ColorProperty) return "C";
        if (property instanceof TextProperty) return "T";
        return null;
    }

    private static String propertyValue(Property property) {
        if (property instanceof BooleanProperty) return ((BooleanProperty) property).getValue() ? "1" : "0";
        if (property instanceof FloatProperty) return String.valueOf(((FloatProperty) property).getValue());
        if (property instanceof IntProperty) return String.valueOf(((IntProperty) property).getValue());
        if (property instanceof ModeProperty) return String.valueOf(((ModeProperty) property).getValue());
        if (property instanceof ColorProperty) return String.valueOf(((ColorProperty) property).getValue());
        if (property instanceof TextProperty) return ((TextProperty) property).getValue();
        return "";
    }

    private static void setPropertyValue(Property property, String value) {
        if (property instanceof BooleanProperty) {
            if (!("0".equals(value) || "1".equals(value))) throw new NumberFormatException();
            ((BooleanProperty) property).setValue("1".equals(value));
        } else if (property instanceof FloatProperty) {
            float parsed = Float.parseFloat(value);
            if (Float.isNaN(parsed) || Float.isInfinite(parsed)) throw new NumberFormatException();
            ((FloatProperty) property).setValue(parsed);
        } else if (property instanceof IntProperty) {
            ((IntProperty) property).setValue(parseInt(value));
        } else if (property instanceof ModeProperty) {
            ((ModeProperty) property).setValue(parseInt(value));
        } else if (property instanceof ColorProperty) {
            ((ColorProperty) property).setValue(parseInt(value));
        } else if (property instanceof TextProperty) {
            ((TextProperty) property).setValue(value);
        }
    }

    private static int parseInt(String value) {
        return Integer.parseInt(value);
    }

    private static void appendRow(StringBuilder out, String[] fields) {
        if (out.length() > 0) out.append('\n');
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) out.append('\t');
            out.append(escape(fields[i]));
        }
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\') escaped.append("\\\\");
            else if (c == '\t') escaped.append("\\t");
            else if (c == '\n') escaped.append("\\n");
            else if (c == '\r') escaped.append("\\r");
            else escaped.append(c);
        }
        return escaped.toString();
    }

    private static String unescape(String value) {
        StringBuilder unescaped = new StringBuilder(value.length());
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (escaped) {
                if (c == 't') unescaped.append('\t');
                else if (c == 'n') unescaped.append('\n');
                else if (c == 'r') unescaped.append('\r');
                else unescaped.append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                unescaped.append(c);
            }
        }
        if (escaped) unescaped.append('\\');
        return unescaped.toString();
    }
}
