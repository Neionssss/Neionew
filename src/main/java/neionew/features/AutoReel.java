package neionew.features;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.Objects;

import static ncore.NCore.mc;
import static ncore.TextUtils.safeName;

public class AutoReel {

    private static boolean reelHandled;

    public static void onTick() {
        if (!Config.autoReel() || mc.player.getMainHandItem().getItem() != Items.LEAD) return;
        boolean canUse = false;

        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof ArmorStand as) || as.distanceTo(mc.player) > 6) continue;
            if (Objects.equals(safeName(as), "REEL")) {
                canUse = true;
                break;
            }
        }

        if (!reelHandled && canUse) {
            LocationChecker.useItem();
            reelHandled = true;
        } else if (!canUse) reelHandled = false;
    }

}
