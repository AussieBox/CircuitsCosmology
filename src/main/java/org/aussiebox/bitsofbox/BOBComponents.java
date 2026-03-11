package org.aussiebox.bitsofbox;

import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.bitsofbox.cca.ShimmerComponent;
import org.aussiebox.bitsofbox.cca.TrinketComponent;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class BOBComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.beginRegistration(PlayerEntity.class, ShimmerComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
                .end(ShimmerComponent::new);
        registry.beginRegistration(PlayerEntity.class, TrinketComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
                .end(TrinketComponent::new);
    }

}
