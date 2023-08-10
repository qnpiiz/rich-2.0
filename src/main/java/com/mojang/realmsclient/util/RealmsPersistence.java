package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.IPersistentSerializable;
import net.minecraft.realms.PersistenceSerializer;
import org.apache.commons.io.FileUtils;

public class RealmsPersistence
{
    private static final PersistenceSerializer field_238092_a_ = new PersistenceSerializer();

    public static RealmsPersistence.RealmsPersistenceData func_225188_a()
    {
        File file1 = func_238093_b_();

        try
        {
            return field_238092_a_.func_237695_a_(FileUtils.readFileToString(file1, StandardCharsets.UTF_8), RealmsPersistence.RealmsPersistenceData.class);
        }
        catch (IOException ioexception)
        {
            return new RealmsPersistence.RealmsPersistenceData();
        }
    }

    public static void func_225187_a(RealmsPersistence.RealmsPersistenceData p_225187_0_)
    {
        File file1 = func_238093_b_();

        try
        {
            FileUtils.writeStringToFile(file1, field_238092_a_.func_237694_a_(p_225187_0_), StandardCharsets.UTF_8);
        }
        catch (IOException ioexception)
        {
        }
    }

    private static File func_238093_b_()
    {
        return new File(Minecraft.getInstance().gameDir, "realms_persistence.json");
    }

    public static class RealmsPersistenceData implements IPersistentSerializable
    {
        @SerializedName("newsLink")
        public String field_225185_a;
        @SerializedName("hasUnreadNews")
        public boolean field_225186_b;
    }
}
