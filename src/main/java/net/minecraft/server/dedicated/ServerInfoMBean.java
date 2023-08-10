package net.minecraft.server.dedicated;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ServerInfoMBean implements DynamicMBean
{
    private static final Logger field_233482_a_ = LogManager.getLogger();
    private final MinecraftServer field_233483_b_;
    private final MBeanInfo field_233484_c_;
    private final Map<String, ServerInfoMBean.Attribute> field_233485_d_ = Stream.of(new ServerInfoMBean.Attribute("tickTimes", this::func_233491_b_, "Historical tick times (ms)", long[].class), new ServerInfoMBean.Attribute("averageTickTime", this::func_233486_a_, "Current average tick time (ms)", Long.TYPE)).collect(Collectors.toMap((p_233492_0_) ->
    {
        return p_233492_0_.field_233493_a_;
    }, Function.identity()));

    private ServerInfoMBean(MinecraftServer p_i231479_1_)
    {
        this.field_233483_b_ = p_i231479_1_;
        MBeanAttributeInfo[] ambeanattributeinfo = this.field_233485_d_.values().stream().map((p_233489_0_) ->
        {
            return p_233489_0_.func_233497_a_();
        }).toArray((p_233487_0_) ->
        {
            return new MBeanAttributeInfo[p_233487_0_];
        });
        this.field_233484_c_ = new MBeanInfo(ServerInfoMBean.class.getSimpleName(), "metrics for dedicated server", ambeanattributeinfo, (MBeanConstructorInfo[])null, (MBeanOperationInfo[])null, new MBeanNotificationInfo[0]);
    }

    public static void func_233490_a_(MinecraftServer p_233490_0_)
    {
        try
        {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new ServerInfoMBean(p_233490_0_), new ObjectName("net.minecraft.server:type=Server"));
        }
        catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException | MalformedObjectNameException malformedobjectnameexception)
        {
            field_233482_a_.warn("Failed to initialise server as JMX bean", (Throwable)malformedobjectnameexception);
        }
    }

    private float func_233486_a_()
    {
        return this.field_233483_b_.getTickTime();
    }

    private long[] func_233491_b_()
    {
        return this.field_233483_b_.tickTimeArray;
    }

    @Nullable
    public Object getAttribute(String p_getAttribute_1_)
    {
        ServerInfoMBean.Attribute serverinfombean$attribute = this.field_233485_d_.get(p_getAttribute_1_);
        return serverinfombean$attribute == null ? null : serverinfombean$attribute.field_233494_b_.get();
    }

    public void setAttribute(javax.management.Attribute p_setAttribute_1_)
    {
    }

    public AttributeList getAttributes(String[] p_getAttributes_1_)
    {
        List<javax.management.Attribute> list = Arrays.stream(p_getAttributes_1_).map(this.field_233485_d_::get).filter(Objects::nonNull).map((p_233488_0_) ->
        {
            return new javax.management.Attribute(p_233488_0_.field_233493_a_, p_233488_0_.field_233494_b_.get());
        }).collect(Collectors.toList());
        return new AttributeList(list);
    }

    public AttributeList setAttributes(AttributeList p_setAttributes_1_)
    {
        return new AttributeList();
    }

    @Nullable
    public Object invoke(String p_invoke_1_, Object[] p_invoke_2_, String[] p_invoke_3_)
    {
        return null;
    }

    public MBeanInfo getMBeanInfo()
    {
        return this.field_233484_c_;
    }

    static final class Attribute
    {
        private final String field_233493_a_;
        private final Supplier<Object> field_233494_b_;
        private final String field_233495_c_;
        private final Class<?> field_233496_d_;

        private Attribute(String p_i231480_1_, Supplier<Object> p_i231480_2_, String p_i231480_3_, Class<?> p_i231480_4_)
        {
            this.field_233493_a_ = p_i231480_1_;
            this.field_233494_b_ = p_i231480_2_;
            this.field_233495_c_ = p_i231480_3_;
            this.field_233496_d_ = p_i231480_4_;
        }

        private MBeanAttributeInfo func_233497_a_()
        {
            return new MBeanAttributeInfo(this.field_233493_a_, this.field_233496_d_.getSimpleName(), this.field_233495_c_, true, false, false);
        }
    }
}
