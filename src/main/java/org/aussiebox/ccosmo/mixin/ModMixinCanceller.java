package org.aussiebox.ccosmo.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class ModMixinCanceller implements MixinCanceller {

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("moriyashiine.enchancement.mixin.config.toggleablepassives.ItemStackMixin")) return true;
        return false;
    }
}
