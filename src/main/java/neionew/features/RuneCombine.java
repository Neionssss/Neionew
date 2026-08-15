package neionew.features;

import io.netty.util.internal.ConcurrentSet;
import ncore.TextUtils;
import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import static ncore.TextUtils.safeName;
import static ncore.TextUtils.unformattedText;
import static ncore.NCore.mc;

public class RuneCombine {

    public static String rune;
    private static final ConcurrentSet<Slot> q = new ConcurrentSet<>();

    public static void onContainer(ChestMenu screen) {
        if (rune == null) return;
        var slots = screen.slots;
        if (slots.stream().anyMatch(s -> unformattedText(safeName(s.getItem())).contains("Error!"))) {
            TextUtils.sendMessage(Component.literal("Item reset, since it is not combinable"));
            rune = null;
            mc.player.closeContainer();
            return;
        }
        for (var slot : screen.slots) {
            var stack = slot.getItem();
            if (stack.getItem() != Items.PLAYER_HEAD) continue;
            if (stack.getDisplayName().getString().equals(rune)) q.add(slot);
        }
        var frame = slots.get(13);
        var pedestal = slots.get(31);

        if (!frame.hasItem() || frame.getItem().getItem() != Item.byBlock(Blocks.END_PORTAL_FRAME) || !pedestal.hasItem()) return;
        var pedItem = pedestal.getItem();
        if (pedItem.getItem() == Items.BARRIER) {
            for (var s : q) {
                if (s.container == mc.player.getInventory() && (q.size() > 1 || s.getItem().count() > 1)) LocationChecker.quickMoveSlot(Config.moveDelay(), screen.containerId, s.index, () -> {});
            }
            q.clear();
        } else if (slots.get(19).hasItem() && slots.get(25).hasItem()) {
            LocationChecker.pickUpSlot(Config.combineDelay(), screen.containerId, frame.index, () -> {});
        } else if (pedItem.getItem() == Items.PLAYER_HEAD) LocationChecker.quickMoveSlot(Config.combineDelay(), screen.containerId, pedestal.index, () -> {});
    }
}