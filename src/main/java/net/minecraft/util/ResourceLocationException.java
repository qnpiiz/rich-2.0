package net.minecraft.util;

public class ResourceLocationException extends RuntimeException
{
    public ResourceLocationException(String message)
    {
        super(message);
    }

    public ResourceLocationException(String message, Throwable throwable)
    {
        super(message, throwable);
    }
}
