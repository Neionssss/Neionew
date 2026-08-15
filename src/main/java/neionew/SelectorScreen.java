package neionew;

import ncore.NActionButton;
import neionew.gui.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static ncore.NCore.mc;

public class SelectorScreen extends Screen {

    private final HashSet<NActionButton> btnList = new HashSet<>();
    private final List<String> optList;
    private final Setting setting;
    private final Screen parent;

    public SelectorScreen(Setting setting, List<String> list, Screen parent) {
        super(Component.literal(setting.name));
        this.setting = setting;
        this.optList = list;
        this.parent = parent;
    }

    @Override
    protected void init() {
        btnList.clear();
        var x = width / 2 - 99;
        for (var s : optList) {
            var color = new Color(0, 0,200);
            if (Objects.equals(s, setting.value)) color = new Color(0, 150, 0);
            btnList.add(new NActionButton(x, 60 + optList.indexOf(s) * 25, 200, 20, s, color, () -> setting.value = s));
        }
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        graphics.text(mc.font, title, width / 2 - mc.font.width(title) / 2, 30, Color.white.getRGB());
        for (var btn : btnList) btn.draw(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(final @NotNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {}

    @Override
    public boolean mouseClicked(final @NotNull MouseButtonEvent event, final boolean doubleClick) {
        for (var btn : btnList) {
            if (btn.mouseClicked(event)) {
                mc.gui.setScreen(parent);
                setting.value = btn.label;
                Config.saveConfig();
            }
        }
        return false;
    }

}
