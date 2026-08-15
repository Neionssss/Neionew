package neionew.features;

import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.awt.*;

import static ncore.NCore.mc;
import static neionew.Neionew.random;

public class GalateaTimer {
    public static boolean running = false;
    private static long startTimeMs = 0L;

    public static void startTimer() {
        running = true;
        startTimeMs = System.currentTimeMillis();
    }

    public static void handleTimer(GuiGraphicsExtractor ctx) {
        if (!Config.scannerMain() || !running) return;

        long elapsed = System.currentTimeMillis() - startTimeMs;
        long remainingMs = 5000 + random.nextLong(100) - elapsed;
        if (remainingMs <= 0) running = false;
        else if (Config.lobbyScannerWarpTimer()) {
            if ((LocationChecker.isInPrivateIsland() && !Config.warpGalatea()) || (LocationChecker.isInMGM() && !Config.warpIsland())) return;
            double seconds = remainingMs / 1000.0;
            Color color = Color.green;
            if (seconds <= 0.5) color = Color.red; else if (seconds <= 1.5) color = Color.yellow;
            var str = String.format("Warp cooldown: %.1fs", seconds);
            ctx.centeredText(mc.font, str, 4 + mc.font.width(str) / 2, 4, color.getRGB());
        }
    }
}