package neionew;

import com.google.gson.*;
import com.mojang.blaze3d.platform.InputConstants;
import neionew.gui.Setting;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static boolean lInventoryMove() { return get("Legit Inventory Move"); }
    public static boolean fullBright() { return get("Full Bright"); }
    public static boolean containerChat() { return get("Container Chat"); }
    public static boolean autoExperiments() { return get("Auto Experiments"); }
    public static int serumsCount() { return get("Serums count"); }
    public static int autoExperimentsDelay() { return get("Experiments Click Delay"); }
    public static boolean bookCombine() { return get("Auto Book Combine"); }
    public static boolean runeCombine() { return get("Auto Rune Combine"); }
    public static boolean disableWhenDone() { return get("Disable when done"); }
    public static int combineDelay() { return get("Move Delay"); }
    public static int moveDelay() { return get("Combine Delay"); }
    public static boolean autoFish() { return get("Auto Fishing"); }
    public static boolean autoReel() { return get("Auto-Reel Lasso"); }
    public static boolean bobberCount() { return get ("Bobber count"); }
    public static boolean getUp() { return get("Leave the water if drowning"); }
    public static boolean autoJoinSB() { return get("Auto Join SB"); }
    public static boolean hideFoodBar() { return get("Hide Food Bar"); }
    public static boolean hideArmorBar() { return get("Hide Armor Bar"); }
    public static String packMode() { return get("Server Pack Mode"); }

    public static boolean scannerMain() { return get("MGM Lobby Scanner"); }
    public static boolean tracers() { return get("Tracers"); }
    public static boolean middleClickESP() { return get("Middle Click ESP"); }
    public static boolean pestESP() { return get("Pest ESP"); }
    public static boolean turtleESP() { return get("Turtle ESP"); }
    public static boolean hoglinESP() { return get("Torrhus Hoglin ESP"); }
    public static boolean ignore1k() { return get ("Ignore 1k"); }
    public static boolean ignore15k() { return get("Ignore 1.5k"); }

    public static boolean warpGalatea() { return get("Warp to MGM"); }
    public static boolean warpIsland() { return get("Warp to Island"); }
    public static boolean lobbyScannerWarpTimer() { return get("Warp Timer"); }

    public static int clickGUI() { return get("Click GUI"); }
    public static int lobbyScannerBind() { return get("MGM Lobby Scanner Bind"); }
    public static int warpLoch() { return get("Warp Loch"); }
    public static int warpIslandKey() { return get("Warp island"); }
    public static int partyWarp() { return get("Party Warp"); }

    private static final String CONFIG_FILE_PATH = "config/neionew.json";
    private static final List<Setting> settings = new ArrayList<>();

    public enum Category { GENERAL, ESP, COMBINE, SETTINGS, KEYBIND }
    public enum Type { BOOLEAN, SELECTOR, NUMBER, KEYBIND }

    static {
        var af = add("Auto Fishing", Category.GENERAL);
        add("Auto-Reel Lasso", Category.GENERAL);
        af.addSubSetting(new Setting("Leave the water if drowning", Category.SETTINGS));
        af.addSubSetting(new Setting("Bobber count", Category.SETTINGS));
        add("Legit Inventory Move", Category.GENERAL);
        add("Full Bright", Category.GENERAL);
        add("Container Chat", Category.GENERAL);
        add("Auto Join SB", Category.GENERAL);
        add("Hide Food Bar", Category.GENERAL);
        add("Hide Armor Bar", Category.GENERAL);
        var ae = add("Auto Experiments", Category.GENERAL);
        ae.addSubSetting(new Setting("Serums count",Category.SETTINGS, 3, 0, 3));
        ae.addSubSetting(new Setting("Experiments Click Delay", Category.SETTINGS, 300, 150, 1000));
        add("Server Pack Mode", Category.GENERAL, List.of("Disabled", "Auto-Accept", "Auto-Reject"));

        add("Tracers", Category.ESP);
        add("Middle Click ESP", Category.ESP);
        var te = add("Turtle ESP", Category.ESP);
        te.addSubSetting(new Setting("Ignore 1k", Category.SETTINGS));
        te.addSubSetting(new Setting("Ignore 1.5k", Category.SETTINGS));
        add("Torrhus Hoglin ESP", Category.ESP);
        add("Pest ESP", Category.ESP);


        var sc = add("MGM Lobby Scanner", Category.GENERAL);
        sc.addSubSetting(new Setting("Warp to MGM", Category.SETTINGS, true));
        sc.addSubSetting(new Setting("Warp to Island", Category.SETTINGS, true));
        sc.addSubSetting(new Setting("Warp Timer", Category.SETTINGS, true));
        var abc = add("Auto Book Combine", Category.COMBINE);
        abc.addSubSetting(new Setting("Disable when done", Category.SETTINGS));
        add("Auto Rune Combine", Category.COMBINE);
        add("Move Delay", Category.COMBINE, 400, 150, 1000);
        add("Combine Delay", Category.COMBINE, 500, 150, 1000);

        add("Click GUI", InputConstants.KEY_RSHIFT);
        add("MGM Lobby Scanner Bind", -1);
        add("Warp Loch", -1);
        add("Warp island", -1);
        add("Party Warp", -1);
    }

    private static Setting add(String key, Object... args) {
        var s = new Setting(key, args);
        settings.add(s);
        return s;
    }

    public static void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE_PATH);

        if (!Files.exists(configPath)) {
            System.out.println("Config not found, creating default...");
            saveConfig();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) throw new JsonSyntaxException("Config File is corrupted");
            JsonObject json = parsed.getAsJsonObject();

            for (Setting s : settings) {
                if (!json.has(s.name)) continue;
                try {
                    switch (s.type) {
                        case BOOLEAN -> s.value = json.get(s.name).getAsBoolean();
                        case SELECTOR -> s.value = json.get(s.name).getAsString();
                        case NUMBER, KEYBIND -> s.value = json.get(s.name).getAsInt();
                    }
                } catch (Exception ex) {
                    System.out.println("Invalid value for " + s.name + ", keeping default");
                }

                if (!s.subSettings.isEmpty()) {
                    for (Setting w : s.subSettings) {
                        if (!json.has(w.name)) continue;
                        try {
                            switch (w.type) {
                                case BOOLEAN -> w.value = json.get(w.name).getAsBoolean();
                                case SELECTOR -> s.value = json.get(s.name).getAsString();
                                case NUMBER -> w.value = json.get(w.name).getAsInt();
                            }
                        } catch (Exception ex) {
                            System.out.println("Invalid subsetting value for " + w.name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading config (corrupted or unreadable): " + e);
        }
    }

    public static void saveConfig() {
        Path configPath = Paths.get(CONFIG_FILE_PATH);
        Path tempPath = Paths.get(CONFIG_FILE_PATH + ".tmp");

        JsonObject json = new JsonObject();
        for (Setting s : settings) {
            json.addProperty(s.name, s.value.toString());
            if (!s.subSettings.isEmpty()) for (Setting w : s.subSettings) json.addProperty(w.name, w.value.toString());
        }
        String jsonText = new GsonBuilder().setPrettyPrinting().create().toJson(json);
        try {
            Files.writeString(tempPath, jsonText);
            Files.move(tempPath, configPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.out.println("Error saving config: " + e);
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
        }
    }

    public static List<Setting> getByCategory(Category category) {
        return settings.stream().filter(s -> s.category == category).toList();
    }

    public static <T> T get(String key) {
        return (T) find(key).value;
    }

    public static void set(String key, Object value) {
        Setting s = find(key);
        if (s.type == Type.NUMBER) s.value = Math.max(s.min, Math.min(s.max, (int) value)); else s.value = value;
    }

    public static void toggle(String key) {
        Setting s = find(key);
        s.value = !(boolean) s.value;
    }

    public static Setting find(String key) {
        for (Setting s : settings) {
            if (s.name.equalsIgnoreCase(key)) return s;
            for (Setting w : s.subSettings) {
                if (w.name.equalsIgnoreCase(key)) return w;
            }
        }
        return null;
    }
}