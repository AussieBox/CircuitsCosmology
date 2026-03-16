package org.aussiebox.ccosmo;

import net.minecraft.entity.player.PlayerEntity;
import org.aussiebox.ccosmo.cca.DragonflameCactusComponent;
import org.aussiebox.ccosmo.cca.ShimmerComponent;
import org.aussiebox.ccosmo.cca.TrinketComponent;
import org.aussiebox.ccosmo.entity.DragonflameCactusEntity;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class CCOSMOComponents implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, ShimmerComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY)
                .end(ShimmerComponent::new);
        registry.beginRegistration(PlayerEntity.class, TrinketComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
                .end(TrinketComponent::new);

        registry.beginRegistration(DragonflameCactusEntity.class, DragonflameCactusComponent.KEY)
                .respawnStrategy(RespawnCopyStrategy.NEVER_COPY)
                .end(DragonflameCactusComponent::new);
    }

}
