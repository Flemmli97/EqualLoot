package io.github.flemmli97.equalloot.utils;

import io.github.flemmli97.equalloot.EqualLoot;
import io.github.flemmli97.equalloot.data.LootShareConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface EntityDropsData {

    @Nullable
    PlayerDropContext equalLoot$playerDropContext();

    void equalLoot$setPlayerDropContext(PlayerDropContext context);

    /**
     * Currently only used for wither as for some reason mojang decided to drop the nether star there instead of using loot tables
     */
    void equalLoot$setAdditionalDropsFor(CustomDropsData customDropsData);

    record CustomDropsData(EqualLoot.DropResult result, Predicate<ItemEntity> predicate) {
    }

    record PlayerDropContext(Player player, LootShareConfig config) {
    }
}
