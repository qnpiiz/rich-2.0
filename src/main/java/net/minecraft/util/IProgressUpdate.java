package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;

public interface IProgressUpdate
{
    void displaySavingString(ITextComponent component);

    void resetProgressAndMessage(ITextComponent component);

    void displayLoadingString(ITextComponent component);

    /**
     * Updates the progress bar on the loading screen to the specified amount.
     */
    void setLoadingProgress(int progress);

    void setDoneWorking();
}
