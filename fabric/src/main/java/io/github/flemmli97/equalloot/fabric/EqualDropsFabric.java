package io.github.flemmli97.equalloot.fabric;

import io.github.flemmli97.equalloot.data.LootConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EqualDropsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DataResourceLoader.get().registerReloader(LootConfigManager.ID.identifier(), LootConfigManager::create);
    }
}
