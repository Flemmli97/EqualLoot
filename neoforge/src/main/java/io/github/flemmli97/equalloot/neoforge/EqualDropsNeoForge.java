package io.github.flemmli97.equalloot.neoforge;

import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.data.LootConfigManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@Mod(value = EqualLoot.MODID)
public class EqualDropsNeoForge {

    public EqualDropsNeoForge() {
        IEventBus eventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(this::addReloadListener);
    }

    public void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(LootConfigManager.ID.identifier(), LootConfigManager.create(event.getServerResources().getRegistryLookup()));
    }
}
