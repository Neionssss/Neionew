package neionew.features;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.util.internal.ConcurrentSet;
import ncore.TextUtils;
import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;

import java.util.*;

import static ncore.NCore.mc;

public class BookCombine {

    private static final ConcurrentSet<Slot> books = new ConcurrentSet<>();
    private static final Map<Tag, Integer> countByTag = new HashMap<>();
    private static final Map<Tag, Integer> levelOfTag = new HashMap<>();

    public static void reset() {
        countByTag.clear();
        levelOfTag.clear();
        books.clear();
    }

    private static boolean messageScheduled;

    public static void closeContainer() {
        if (!messageScheduled) {
            messageScheduled = true;
            LocationChecker.delay(() -> {
                TextUtils.sendMessage("[Book Combine] Nothing to combine");
                if (Config.disableWhenDone()) Config.toggle("Auto Book Combine");
                mc.player.closeContainer();
                messageScheduled = false;
            });
        }
    }

    public static void onContainer(ChestMenu screen) {
        var slots = screen.slots;
        for (Slot s : slots) {
            var stack = s.getItem();
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            var custom = stack.get(DataComponents.CUSTOM_DATA);
            if (custom == null) continue;
            var enchantmentsTag = custom.copyTag().get("enchantments");
            if (enchantmentsTag == null) continue;

            var jsonStr = enchantmentsTag.toString();
            try {
                var json = JsonParser.parseString(jsonStr).getAsJsonObject();
                int level = json.entrySet().iterator().next().getValue().getAsInt();

                countByTag.merge(enchantmentsTag, 1, Integer::sum);
                levelOfTag.putIfAbsent(enchantmentsTag, level);
            } catch (JsonSyntaxException _) {}
        }
        Tag targetTag = null;
        int bestLevel = Integer.MAX_VALUE;

        for (var entry : countByTag.entrySet()) {
            Tag tag = entry.getKey();
            int cnt = entry.getValue();
            int lvl = levelOfTag.get(tag);
            if (cnt >= 2 && lvl != 10 && lvl != 5 && lvl < bestLevel) {
                targetTag = tag;
                bestLevel = lvl;
            }
        }
        if (targetTag == null) {
            closeContainer();
            return;
        }
        books.clear();
        for (Slot s : slots) {
            var stack = s.getItem();
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            var custom = stack.get(DataComponents.CUSTOM_DATA);
            if (custom == null) continue;
            var tag = custom.copyTag().get("enchantments");
            if (Objects.equals(tag, targetTag)) books.add(s);
        }
        var anvil = slots.get(22);
        var avStack = anvil.getItem();
        var lore = avStack.get(DataComponents.LORE);
        if (lore == null) return;
        var line = lore.styledLines().getLast();
        if (line.contains(Component.literal("Click to combine!")) || line.contains(Component.literal("Claim the result item above!"))) LocationChecker.pickUpSlot(Config.combineDelay(), screen.containerId, anvil.index, () -> {});
        else if (!(avStack.getItem() instanceof SignItem) && books.size() >= 2) {
            for (Slot s : books.stream().filter(s -> s.container == mc.player.getInventory()).toList()) LocationChecker.quickMoveSlot(Config.moveDelay(), screen.containerId, s.index, () -> {});
        }
        reset();
    }
}
