package neionew.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import neionew.ClickGUI;
import neionew.Config;
import neionew.SelectorScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ncore.NCore.mc;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void var919(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        var screen = (Screen) (Object) this;
        if (!Config.lInventoryMove() || !(screen instanceof ClickGUI || screen instanceof SelectorScreen || screen instanceof PauseScreen)) return;
        for (KeyMapping kb : mc.options.keyMappings) {
            InputConstants.Key boundKey = ((KeyMappingAccessor) kb).getKey();

            if (boundKey.getType() != InputConstants.Type.KEYSYM) continue;
            int keyCode = boundKey.getValue();
            if (keyCode == -1) continue;

            int state = GLFW.glfwGetKey(mc.getWindow().handle(), keyCode);
            kb.setDown(state == 1);
        }
    }
}
