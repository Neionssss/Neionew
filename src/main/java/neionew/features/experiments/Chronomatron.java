package neionew.features.experiments;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import static ncore.NCore.mc;

public class Chronomatron {

    private final List<Slot> order = new ArrayList<>();
    private boolean itemAdded = false;
    private int lastAddedSlot = 0;
    private int done = 0;

    public void reset() {
        order.clear();
        itemAdded = false;
        lastAddedSlot = 0;
        done = 0;
    }

    public void onDraw(ChestMenu screen) {
        var slots = screen.slots;
        var count = slots.get(4).getItem().count();
        if (count > 12 - Config.serumsCount()) {
            LocationChecker.delay(() -> mc.player.closeContainer());
            return;
        }
        var deciderItem = slots.get(49).getItem().getItem();
        if (deciderItem.equals(Item.byBlock(Blocks.GLOWSTONE))) {
            if (!Items.DYED_TERRACOTTA.asList().contains(slots.get(lastAddedSlot).getItem().getItem())) itemAdded = false;
        } else if (deciderItem == Items.CLOCK) {
            if (!itemAdded) {
                slots.subList(9, 44).stream().filter(slot -> Items.DYED_TERRACOTTA.asList().contains(slot.getItem().getItem())).findFirst().ifPresent(slot -> {
                    order.add(slot);
                    lastAddedSlot = slot.index;
                    itemAdded = true;
                    done = 0;
                });
            }
            if (!order.isEmpty() && done < order.size()) LocationChecker.pickUpSlot(Config.autoExperimentsDelay(), screen.containerId, order.get(done).index, () -> done++);
        }
    }

}
