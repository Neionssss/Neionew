package neionew;

import neionew.gui.Tab;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.Entity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Neionew implements ClientModInitializer {

    public static final Random random = new Random();
    private static final List<Tab> tabs = List.of(
            new Tab(Config.Category.GENERAL),
            new Tab(Config.Category.ESP),
            new Tab(Config.Category.COMBINE),
            new Tab(Config.Category.KEYBIND)
    );
    public static final Map<Class<? extends Entity>, Color> entityColors = new HashMap<>();
    public static final ClickGUI clickGUI = new ClickGUI(tabs);

    public static void addToList(Class<? extends Entity> clazz) {
        if (entityColors.containsKey(clazz)) entityColors.remove(clazz); else entityColors.put(clazz, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }

    @Override
    public void onInitializeClient() {
        Config.loadConfig();
        if (Config.scannerMain()) Config.toggle("MGM Lobby Scanner");
    }

}