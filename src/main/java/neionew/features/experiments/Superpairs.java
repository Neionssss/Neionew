package neionew.features.experiments;

import ncore.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ncore.TextUtils.safeName;
import static ncore.TextUtils.unformattedText;

public class Superpairs {

    public final HashMap<Slot, Pair<ItemStack, String>> pairs = new HashMap<>();

    public void onDraw(GuiGraphicsExtractor context, Slot lc, List<Slot> slots) {
        if (lc.index < 9 || lc.index > 44) return;
        var stack = lc.getItem();
        if (!(stack.getItem() instanceof BlockItem bi && (bi.getBlock() instanceof StainedGlassBlock || bi.getBlock() instanceof StainedGlassPaneBlock))) pairs.put(lc, new Pair<>(stack, safeName(stack) + ":" + stack.getItem()));
        for (Map.Entry<Slot, Pair<ItemStack, String>> entry : pairs.entrySet()) {
            var cur = entry.getKey();
            var curItem = lc.getItem();
            var prev = entry.getValue();
            var prevStack = prev.getA();

            if ((safeName(curItem) + ":" + curItem.getItem()).equals(prev.getB()) && cur.index != lc.index && cur.hasItem() && slots.stream().anyMatch(s -> unformattedText(safeName(s.getItem())).contains("second button"))) RenderUtils.highlightSlot(context, cur, Color.yellow);
            context.item(prevStack, cur.x, cur.y);
            cur.getItem().set(DataComponents.CUSTOM_NAME, prevStack.getCustomName());
        }
    }
}
