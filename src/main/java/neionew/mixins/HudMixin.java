package neionew.mixins;

import neionew.Config;
import neionew.features.GalateaTimer;
import neionew.LocationChecker;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.stream.StreamSupport;

import static ncore.NCore.mc;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
    private void var8(GuiGraphicsExtractor graphics, Player player, int yLineBase, int xRight, CallbackInfo ci) {
        if (LocationChecker.isOnSkyblock() && Config.hideFoodBar()) ci.cancel();
    }
    @Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
    private static void var9(GuiGraphicsExtractor graphics, Player player, int yLineBase, int numHealthRows, int healthRowHeight, int xLeft, CallbackInfo ci) {
        if (LocationChecker.isOnSkyblock() && Config.hideArmorBar()) ci.cancel();
    }
    @Inject(method = "extractRenderState", at = @At(value = "TAIL"))
    public void var090(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        GalateaTimer.handleTimer(graphics);
        if (Config.bobberCount() && LocationChecker.isOnSkyblock()) {
            long count = StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false).filter(e -> e instanceof FishingHook fh && fh.getPlayerOwner() != null && fh.getPlayerOwner() != mc.player && fh.distanceTo(mc.player) <= 30 && !fh.getPlayerOwner().getName().getString().equals("§bMartin ")).count();
            var color = Color.red;
            if (count > 0 && count < 5) color = Color.yellow; else if (count >= 5) color = Color.green;
            Component str = Component.literal("Active bobbers: ").withColor(Color.cyan.getRGB()).append(Component.literal(String.valueOf(count)).withColor(color.getRGB()));
            graphics.centeredText(mc.font, str, 4 + mc.font.width(str) / 2, 16, Color.white.getRGB());
        }
    }
}
