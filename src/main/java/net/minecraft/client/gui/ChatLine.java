package net.minecraft.client.gui;

public class ChatLine<T>
{
    private final int updateCounterCreated;
    private final T lineString;
    private final int chatLineID;

    public ChatLine(int updatedCounterCreated, T lineString, int chatLineID)
    {
        this.lineString = lineString;
        this.updateCounterCreated = updatedCounterCreated;
        this.chatLineID = chatLineID;
    }

    public T getLineString()
    {
        return this.lineString;
    }

    public int getUpdatedCounter()
    {
        return this.updateCounterCreated;
    }

    public int getChatLineID()
    {
        return this.chatLineID;
    }
}
