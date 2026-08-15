package neionew.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import neionew.ClickGUI;
import neionew.Neionew;
import neionew.features.*;
import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    public abstract Window getWindow();

    @Shadow
    public abstract @Nullable ClientPacketListener getConnection();

    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    public Gui gui;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(method = "tick", at = @At("RETURN"))
    private void var4(CallbackInfo ci) {
        if (player == null || level == null) return;
        long now = System.currentTimeMillis();
        AutoReel.onTick();
        AutoFishing.onTick();
        AutoLobby.onTick();

        if (gui.screen() == null && now - LocationChecker.lastClick > 150) {
            boolean handled = false;

            if (var5(Config.clickGUI())) {
                gui.setScreen(Neionew.clickGUI);
                handled = true;
            } else if (var5(Config.lobbyScannerBind())) {
                Config.toggle("MGM Lobby Scanner");
                handled = true;
            } else if (LocationChecker.isOnSkyblock()) {
                if (var5(Config.warpLoch())) LocationChecker.warpToLoch();
                else if (var5(Config.warpIslandKey())) LocationChecker.warpToIsland();
                else if (var5(Config.partyWarp())) getConnection().sendCommand("party warp");
                else return;

                handled = true;
            }

            if (handled) LocationChecker.lastClick = now;
        }
    }

    @Unique
    private boolean var5(int keyCode) {
        if (keyCode <= 0) return false;
        return InputConstants.isKeyDown(getWindow(), keyCode);
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void onMiddleClick(CallbackInfo ci) {
        if (player == null || hitResult == null || !(hitResult instanceof EntityHitResult hit) || !LocationChecker.isOnSkyblock() || !Config.middleClickESP()) return;

        var entity = hit.getEntity();
        if (entity instanceof ArmorStand) return;
        Neionew.addToList(entity.getClass());
        ci.cancel();
    }

    @Inject(method = "setLevel", at = @At("TAIL"))
    private void var6(ClientLevel level, CallbackInfo ci) {
        GalateaTimer.startTimer();
    }
}
