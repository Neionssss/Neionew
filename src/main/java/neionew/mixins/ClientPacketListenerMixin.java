package neionew.mixins;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    public abstract void sendCommand(String command);

    @Redirect(method = "handleLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setCameraEntity(Lnet/minecraft/world/entity/Entity;)V"))
    private void login(Minecraft instance, Entity cameraEntity) {
        instance.setCameraEntity(cameraEntity);
        if (Config.autoJoinSB() && !LocationChecker.isOnSkyblock()) sendCommand("play sb");
    }
}