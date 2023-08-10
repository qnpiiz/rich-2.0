package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvitesList extends ValueObject
{
    private static final Logger field_230570_b_ = LogManager.getLogger();
    public List<PendingInvite> field_230569_a_ = Lists.newArrayList();

    public static PendingInvitesList func_230756_a_(String p_230756_0_)
    {
        PendingInvitesList pendinginviteslist = new PendingInvitesList();

        try
        {
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = jsonparser.parse(p_230756_0_).getAsJsonObject();

            if (jsonobject.get("invites").isJsonArray())
            {
                Iterator<JsonElement> iterator = jsonobject.get("invites").getAsJsonArray().iterator();

                while (iterator.hasNext())
                {
                    pendinginviteslist.field_230569_a_.add(PendingInvite.func_230755_a_(iterator.next().getAsJsonObject()));
                }
            }
        }
        catch (Exception exception)
        {
            field_230570_b_.error("Could not parse PendingInvitesList: " + exception.getMessage());
        }

        return pendinginviteslist;
    }
}
