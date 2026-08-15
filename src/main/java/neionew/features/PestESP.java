package neionew.features;

import ncore.RenderUtils;
import neionew.Config;
import neionew.LocationChecker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Silverfish;

import java.awt.*;

import static ncore.NCore.mc;

public class PestESP {

    public static void render() {
        if (!LocationChecker.isInGarden() || !Config.pestESP()) return;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Bat) && !(entity instanceof Silverfish)) continue;
            Color color = Color.blue;
            if (entity instanceof Bat) color = Color.yellow;
            RenderUtils.renderESP(entity, color);
            if (Config.tracers()) RenderUtils.drawTracer(entity, color);
        }
    }
}
