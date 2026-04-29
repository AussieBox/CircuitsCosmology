package org.aussiebox.ccosmo.client.render.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianCuffItem;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

public class PyrrhianCuffFlightRenderer {
    public static int y = 0;

    public static void render(DrawContext context, RenderTickCounter counter) {
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        y = height/2+6+MinecraftClient.getInstance().textRenderer.fontHeight;

        if (player.getMainHandStack().getItem() instanceof ShimmerToolItem)
            y = ShimmerToolChargeRenderer.y+13;

        if (player.isInCreativeMode()) return;

        TrinketComponent trinketComponent = TrinketComponent.KEY.get(player);
        double flightTime = trinketComponent.getPyrrhianCuffFlightTime();
        double flightTimeMaximum = PyrrhianCuffItem.getCuffFlyTime(player);
        double glideTime = trinketComponent.getPyrrhianCuffGlideTime();
        double glideTimeMaximum = PyrrhianCuffItem.getCuffGlideTime(player);

        if (!CCOSMOUtil.playerHasTrinket(player, ModItems.pyrrhian_cuff))
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
            int progress = (int) (glideTime * 100 / glideTimeMaximum); // ({int}*{textureWidth}/{maxInt})
            context.fill(
                    width / 2 - 50,
                    y,
                    width / 2 + 50,
                    y + 2,
                    (trinketComponent.getGlideDamageCooldown() > 0) ? 0xFFAA0000 : 0xFF555555
            );
            context.fill(
                    width / 2 - 50,
                    y,
                    width / 2 - 50 + (progress),
                    y + 2,
                    (trinketComponent.getGlideDamageCooldown() > 0) ? 0xFFFF5555 : 0xFFF7FABB
            );
            y += 4;
        }
    }

}
