package io.github.flemmli97.equalloot.utils;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

public interface EntityDropsData {

    @Nullable
    Player equalLoot$playerDropContext();

    void equalLoot$setPlayerDropContext(Player player);

    /**
     * Currently only used for wither as for some reason mojang decided to drop the nether star there instead of using loot tables
     */
    void equalLoot$setAdditionalDropsFor(CustomDropsData customDropsData);

    record CustomDropsData(Collection<Player> players, Predicate<ItemEntity> predicate) {
    }
}
