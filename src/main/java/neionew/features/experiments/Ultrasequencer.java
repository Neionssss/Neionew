package neionew.features.experiments;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

import java.util.HashMap;
import java.util.Map;

import static ncore.NCore.mc;

public class Ultrasequencer {

    public final HashMap<Slot, Integer> ultraSequence = new HashMap<>();
    private boolean cleared = false;

    public void onDraw(ChestMenu screen) {
        if (ultraSequence.size() > 9 - Config.serumsCount()) {
            LocationChecker.delay(() -> mc.player.closeContainer());
            return;
        }
        var slots = screen.slots;
        var deciderItem = slots.get(49).getItem().getItem();
        for (Slot slot : slots) {
            if (!slot.hasItem() || slot.index < 9) continue;
            var stack = slot.getItem();
            if ((Block.byItem(stack.getItem()) instanceof StainedGlassPaneBlock)) continue;
            if (deciderItem == Items.GLOWSTONE) {
                if (!cleared) {
                    cleared = true;
                    ultraSequence.clear();
                }
                 if (slot.index < 45) ultraSequence.putIfAbsent(slot, stack.count());
            } else if (!ultraSequence.isEmpty() && deciderItem == Items.CLOCK) {
                cleared = false;
                ultraSequence.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).ifPresent(s -> LocationChecker.pickUpSlot(Config.autoExperimentsDelay(), screen.containerId, s.index, () -> ultraSequence.remove(s)));
            }
        }
    }
}
