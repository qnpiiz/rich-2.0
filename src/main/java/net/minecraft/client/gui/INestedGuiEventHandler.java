package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface INestedGuiEventHandler extends IGuiEventListener
{
    List <? extends IGuiEventListener > getEventListeners();

default Optional<IGuiEventListener> getEventListenerForPos(double mouseX, double mouseY)
    {
        for (IGuiEventListener iguieventlistener : this.getEventListeners())
        {
            if (iguieventlistener.isMouseOver(mouseX, mouseY))
            {
                return Optional.of(iguieventlistener);
            }
        }

        return Optional.empty();
    }

default boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (IGuiEventListener iguieventlistener : this.getEventListeners())
        {
            if (iguieventlistener.mouseClicked(mouseX, mouseY, button))
            {
                this.setListener(iguieventlistener);

                if (button == 0)
                {
                    this.setDragging(true);
                }

                return true;
            }
        }

        return false;
    }

default boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.setDragging(false);
        return this.getEventListenerForPos(mouseX, mouseY).filter((listener) ->
        {
            return listener.mouseReleased(mouseX, mouseY, button);
        }).isPresent();
    }

default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        return this.getListener() != null && this.isDragging() && button == 0 ? this.getListener().mouseDragged(mouseX, mouseY, button, dragX, dragY) : false;
    }

    boolean isDragging();

    void setDragging(boolean dragging);

default boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        return this.getEventListenerForPos(mouseX, mouseY).filter((listener) ->
        {
            return listener.mouseScrolled(mouseX, mouseY, delta);
        }).isPresent();
    }

default boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.getListener() != null && this.getListener().keyPressed(keyCode, scanCode, modifiers);
    }

default boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        return this.getListener() != null && this.getListener().keyReleased(keyCode, scanCode, modifiers);
    }

default boolean charTyped(char codePoint, int modifiers)
    {
        return this.getListener() != null && this.getListener().charTyped(codePoint, modifiers);
    }

    @Nullable
    IGuiEventListener getListener();

    void setListener(@Nullable IGuiEventListener listener);

default void setFocusedDefault(@Nullable IGuiEventListener eventListener)
    {
        this.setListener(eventListener);
        eventListener.changeFocus(true);
    }

default void setListenerDefault(@Nullable IGuiEventListener eventListener)
    {
        this.setListener(eventListener);
    }

default boolean changeFocus(boolean focus)
    {
        IGuiEventListener iguieventlistener = this.getListener();
        boolean flag = iguieventlistener != null;

        if (flag && iguieventlistener.changeFocus(focus))
        {
            return true;
        }
        else
        {
            List <? extends IGuiEventListener > list = this.getEventListeners();
            int j = list.indexOf(iguieventlistener);
            int i;

            if (flag && j >= 0)
            {
                i = j + (focus ? 1 : 0);
            }
            else if (focus)
            {
                i = 0;
            }
            else
            {
                i = list.size();
            }

            ListIterator <? extends IGuiEventListener > listiterator = list.listIterator(i);
            BooleanSupplier booleansupplier = focus ? listiterator::hasNext : listiterator::hasPrevious;
            Supplier <? extends IGuiEventListener > supplier = focus ? listiterator::next : listiterator::previous;

            while (booleansupplier.getAsBoolean())
            {
                IGuiEventListener iguieventlistener1 = supplier.get();

                if (iguieventlistener1.changeFocus(focus))
                {
                    this.setListener(iguieventlistener1);
                    return true;
                }
            }

            this.setListener((IGuiEventListener)null);
            return false;
        }
    }
}
