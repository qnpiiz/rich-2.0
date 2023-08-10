package net.minecraft.server;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.util.registry.DynamicRegistries;

public class ServerPropertiesProvider
{
    private final Path propertiesPath;
    private ServerProperties properties;

    public ServerPropertiesProvider(DynamicRegistries p_i242100_1_, Path p_i242100_2_)
    {
        this.propertiesPath = p_i242100_2_;
        this.properties = ServerProperties.func_244380_a(p_i242100_1_, p_i242100_2_);
    }

    public ServerProperties getProperties()
    {
        return this.properties;
    }

    public void save()
    {
        this.properties.save(this.propertiesPath);
    }

    public ServerPropertiesProvider func_219033_a(UnaryOperator<ServerProperties> p_219033_1_)
    {
        (this.properties = p_219033_1_.apply(this.properties)).save(this.propertiesPath);
        return this;
    }
}
