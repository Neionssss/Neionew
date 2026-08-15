package neionew.gui;

import neionew.Config;

import java.util.ArrayList;
import java.util.List;

public class Setting {
    public final String name;
    public final Config.Category category;
    public final Config.Type type;
    public Object value;
    public int min, max;
    public List<String> btnList;
    public final List<Setting> subSettings = new ArrayList<>();

    public Setting(String key, Object... args) {
        this.name = key;

        switch (args[0]) {
            case Config.Category cat when args.length == 1 || (args.length == 2 && args[1] instanceof Boolean) -> {
                this.category = cat;
                this.type = Config.Type.BOOLEAN;
                this.value = args.length == 2;
            }
            case Config.Category cat when args.length == 4 -> {
                this.category = cat;
                this.type = Config.Type.NUMBER;
                this.value = args[1];
                this.min = (int) args[2];
                this.max = (int) args[3];
            }
            case Config.Category cat when args.length == 2 && args[1] instanceof List -> {
                this.category = cat;
                this.type = Config.Type.SELECTOR;
                this.value = "Disabled";
                this.btnList = (List<String>) args[1];
            }
            case Integer defKey when args.length == 1 -> {
                this.category = Config.Category.KEYBIND;
                this.type = Config.Type.KEYBIND;
                this.value = defKey;
            }
            default -> throw new IllegalArgumentException("Invalid Setting arguments");
        }
    }

    public void addSubSetting(Setting setting) {
        subSettings.add(setting);
    }
}
