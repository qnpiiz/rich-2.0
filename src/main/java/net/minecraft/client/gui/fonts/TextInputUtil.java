package net.minecraft.client.gui.fonts;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.TextFormatting;

public class TextInputUtil
{
    private final Supplier<String> textSupplier;
    private final Consumer<String> textConsumer;
    private final Supplier<String> clipboardSupplier;
    private final Consumer<String> clipboardConsumer;
    private final Predicate<String> textLimiter;
    private int endIndex;
    private int startIndex;

    public TextInputUtil(Supplier<String> textSupplier, Consumer<String> textConsumer, Supplier<String> clipboardSupplier, Consumer<String> clipboardConsumer, Predicate<String> textLimiter)
    {
        this.textSupplier = textSupplier;
        this.textConsumer = textConsumer;
        this.clipboardSupplier = clipboardSupplier;
        this.clipboardConsumer = clipboardConsumer;
        this.textLimiter = textLimiter;
        this.moveCursorToEnd();
    }

    public static Supplier<String> getClipboardTextSupplier(Minecraft minecraft)
    {
        return () ->
        {
            return getClipboardText(minecraft);
        };
    }

    public static String getClipboardText(Minecraft minecraft)
    {
        return TextFormatting.getTextWithoutFormattingCodes(minecraft.keyboardListener.getClipboardString().replaceAll("\\r", ""));
    }

    public static Consumer<String> getClipboardTextSetter(Minecraft minecraft)
    {
        return (text) ->
        {
            setClipboardText(minecraft, text);
        };
    }

    public static void setClipboardText(Minecraft minecraft, String text)
    {
        minecraft.keyboardListener.setClipboardString(text);
    }

    public boolean putChar(char character)
    {
        if (SharedConstants.isAllowedCharacter(character))
        {
            this.insertTextAtSelection(this.textSupplier.get(), Character.toString(character));
        }

        return true;
    }

    public boolean specialKeyPressed(int key)
    {
        if (Screen.isSelectAll(key))
        {
            this.selectAll();
            return true;
        }
        else if (Screen.isCopy(key))
        {
            this.copySelectedText();
            return true;
        }
        else if (Screen.isPaste(key))
        {
            this.insertClipboardText();
            return true;
        }
        else if (Screen.isCut(key))
        {
            this.cutText();
            return true;
        }
        else if (key == 259)
        {
            this.deleteCharAtSelection(-1);
            return true;
        }
        else
        {
            if (key == 261)
            {
                this.deleteCharAtSelection(1);
            }
            else
            {
                if (key == 263)
                {
                    if (Screen.hasControlDown())
                    {
                        this.moveCursorByWords(-1, Screen.hasShiftDown());
                    }
                    else
                    {
                        this.moveCursorByChar(-1, Screen.hasShiftDown());
                    }

                    return true;
                }

                if (key == 262)
                {
                    if (Screen.hasControlDown())
                    {
                        this.moveCursorByWords(1, Screen.hasShiftDown());
                    }
                    else
                    {
                        this.moveCursorByChar(1, Screen.hasShiftDown());
                    }

                    return true;
                }

                if (key == 268)
                {
                    this.moveCursorToStart(Screen.hasShiftDown());
                    return true;
                }

                if (key == 269)
                {
                    this.moveCursorToEnd(Screen.hasShiftDown());
                    return true;
                }
            }

            return false;
        }
    }

    private int clampIndexToTextLength(int textIndex)
    {
        return MathHelper.clamp(textIndex, 0, this.textSupplier.get().length());
    }

    private void insertTextAtSelection(String text, String clipboardText)
    {
        if (this.startIndex != this.endIndex)
        {
            text = this.deleteSelectionFromText(text);
        }

        this.endIndex = MathHelper.clamp(this.endIndex, 0, text.length());
        String s = (new StringBuilder(text)).insert(this.endIndex, clipboardText).toString();

        if (this.textLimiter.test(s))
        {
            this.textConsumer.accept(s);
            this.startIndex = this.endIndex = Math.min(s.length(), this.endIndex + clipboardText.length());
        }
    }

