package org.aussiebox.ccosmo.client.render.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.cca.ShimmerComponent;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;
import org.aussiebox.ccosmo.item.ModItems;
import org.aussiebox.ccosmo.item.custom.PyrrhianBeltItem;
import org.aussiebox.ccosmo.item.custom.ShimmerToolItem;
import org.aussiebox.ccosmo.util.CCOSMOUtil;

import java.util.Objects;

public class ShimmerToolChargeRenderer {

    public static void render(DrawContext context, RenderTickCounter counter) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        if (!(player.getMainHandStack().getItem() instanceof ShimmerToolItem)) return;
        ItemStack stack = player.getMainHandStack();

        int maxCharges = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_MAX_CHARGES, 5);
        int charges = stack.getOrDefault(ModDataComponentTypes.SHIMMER_TOOL_CHARGES, 0);
        int emptySlots = maxCharges - charges;
        int fullSlots = maxCharges - emptySlots;
        int slotsRendered = 0;

        Text bar = Text.empty();

        if (fullSlots > 0) {
            for (int i = 0; i < fullSlots; i++) {
                if (slotsRendered == 0) bar = Text.literal("S-");
                else if (slotsRendered == maxCharges-1) bar = bar.copy().append("E");
                else bar = bar.copy().append("L-");
                slotsRendered++;
            }
        }
        if (emptySlots >= 0) {
            for (int i = 0; i < emptySlots; i++) {
                if (slotsRendered == 0) bar = Text.literal("s-");
                else if (slotsRendered == maxCharges-1) bar = bar.copy().append("e");
                else bar = bar.copy().append("l-");
                slotsRendered++;
            }
        }

        bar = bar.copy().setStyle(Style.EMPTY.withFont(CCOSMO.id("shimmer_tool_bar")));

        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int y = height/2+5+textRenderer.fontHeight;

        TrinketComponent trinketComponent = TrinketComponent.KEY.get(player);
        double flightTime = trinketComponent.getPyrrhianBeltFlightTime();
        double flightTimeMaximum = PyrrhianBeltItem.getBeltFlyTime(player);
        if (CCOSMOUtil.playerHasTrinket(player, ModItems.PYRRHIAN_BELT) && flightTime < flightTimeMaximum) {
            y += 10;
            if (Objects.equals(player.getUuidAsString(), "fdf5edf6-f202-47fe-98f0-68a60d68b0d5"))
                y += 2 + textRenderer.fontHeight;
        }

        context.drawText(
                textRenderer,
                bar,
                width/2 - (textRenderer.getWidth(bar)/2),
                y,
                0xFFFFFF,
                false
        );

        if (CCOSMOUtil.stackHasEnchantment(player.getWorld(), stack, CCOSMOConstants.SHIMMERSEEP_ENCHANT)) {
            int totalSeconds = ShimmerComponent.KEY.get(player).shimmerseepTicks/20;

            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            Text text = Text.literal(minutes + ":" + (seconds < 10 ? "0" : "") + seconds);
            context.drawText(
                    textRenderer,
                    text,
                    width/2 - (textRenderer.getWidth(text)/2),
                    y+10,
                    0xA024FF,
                    true
            );
        }
    }

}
