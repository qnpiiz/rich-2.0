package net.minecraft.client.gui;

import net.minecraft.inventory.container.Container;

public interface IHasContainer<T extends Container>
{
    T getContainer();
}