    public void putText(String text)
    {
        this.insertTextAtSelection(this.textSupplier.get(), text);
    }

    private void deselectSelection(boolean keepSelection)
    {
        if (!keepSelection)
        {
            this.startIndex = this.endIndex;
        }
    }

    public void moveCursorByChar(int direction, boolean keepSelection)
    {
        this.endIndex = Util.func_240980_a_(this.textSupplier.get(), this.endIndex, direction);
        this.deselectSelection(keepSelection);
    }

    public void moveCursorByWords(int direction, boolean keepSelection)
    {
        this.endIndex = CharacterManager.func_238351_a_(this.textSupplier.get(), direction, this.endIndex, true);
        this.deselectSelection(keepSelection);
    }

    public void deleteCharAtSelection(int bidiDirection)
    {
        String s = this.textSupplier.get();

        if (!s.isEmpty())
        {
            String s1;

            if (this.startIndex != this.endIndex)
            {
                s1 = this.deleteSelectionFromText(s);
            }
            else
            {
                int i = Util.func_240980_a_(s, this.endIndex, bidiDirection);
                int j = Math.min(i, this.endIndex);
                int k = Math.max(i, this.endIndex);
                s1 = (new StringBuilder(s)).delete(j, k).toString();

                if (bidiDirection < 0)
                {
                    this.startIndex = this.endIndex = j;
                }
            }

            this.textConsumer.accept(s1);
        }
    }

    public void cutText()
    {
        String s = this.textSupplier.get();
        this.clipboardConsumer.accept(this.getSelectedText(s));
        this.textConsumer.accept(this.deleteSelectionFromText(s));
    }

    public void insertClipboardText()
    {
        this.insertTextAtSelection(this.textSupplier.get(), this.clipboardSupplier.get());
        this.startIndex = this.endIndex;
    }

    public void copySelectedText()
    {
        this.clipboardConsumer.accept(this.getSelectedText(this.textSupplier.get()));
    }

    public void selectAll()
    {
        this.startIndex = 0;
        this.endIndex = this.textSupplier.get().length();
    }

    private String getSelectedText(String text)
    {
        int i = Math.min(this.endIndex, this.startIndex);
        int j = Math.max(this.endIndex, this.startIndex);
        return text.substring(i, j);
    }

    private String deleteSelectionFromText(String text)
    {
        if (this.startIndex == this.endIndex)
        {
            return text;
        }
        else
        {
            int i = Math.min(this.endIndex, this.startIndex);
            int j = Math.max(this.endIndex, this.startIndex);
            String s = text.substring(0, i) + text.substring(j);
            this.startIndex = this.endIndex = i;
            return s;
        }
    }

    private void moveCursorToStart(boolean keepSelection)
    {
        this.endIndex = 0;
        this.deselectSelection(keepSelection);
    }

    public void moveCursorToEnd()
    {
        this.moveCursorToEnd(false);
    }

    private void moveCursorToEnd(boolean keepSelection)
    {
        this.endIndex = this.textSupplier.get().length();
        this.deselectSelection(keepSelection);
    }

    public int getEndIndex()
    {
        return this.endIndex;
    }

    public void moveCursorTo(int textIndex, boolean keepSelection)
    {
        this.endIndex = this.clampIndexToTextLength(textIndex);
        this.deselectSelection(keepSelection);
    }

    public int getStartIndex()
    {
        return this.startIndex;
    }

    public void setSelection(int selectionStart, int selectionEnd)
    {
        int i = this.textSupplier.get().length();
        this.endIndex = MathHelper.clamp(selectionStart, 0, i);
        this.startIndex = MathHelper.clamp(selectionEnd, 0, i);
    }

    public boolean hasSelection()
    {
        return this.endIndex != this.startIndex;
    }
}
