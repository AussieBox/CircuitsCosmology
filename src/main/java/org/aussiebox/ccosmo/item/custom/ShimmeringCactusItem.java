package org.aussiebox.ccosmo.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.CCOSMOConstants;
import org.aussiebox.ccosmo.component.ModDataComponentTypes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ShimmeringCactusItem extends Item {
    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    };

    public ShimmeringCactusItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = super.getDefaultStack();
        itemStack.set(ModDataComponentTypes.SHIMMERING_CACTUS_FUSE, 20);
        itemStack.set(ModDataComponentTypes.SHIMMERING_CACTUS_LIT, false);
        return itemStack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        boolean lit = stack.getOrDefault(ModDataComponentTypes.SHIMMERING_CACTUS_LIT, false);
        int fuse = stack.getOrDefault(ModDataComponentTypes.SHIMMERING_CACTUS_FUSE, 20);

        if (lit && fuse == 60) {
            world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 2, 1);
        }

        if (lit && fuse < 60) {
            world.addParticle(ParticleTypes.SMOKE, entity.getPos().getX(), entity.getPos().getY()+entity.getHeight(), entity.getPos().getZ(), 0, 0, 0);
        }

        if (lit && fuse > 0) {
            stack.set(ModDataComponentTypes.SHIMMERING_CACTUS_FUSE, fuse-1);
            if (fuse-1 == 0) {
                stack.decrement(1);
                for (int i = 0; i < 20; i++) {
                    world.addParticle(CCOSMO.SHIMMERING_ALTAR_LARGE, entity.getPos().getX()+ThreadLocalRandom.current().nextDouble(-0.5, 0.5), entity.getPos().getY()+(entity.getHeight()/2)+ThreadLocalRandom.current().nextDouble(-0.75, 0.75), entity.getPos().getZ()+ThreadLocalRandom.current().nextDouble(-0.5, 0.5), 0, 0, 0);
                }
                DamageSource damageSource = entity.getDamageSources().create(CCOSMOConstants.SHIMMERING_CACTUS);
                world.createExplosion(null, damageSource, EXPLOSION_BEHAVIOR, entity.getPos(), 2.0F, false, World.ExplosionSourceType.NONE);
            }
        }

        if (entity instanceof PlayerEntity playerEntity) playerEntity.getInventory().markDirty();
    }



    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(target instanceof PlayerEntity playerTarget)) return false;
        playerTarget.getInventory().offerOrDrop(stack.copy());
        stack.decrement(1);
        if (attacker instanceof PlayerEntity playerAttacker) playerAttacker.getInventory().markDirty();
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!stack.getOrDefault(ModDataComponentTypes.SHIMMERING_CACTUS_LIT, false)) {
            stack.set(ModDataComponentTypes.SHIMMERING_CACTUS_LIT, true);
            user.getInventory().markDirty();
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> list, TooltipType type) {
        if (stack.contains(ModDataComponentTypes.SHIMMERING_CACTUS_FUSE)) {
            int fuseTicks = stack.getOrDefault(ModDataComponentTypes.SHIMMERING_CACTUS_FUSE, 20);
            String fuseSeconds = new DecimalFormat("0.00").format((double) fuseTicks/20);
            list.add(1, Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.1").withColor(0xAAAAAA)
                    .append(Text.literal(String.valueOf(fuseTicks)).withColor(0xFFAAAAAA))
                    .append(Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.2").withColor(0xFFAAAAAA))
                    .append(Text.literal(fuseSeconds).withColor(0xFFAAAAAA))
                    .append(Text.translatable("item.ccosmo.dragonflame_cactus.tooltip.fuse.3").withColor(0xFFAAAAAA)));
        }
    }
}
