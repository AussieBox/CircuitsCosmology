package org.aussiebox.ccosmo.cca;

import lombok.Getter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.aussiebox.ccosmo.CCOSMO;
import org.aussiebox.ccosmo.entity.DragonflameCactusEntity;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class DragonflameCactusComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<DragonflameCactusComponent> KEY = ComponentRegistry.getOrCreate(CCOSMO.id("dragonflame_cactus_component"), DragonflameCactusComponent.class);
    private final DragonflameCactusEntity entity;

    @Getter
    private int timer;

    public DragonflameCactusComponent(DragonflameCactusEntity entity) {
        this.entity = entity;
    }

    public void setTimer(int timer) {
        this.timer = timer;
        sync();
    }

    @Override
    public void serverTick() {
        timer--;
        sync();
    }

    public void sync() {
        KEY.sync(this.entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.timer = tag.contains("timer") ? tag.getInt("timer") : 0;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup wrapperLookup) {
        tag.putInt("timer", this.timer);
    }
}
