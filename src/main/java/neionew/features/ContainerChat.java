package neionew.features;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Locale;

import static ncore.NCore.mc;

public class ContainerChat {

    private String message = "";
    private boolean typing = false;
    private boolean hovered = false;

    public void setTyping(boolean bool) {
        if (typing != bool) typing = bool;
    }

    public boolean getHovered() {
        return hovered;
    }

    public void onRender(GuiGraphicsExtractor ctx, int sw, int sh, int mx, int my) {
        int w = 200, h = 20, x = (sw - w) / 2, y = sh - 50;
        hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = hovered ? 0xAA000000 : 0x88000000;
        if (typing) color = Color.gray.darker().getRGB();
        ctx.fill(x, y, x + w, y + h, color);
        ctx.text(mc.font, "Chat", x, y - 15, 0xFFFFFFFF);
        ctx.text(mc.font, message, x + 5, y + 5, 0xFFFFFFFF);
    }

    public void onKeyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!typing) return;
        int key = input.key();
        switch (key) {
            case GLFW.GLFW_KEY_ENTER -> {
                mc.getConnection().sendChat(message);
                resetChat();
            }

            case GLFW.GLFW_KEY_SPACE -> message += " ";

            case GLFW.GLFW_KEY_ESCAPE -> resetChat();

            case GLFW.GLFW_KEY_BACKSPACE -> message = message.substring(0, message.length() - 1);
            default -> {
                if (isPrintableKey(key)) {
                    char character = getCharacterFromKey(key, input.modifiers());
                    if (character != 0) message += character;
                }
            }
        }
        cir.setReturnValue(false);
    }


    private char getCharacterFromKey(int key, int modifiers) {
        boolean shift = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
        String keyString = GLFW.glfwGetKeyName(key, 0);

        if (shift) {
            return switch (key) {
                case 49 -> '!';
                case 50 -> '@';
                case 51 -> '#';
                case 52 -> '$';
                case 53 -> '%';
                case 54 -> '^';
                case 55 -> '&';
                case 56 -> '*';
                case 57 -> '(';
                case 48 -> ')';
                case 45 -> '_';
                case 61 -> '+';
                case 47 -> '?';
                default -> keyString.toUpperCase(Locale.getDefault()).charAt(0);
            };
        } else return keyString.charAt(0);
    }


    private boolean isPrintableKey(int key) {
        return (key >= GLFW.GLFW_KEY_MINUS && key <= GLFW.GLFW_KEY_GRAVE_ACCENT || key == GLFW.GLFW_KEY_COMMA);
    }

    private void resetChat() {
        message = "";
        typing = false;
    }
}
