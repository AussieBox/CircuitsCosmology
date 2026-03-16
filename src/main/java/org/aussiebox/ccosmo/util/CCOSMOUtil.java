package org.aussiebox.ccosmo.util;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.aussiebox.ccosmo.CCOSMO;
import org.joml.Vector2f;

import java.util.*;

public class CCOSMOUtil {

    public static void grantAdvancement(ServerPlayerEntity player, String path) {
        AdvancementEntry advancement = Objects.requireNonNull(player.getEntityWorld().getServer()).getAdvancementLoader().get(Identifier.of(CCOSMO.MOD_ID, path));
        PlayerAdvancementTracker advancementTracker = player.getAdvancementTracker();
        if (!advancementTracker.getProgress(advancement).isDone()) {
            for (String missing : advancementTracker.getProgress(advancement).getUnobtainedCriteria()) {
                advancementTracker.grantCriterion(advancement, missing);
            }
        }
    }

    public static List<BlockPos> getAllBlockPosInBox(Box box) {
        List<BlockPos> positions = new ArrayList<>();

        for (int x = (int) box.minX; x <= box.maxX; x++) {
            for (int y = (int) box.minY; y <= box.maxY; y++) {
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }

    public static Map<BlockPos, BlockState> getAllBlocksInBox(World world, Box box, boolean includeAir) {
        Map<BlockPos, BlockState> blocks = new HashMap<>();

        for (int x = (int) box.minX; x <= box.maxX; x++) {
            for (int y = (int) box.minY; y <= box.maxY; y++) {
                for (int z = (int) box.minZ; z <= box.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).isOf(Blocks.AIR) || world.getBlockState(pos).isOf(Blocks.CAVE_AIR) || world.getBlockState(pos).isOf(Blocks.VOID_AIR)) {
                        if (includeAir) blocks.putIfAbsent(pos, world.getBlockState(pos));
                    } else {
                        blocks.putIfAbsent(pos, world.getBlockState(pos));
                    }
                }
            }
        }

        return blocks;
    }

    public static boolean stackHasEnchantment(World world, ItemStack stack, RegistryKey<Enchantment> enchantment) {
        var stackEnchants = stack.getEnchantments().getEnchantmentEntries();

        for (var entry : stackEnchants) {
            RegistryKey<Enchantment> key = world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(entry.getKey().getKey().get()).registryKey();
            if (key.equals(enchantment)) {
                return true;
            }
        }

        return false;
    }

    public static Vec3d shiftVecTowardsVec(Vec3d point, Vec3d shiftTowards, double amount) {
        return point.add(shiftTowards.subtract(point).normalize().multiply(amount));
    }

    public static boolean playerHasTrinket(PlayerEntity player, Item targetItem) {
        var component = TrinketsApi.getTrinketComponent(player);

        if (component.isPresent()) {
            for (Pair<SlotReference, ItemStack> equipped : component.get().getAllEquipped()) {
                ItemStack stack = equipped.getRight();
                if (stack.isOf(targetItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Vector2f> calculateCirclePoints(double centerX, double centerY, double radius, double rotationAngle, int numPoints) {
        List<Vector2f> points = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double baseAngle = 2 * Math.PI * i / numPoints;

            double totalAngle = baseAngle + rotationAngle;

            double x = centerX + (radius * Math.cos(totalAngle));
            double y = centerY + (radius * Math.sin(totalAngle));

            points.add(new Vector2f((float) x, (float) y));
        }
        return points;
    }

}
