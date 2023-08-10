package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class CommandStorage
{
    private final Map<String, CommandStorage.Container> loadedContainers = Maps.newHashMap();
    private final DimensionSavedDataManager manager;

    public CommandStorage(DimensionSavedDataManager manager)
    {
        this.manager = manager;
    }

    private CommandStorage.Container createContainer(String namespace, String name)
    {
        CommandStorage.Container commandstorage$container = new CommandStorage.Container(name);
        this.loadedContainers.put(namespace, commandstorage$container);
        return commandstorage$container;
    }

    public CompoundNBT getData(ResourceLocation id)
    {
        String s = id.getNamespace();
        String s1 = prefixStorageNamespace(s);
        CommandStorage.Container commandstorage$container = this.manager.get(() ->
        {
            return this.createContainer(s, s1);
        }, s1);
        return commandstorage$container != null ? commandstorage$container.getData(id.getPath()) : new CompoundNBT();
    }

    public void setData(ResourceLocation id, CompoundNBT nbt)
    {
        String s = id.getNamespace();
        String s1 = prefixStorageNamespace(s);
        this.manager.getOrCreate(() ->
        {
            return this.createContainer(s, s1);
        }, s1).setData(id.getPath(), nbt);
    }

    public Stream<ResourceLocation> getSavedDataKeys()
    {
        return this.loadedContainers.entrySet().stream().flatMap((entry) ->
        {
            return entry.getValue().getSavedKeys(entry.getKey());
        });
    }

    private static String prefixStorageNamespace(String namespace)
    {
        return "command_storage_" + namespace;
    }

    static class Container extends WorldSavedData
    {
        private final Map<String, CompoundNBT> contents = Maps.newHashMap();

        public Container(String name)
        {
            super(name);
        }

        public void read(CompoundNBT nbt)
        {
            CompoundNBT compoundnbt = nbt.getCompound("contents");

            for (String s : compoundnbt.keySet())
            {
                this.contents.put(s, compoundnbt.getCompound(s));
            }
        }

        public CompoundNBT write(CompoundNBT compound)
        {
            CompoundNBT compoundnbt = new CompoundNBT();
            this.contents.forEach((id, nbt) ->
            {
                compoundnbt.put(id, nbt.copy());
            });
            compound.put("contents", compoundnbt);
            return compound;
        }

        public CompoundNBT getData(String id)
        {
            CompoundNBT compoundnbt = this.contents.get(id);
            return compoundnbt != null ? compoundnbt : new CompoundNBT();
        }

        public void setData(String id, CompoundNBT nbt)
        {
            if (nbt.isEmpty())
            {
                this.contents.remove(id);
            }
            else
            {
                this.contents.put(id, nbt);
            }

            this.markDirty();
        }

        public Stream<ResourceLocation> getSavedKeys(String namespace)
        {
            return this.contents.keySet().stream().map((id) ->
            {
                return new ResourceLocation(namespace, id);
            });
        }
    }
}
