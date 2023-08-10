package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BackupList extends ValueObject
{
    private static final Logger field_230561_b_ = LogManager.getLogger();
    public List<Backup> field_230560_a_;

    public static BackupList func_230753_a_(String p_230753_0_)
    {
        JsonParser jsonparser = new JsonParser();
        BackupList backuplist = new BackupList();
        backuplist.field_230560_a_ = Lists.newArrayList();

        try
        {
            JsonElement jsonelement = jsonparser.parse(p_230753_0_).getAsJsonObject().get("backups");

            if (jsonelement.isJsonArray())
            {
                Iterator<JsonElement> iterator = jsonelement.getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    backuplist.field_230560_a_.add(Backup.func_230750_a_(iterator.next()));
                }
            }
        }
        catch (Exception exception)
        {
            field_230561_b_.error("Could not parse BackupList: " + exception.getMessage());
        }

        return backuplist;
    }
}
