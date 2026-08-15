package neionew.gui;

import com.mojang.blaze3d.platform.InputConstants;
import ncore.NButton;
import neionew.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

import static ncore.NCore.mc;
import static ncore.RenderUtils.roundedFill;

public class NKeyBind extends NButton {
    private boolean waiting = false;

    public NKeyBind(int x, int y, int width, int height, String label) {
        super(x, y, width, height, label);
    }

    @Override
    public void draw(GuiGraphicsExtractor context, double mouseX, double mouseY) {
        roundedFill(context, x, y, width, height, isHovered(mouseX, mouseY) ? 0xFF555555 : 0xFF333333);
        int key = Config.get(label);

        String display = waiting ? "Press key..." : (label + ": " + (key == -1 ? "NONE" : InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString().toUpperCase(Locale.ENGLISH)));
        int textX = x + width / 2;
        int textY = y + (height - mc.font.lineHeight) / 2;
        context.centeredText(mc.font, display, textX, textY, Color.white.getRGB());
    }

    @Override
    protected void onClick(final MouseButtonEvent event) {
        for (NButton b : NButton.getButtons()) {
            if (b instanceof NKeyBind kb && kb != this) kb.waiting = false;
        }

        waiting = !waiting;
    }

    public boolean keyPressed(int keyCode) {
        if (!waiting) return false;

        int code = keyCode;
        if (keyCode == InputConstants.KEY_ESCAPE && !Objects.equals(label, "Click GUI")) code = -1;

        Config.set(label, code);

        waiting = false;
        return true;
    }
}