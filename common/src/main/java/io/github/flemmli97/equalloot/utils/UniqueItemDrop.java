package io.github.flemmli97.equalloot.utils;

import java.util.UUID;

public interface UniqueItemDrop {

    void equalLoot$setUniqueItemDropTo(EntityDropsData.PlayerDropContext target);

    boolean equalLoot$isUniqueItemDrop();

    UUID equalLoot$GetTarget();
}
