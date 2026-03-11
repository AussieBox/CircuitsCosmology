package org.aussiebox.bitsofbox.client.mixin;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.aussiebox.bitsofbox.BOB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow
    protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 1))
    private void onInit(CallbackInfo ci) {
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "base_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "wooden_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "stone_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "copper_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "gold_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "iron_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "diamond_shimmerfork_hand")));
        this.loadItemModel(ModelIdentifier.ofInventoryVariant(Identifier.of(BOB.MOD_ID, "netherite_shimmerfork_hand")));
    }
}
