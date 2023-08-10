package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class UserList<K, V extends UserListEntry<K>>
{
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Gson field_232645_b_ = (new GsonBuilder()).setPrettyPrinting().create();
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();

    public UserList(File saveFile)
    {
        this.saveFile = saveFile;
    }

    public File getSaveFile()
    {
        return this.saveFile;
    }

    /**
     * Adds an entry to the list
     */
    public void addEntry(V entry)
    {
        this.values.put(this.getObjectKey(entry.getValue()), entry);

        try
        {
            this.writeChanges();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Could not save the list after adding a user.", (Throwable)ioexception);
        }
    }

    @Nullable
    public V getEntry(K obj)
    {
        this.removeExpired();
        return this.values.get(this.getObjectKey(obj));
    }

    public void removeEntry(K entry)
    {
        this.values.remove(this.getObjectKey(entry));

        try
        {
            this.writeChanges();
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Could not save the list after removing a user.", (Throwable)ioexception);
        }
    }

    public void removeEntry(UserListEntry<K> p_199042_1_)
    {
        this.removeEntry(p_199042_1_.getValue());
    }

    public String[] getKeys()
    {
        return this.values.keySet().toArray(new String[this.values.size()]);
    }

    public boolean isEmpty()
    {
        return this.values.size() < 1;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(K obj)
    {
        return obj.toString();
    }

    protected boolean hasEntry(K entry)
    {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    /**
     * Removes expired bans from the list. See {@link BanEntry#hasBanExpired}
     */
    private void removeExpired()
    {
        List<K> list = Lists.newArrayList();

        for (V v : this.values.values())
        {
            if (v.hasBanExpired())
            {
                list.add(v.getValue());
            }
        }

        for (K k : list)
        {
            this.values.remove(this.getObjectKey(k));
        }
    }

    protected abstract UserListEntry<K> createEntry(JsonObject entryData);

    public Collection<V> getEntries()
    {
        return this.values.values();
    }

    public void writeChanges() throws IOException
    {
        JsonArray jsonarray = new JsonArray();
        this.values.values().stream().map((p_232646_0_) ->
        {
            return Util.make(new JsonObject(), p_232646_0_::onSerialization);
        }).forEach(jsonarray::add);

        try (BufferedWriter bufferedwriter = Files.newWriter(this.saveFile, StandardCharsets.UTF_8))
        {
            field_232645_b_.toJson((JsonElement)jsonarray, bufferedwriter);
        }
    }

    public void readSavedFile() throws IOException
    {
        if (this.saveFile.exists())
        {
            try (BufferedReader bufferedreader = Files.newReader(this.saveFile, StandardCharsets.UTF_8))
            {
                JsonArray jsonarray = field_232645_b_.fromJson(bufferedreader, JsonArray.class);
                this.values.clear();

                for (JsonElement jsonelement : jsonarray)
                {
                    JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "entry");
                    UserListEntry<K> userlistentry = this.createEntry(jsonobject);

                    if (userlistentry.getValue() != null)
                    {
                        this.values.put(this.getObjectKey(userlistentry.getValue()), (V)userlistentry);
                    }
                }
            }
        }
    }
}
