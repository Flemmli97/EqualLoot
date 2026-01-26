package io.github.flemmli97.equalloot.utils;

import java.util.UUID;

public interface UniqueItemDrop {

    void equalLoot$setUniqueItemDropTo(UUID target);

    boolean equalLoot$isUniqueItemDrop();

    UUID equalLoot$GetTarget();
}
