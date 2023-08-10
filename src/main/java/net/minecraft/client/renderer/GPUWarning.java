package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GPUWarning extends ReloadListener<GPUWarning.GPUInfo>
{
    private static final Logger field_241686_a_ = LogManager.getLogger();
    private static final ResourceLocation field_241687_b_ = new ResourceLocation("gpu_warnlist.json");
    private ImmutableMap<String, String> field_241688_c_ = ImmutableMap.of();
    private boolean field_241689_d_;
    private boolean field_241690_e_;
    private boolean field_241691_f_;

    public boolean func_241692_a_()
    {
        return !this.field_241688_c_.isEmpty();
    }

    public boolean func_241695_b_()
    {
        return this.func_241692_a_() && !this.field_241690_e_;
    }

    public void func_241697_d_()
    {
        this.field_241689_d_ = true;
    }

    public void func_241698_e_()
    {
        this.field_241690_e_ = true;
    }

    public void func_241699_f_()
    {
        this.field_241690_e_ = true;
        this.field_241691_f_ = true;
    }

    public boolean func_241700_g_()
    {
        return this.field_241689_d_ && !this.field_241690_e_;
    }

    public boolean func_241701_h_()
    {
        return this.field_241691_f_;
    }

    public void func_241702_i_()
    {
        this.field_241689_d_ = false;
        this.field_241690_e_ = false;
        this.field_241691_f_ = false;
    }

    @Nullable
    public String func_241703_j_()
    {
        return this.field_241688_c_.get("renderer");
    }

    @Nullable
    public String func_241704_k_()
    {
        return this.field_241688_c_.get("version");
    }

    @Nullable
    public String func_241705_l_()
    {
        return this.field_241688_c_.get("vendor");
    }

    @Nullable
    public String func_243499_m()
    {
        StringBuilder stringbuilder = new StringBuilder();
        this.field_241688_c_.forEach((p_243498_1_, p_243498_2_) ->
        {
            stringbuilder.append(p_243498_1_).append(": ").append(p_243498_2_);
        });
        return stringbuilder.length() == 0 ? null : stringbuilder.toString();
    }

    /**
     * Performs any reloading that can be done off-thread, such as file IO
     */
    protected GPUWarning.GPUInfo prepare(IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        List<Pattern> list = Lists.newArrayList();
        List<Pattern> list1 = Lists.newArrayList();
        List<Pattern> list2 = Lists.newArrayList();
        profilerIn.startTick();
        JsonObject jsonobject = func_241696_c_(resourceManagerIn, profilerIn);

        if (jsonobject != null)
        {
            profilerIn.startSection("compile_regex");
            func_241693_a_(jsonobject.getAsJsonArray("renderer"), list);
            func_241693_a_(jsonobject.getAsJsonArray("version"), list1);
            func_241693_a_(jsonobject.getAsJsonArray("vendor"), list2);
            profilerIn.endSection();
        }

        profilerIn.endTick();
        return new GPUWarning.GPUInfo(list, list1, list2);
    }

    protected void apply(GPUWarning.GPUInfo objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn)
    {
        this.field_241688_c_ = objectIn.func_241709_a_();
    }

    private static void func_241693_a_(JsonArray p_241693_0_, List<Pattern> p_241693_1_)
    {
        p_241693_0_.forEach((p_241694_1_) ->
        {
            p_241693_1_.add(Pattern.compile(p_241694_1_.getAsString(), 2));
        });
    }

    @Nullable
    private static JsonObject func_241696_c_(IResourceManager p_241696_0_, IProfiler p_241696_1_)
    {
        p_241696_1_.startSection("parse_json");
        JsonObject jsonobject = null;

        try (
                IResource iresource = p_241696_0_.getResource(field_241687_b_);
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            )
        {
            jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
        }
        catch (JsonSyntaxException | IOException ioexception)
        {
            field_241686_a_.warn("Failed to load GPU warnlist");
        }

        p_241696_1_.endSection();
        return jsonobject;
    }

    public static final class GPUInfo
    {
        private final List<Pattern> field_241706_a_;
        private final List<Pattern> field_241707_b_;
        private final List<Pattern> field_241708_c_;

        private GPUInfo(List<Pattern> p_i241261_1_, List<Pattern> p_i241261_2_, List<Pattern> p_i241261_3_)
        {
            this.field_241706_a_ = p_i241261_1_;
            this.field_241707_b_ = p_i241261_2_;
            this.field_241708_c_ = p_i241261_3_;
        }

        private static String func_241711_a_(List<Pattern> p_241711_0_, String p_241711_1_)
        {
            List<String> list = Lists.newArrayList();

            for (Pattern pattern : p_241711_0_)
            {
                Matcher matcher = pattern.matcher(p_241711_1_);

                while (matcher.find())
                {
                    list.add(matcher.group());
                }
            }

            return String.join(", ", list);
        }

        private ImmutableMap<String, String> func_241709_a_()
        {
            Builder<String, String> builder = new Builder<>();
            String s = func_241711_a_(this.field_241706_a_, PlatformDescriptors.getGlRenderer());

            if (!s.isEmpty())
            {
                builder.put("renderer", s);
            }

            String s1 = func_241711_a_(this.field_241707_b_, PlatformDescriptors.getGlVersion());

            if (!s1.isEmpty())
            {
                builder.put("version", s1);
            }

            String s2 = func_241711_a_(this.field_241708_c_, PlatformDescriptors.getGlVendor());

            if (!s2.isEmpty())
            {
                builder.put("vendor", s2);
            }

            return builder.build();
        }
    }
}
