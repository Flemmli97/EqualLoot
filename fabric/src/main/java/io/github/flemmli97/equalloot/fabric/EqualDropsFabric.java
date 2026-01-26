package io.github.flemmli97.equalloot.fabric;

import io.github.flemmli97.equalloot.data.LootConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EqualDropsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LootConfigManager.ID, provider -> new IdentifiableResourceReloadListener() {
            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return LootConfigManager.create(provider).reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return LootConfigManager.ID;
            }
        });
    }
}
