package neionew.mixins;

import neionew.LocationChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @Shadow
    protected abstract void ensureHasSentCarriedItem();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void startPrediction(ClientLevel level, PredictiveAction predictiveAction);

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void var1(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir) {
        var stack = player.getMainHandItem().getCustomName();
        if (!LocationChecker.isInMGM() || stack == null || !stack.getString().contains("Fishing Net")) return;
        ensureHasSentCarriedItem();
        startPrediction(minecraft.level, sequence -> new ServerboundUseItemOnPacket(hand, blockHit, sequence));
        cir.setReturnValue(InteractionResult.CONSUME);
    }
}
