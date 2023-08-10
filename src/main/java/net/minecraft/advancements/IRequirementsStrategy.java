package net.minecraft.advancements;

import java.util.Collection;

public interface IRequirementsStrategy
{
    IRequirementsStrategy AND = (requirementStrings) ->
    {
        String[][] astring = new String[requirementStrings.size()][];
        int i = 0;

        for (String s : requirementStrings)
        {
            astring[i++] = new String[] {s};
        }

        return astring;
    };
    IRequirementsStrategy OR = (requirementStrings) ->
    {
        return new String[][]{requirementStrings.toArray(new String[0])};
    };

    String[][] createRequirements(Collection<String> p_createRequirements_1_);
}
