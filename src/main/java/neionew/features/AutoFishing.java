package neionew.features;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Items;

import static ncore.NCore.mc;
import static ncore.TextUtils.equalsOneOf;
import static ncore.TextUtils.safeName;

public class AutoFishing {

    private static int stage;
    private static boolean isAutoJumping = false;

    public static void onTick() {
        if (!LocationChecker.isOnSkyblock() || !Config.autoFish() || mc.gui.screen() instanceof AbstractContainerScreen<?> || mc.player.getMainHandItem().getItem() != Items.FISHING_ROD) return;
        var jumpKey = mc.options.keyJump;
        if (mc.player.isUnderWater() && Config.getUp() && mc.player.getAirSupply() <= 0) {
            jumpKey.setDown(true);
            isAutoJumping = true;
        } else if (isAutoJumping) {
            jumpKey.setDown(false);
            isAutoJumping = false;
        }
        boolean canCatch = false;
        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof ArmorStand as)) continue;
            if (equalsOneOf(safeName(as),"!!!", "0.1")) {
                canCatch = true;
                break;
            }
        }
        if (!canCatch) {
            stage = 0;
        } else if (stage == 0) {
            LocationChecker.useItem();
            stage = 1;
        } else if (stage == 1) {
            LocationChecker.useItem();
            stage = 2;
        }
    }
}
