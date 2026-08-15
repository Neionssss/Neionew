package neionew.mixins;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ncore.NCore.mc;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Shadow
    @Final
    private GpuBuffer emptyBuffer;

    @Shadow
    protected abstract FogType getFogType(Camera camera);

    @Shadow
    @Final
    public static int FOG_UBO_SIZE;

    @Inject(method = "getBuffer", at = @At("HEAD"), cancellable = true)
    private void s(FogRenderer.FogMode mode, CallbackInfoReturnable<GpuBufferSlice> cir) {
        var ed = mc.getEntityRenderDispatcher().camera;
        if (ed != null && getFogType(ed) == FogType.WATER) cir.setReturnValue(emptyBuffer.slice(0L, FOG_UBO_SIZE));
    }
}
