package org.aussiebox.ccosmo.client.render.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianAnkletItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

import java.util.Objects;

public class PyrrhianAnkletFlightRenderer {
    public static int y = 0;

    public static void render(DrawContext context, RenderTickCounter counter) {
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        y = height/2+6+MinecraftClient.getInstance().textRenderer.fontHeight;

        if (player.isInCreativeMode()) return;

        TrinketComponent trinketComponent = TrinketComponent.KEY.get(player);
        double flightTime = trinketComponent.getPyrrhianAnkletFlightTime();
        double flightTimeMaximum = PyrrhianAnkletItem.getAnkletFlyTime(player);
        double glideTime = trinketComponent.getPyrrhianAnkletGlideTime();
        double glideTimeMaximum = PyrrhianAnkletItem.getAnkletGlideTime(player);

        if (!CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_ANKLET))
            return;
        if (!trinketComponent.isFlying() && !trinketComponent.isGliding())
            if (flightTime >= flightTimeMaximum && glideTime >= glideTimeMaximum)
                return;

        if (flightTime < flightTimeMaximum || trinketComponent.getGlideDamageCooldown() > 0) {
            int progress = (int) (flightTime*100/flightTimeMaximum); // ({int}*{textureWidth}/{maxInt})
            context.fill(
                    width/2-50,
                    y,
                    width/2+50,
                    y+2,
                    (trinketComponent.getFlightDamageCooldown() > 0) ? 0xFFAA0000 : 0xFF555555
            );
            context.fill(
                    width/2-50,
                    y,
                    width/2-50+(progress),
                    y+2,
                    (trinketComponent.getFlightDamageCooldown() > 0) ? 0xFFFF5555 : 0xFFBBF7FA
            );
            y += 4;
        }

        if (glideTime < glideTimeMaximum || trinketComponent.getGlideDamageCooldown() > 0) {
            int progress = (int) (glideTime*100/glideTimeMaximum); // ({int}*{textureWidth}/{maxInt})
            context.fill(
                    width/2-50,
                    y,
                    width/2+50,
                    y+2,
                    (trinketComponent.getGlideDamageCooldown() > 0) ? 0xFFAA0000 : 0xFF555555
            );
            context.fill(
                    width/2-50,
                    y,
                    width/2-50+(progress),
                    y+2,
                    (trinketComponent.getGlideDamageCooldown() > 0) ? 0xFFFF5555 : 0xFFF7FABB
            );
            y += 4;
        }

        if (y != height/2+6+MinecraftClient.getInstance().textRenderer.fontHeight) {
            if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) {
                context.drawText(
                        MinecraftClient.getInstance().textRenderer,
                        Text.translatable("tip.ccosmo.pyrrhian_anklet_buffed"),
                        width/2-(MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("tip.ccosmo.pyrrhian_anklet_buffed"))/2),
                        y+2,
                        0xFFD1ABF7,
                        true
                );
            }
        }
    }

}
