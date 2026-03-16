package org.aussiebox.ccosmo.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.aussiebox.ccosmo.CCOSMO;

public class ModEntities {

    public static final EntityType<DragonflameCactusEntity> DragonflameCactusEntityType = Registry.register(Registries.ENTITY_TYPE,
            CCOSMO.id("dragonflame_cactus"),
            EntityType.Builder.<DragonflameCactusEntity>create(DragonflameCactusEntity::new, SpawnGroup.MISC)
                    .dimensions(0.4F, 0.4F).build("dragonflame_cactus")
    );
    public static final EntityType<PickarangEntity> PickarangEntityType = Registry.register(Registries.ENTITY_TYPE,
            CCOSMO.id("pickarang"),
            EntityType.Builder.<PickarangEntity>create(PickarangEntity::new, SpawnGroup.MISC)
                    .dimensions(1.25F, 0.2F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20).build("pickarang")
    );
    public static final EntityType<ShimmerforkEntity> FluidityTridentEntityType = Registry.register(Registries.ENTITY_TYPE,
            CCOSMO.id("fluidity_trident"),
            EntityType.Builder.<ShimmerforkEntity>create(ShimmerforkEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20).build("fluidity_trident")
    );

    public static void init() {
        CCOSMO.LOGGER.info("Registering Entities for " + CCOSMO.MOD_ID);
    }
}
