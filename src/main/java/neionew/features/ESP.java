package neionew.features;

import ncore.RenderUtils;
import neionew.Config;
import neionew.Neionew;
import neionew.LocationChecker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

import java.awt.Color;
import java.util.Map;

import static ncore.RenderUtils.drawText;
import static ncore.RenderUtils.interpolateEntity;
import static ncore.TextUtils.safeName;
import static ncore.NCore.mc;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class ESP {

    public static boolean leaving = true;
    public static boolean getLeaving() {
        return leaving;
    }

    private static boolean match1k(String name) {
        return name.contains("/1,000") || name.contains("/4,000");
    }

    private static boolean match15k(String name) {
        return name.contains("/1,500") || name.contains("/6,000");
    }

    public static void renderNameTags(LivingEntity e) {
        var interpPos = interpolateEntity(e);
        var distance = mc.player.distanceToSqr(interpPos);
        var scale = 0.3 + 0.0015 * distance;

        var name = safeName(e);

        var turtleName = "Big Turtle";
        var color = Color.RED;

        if (match1k(name)) {
            turtleName = "Small Turtle";
            color = Color.PINK;
        } else if (match15k(name)) {
            turtleName = "Medium Turtle";
            color = Color.YELLOW;
        }

        var hp = substringBefore(substringAfter(name, "Shellwise"), "/");

        drawText("§l" + turtleName + " §f| §4HP:" + hp, interpPos.add(0, e.getEyeHeight() + 0.3, 0), scale, color);
    }

    public static void render() {
        if (!LocationChecker.isOnSkyblock()) return;
        leaving = true;

        for (Entity e : mc.level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity li)) continue;
            boolean shouldIgnore = switch (li) {
                case ArmorStand a -> {
                    String name = safeName(a);
                    yield (Config.ignore1k() && match1k(name)) || (Config.ignore15k() && match15k(name));
                }
                case Turtle t -> {
                    float scale = t.getScale();
                    yield (Config.ignore1k() && scale == 1.0F) || (Config.ignore15k() && scale == 1.2F);
                }
                default -> false;
            };
            boolean turtleOk = Config.turtleESP() && !shouldIgnore;
            boolean match = ((li instanceof Turtle || safeName(li).contains("Shellwise")) && turtleOk && LocationChecker.isInMGM()) || (Config.hoglinESP() && li instanceof Armadillo && LocationChecker.isInTC());
            Color color = null;

            if (Config.middleClickESP() && !Neionew.entityColors.isEmpty()) {
                for (Map.Entry<Class<? extends Entity>, Color> entry : Neionew.entityColors.entrySet()) {
                    if (entry.getKey().isInstance(li)) {
                        match = true;
                        color = entry.getValue();
                        break;
                    }
                }
            }

            if (!match) continue;
            handleEntity(li, color);
            leaving = false;
        }
    }

    private static void handleEntity(LivingEntity entity, Color colorr) {
        Color color;
        if (entity instanceof Turtle || entity instanceof ArmorStand stand && safeName(stand).contains("Shellwise")) color = Color.green;
        else if (entity instanceOf Hoglin) color = Color.pink else color = colorr;

        if (entity instanceof ArmorStand) {
            entity.setCustomNameVisible(false);
            renderNameTags(entity);
            return;
        }

        if (Config.tracers()) RenderUtils.drawTracer(entity, color);
        RenderUtils.renderESP(entity, color);
        GalateaTimer.running = false;
    }

}
