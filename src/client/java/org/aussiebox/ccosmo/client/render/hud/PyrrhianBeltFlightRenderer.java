package org.aussiebox.ccosmo.client.render.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianBeltItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

import java.util.Objects;

public class PyrrhianBeltFlightRenderer {

    public static final Identifier BACKGROUND = CCOSMO.id("textures/gui/pyrrhian_belt/background.png");
    public static final Identifier PROGRESS = CCOSMO.id("textures/gui/pyrrhian_belt/progress.png");

    public static void render(DrawContext context, RenderTickCounter counter) {
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        if (player.isInCreativeMode()) return;

        TrinketComponent trinketComponent = TrinketComponent.KEY.get(player);
        double flightTime = trinketComponent.getPyrrhianBeltFlightTime();
        double flightTimeMaximum = PyrrhianBeltItem.getBeltFlyTime(player);
        double glideTime = trinketComponent.getPyrrhianBeltGlideTime();
        double glideTimeMaximum = PyrrhianBeltItem.getBeltGlideTime(player);

        if (!player.getAbilities().flying)
            if (flightTime >= flightTimeMaximum && glideTime >= glideTimeMaximum) return;
        if (!CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_BELT))
            return;

        int progress = (int) (flightTime*79/flightTimeMaximum); // ({int}*{textureWidth}/{maxInt})
        context.drawTexture(
                BACKGROUND,
                width/2-41,
                height/2+14,
                0,
                0,
                81,
                8,
                81,
                8
        );
        context.drawTexture(
                PROGRESS,
                width/2-41+1,
                height/2+14,
                progress,
                8,
                0,
                0,
                progress,
                8,
                79,
                8
        );

        progress = (int) (glideTime*79/glideTimeMaximum); // ({int}*{textureWidth}/{maxInt})
        context.drawTexture(
                BACKGROUND,
                width/2-41,
                height/2+24,
                0,
                0,
                81,
                8,
                81,
                8
        );
        context.drawTexture(
                PROGRESS,
                width/2-41+1,
                height/2+24,
                progress,
                8,
                0,
                0,
                progress,
                8,
                79,
                8
        );

        if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5")) {
            context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("tip.ccosmo.pyrrhian_belt_buffed"),
                    width/2-(MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("tip.ccosmo.pyrrhian_belt_buffed"))/2),
                    height/2+14+MinecraftClient.getInstance().textRenderer.fontHeight+2,
                    0xFFFF55FF,
                    true
            );
        }
    }

}
