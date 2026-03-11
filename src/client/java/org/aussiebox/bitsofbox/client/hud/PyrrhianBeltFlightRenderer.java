package org.aussiebox.bitsofbox.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.aussiebox.bitsofbox.BOB;
import org.aussiebox.bitsofbox.BOBConstants;
import org.aussiebox.bitsofbox.cca.TrinketComponent;
import org.aussiebox.bitsofbox.item.ModItems;
import org.aussiebox.bitsofbox.util.BOBUtil;

public class PyrrhianBeltFlightRenderer {

    public static final Identifier BACKGROUND = BOB.id("textures/gui/pyrrhian_belt/background.png");
    public static final Identifier PROGRESS = BOB.id("textures/gui/pyrrhian_belt/progress.png");

    public static void render(DrawContext context, RenderTickCounter counter) {
        if (MinecraftClient.getInstance().player == null) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        if (player.isInCreativeMode()) return;

        TrinketComponent trinketComponent = TrinketComponent.KEY.get(player);
        double flightTime = trinketComponent.getPyrrhianBeltFlightTime();

        if (!player.getAbilities().flying || !BOBUtil.playerHasTrinket(player, ModItems.PYRRHIAN_BELT))
            if (flightTime >= BOBConstants.pyrrhianBeltFlightTimeMaximum) return;

        int progress = (int) (flightTime*79/BOBConstants.pyrrhianBeltFlightTimeMaximum); // ({int}*{textureWidth}/{maxInt})
        context.drawTexture(
                BACKGROUND,
                width/2-41,
                height/2+8,
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
                height/2+8,
                progress,
                8,
                0,
                0,
                progress,
                8,
                79,
                8
        );
    }

}
