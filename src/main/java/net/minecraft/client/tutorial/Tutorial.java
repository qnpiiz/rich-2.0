package net.minecraft.client.tutorial;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.TutorialToast;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

public class Tutorial
{
    private final Minecraft minecraft;
    @Nullable
    private ITutorialStep tutorialStep;
    private List<Tutorial.ToastTimeInfo> field_244696_c = Lists.newArrayList();

    public Tutorial(Minecraft minecraft)
    {
        this.minecraft = minecraft;
    }

    public void handleMovement(MovementInput p_193293_1_)
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.handleMovement(p_193293_1_);
        }
    }

    public void onMouseMove(double velocityX, double velocityY)
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.onMouseMove(velocityX, velocityY);
        }
    }

    public void onMouseHover(@Nullable ClientWorld worldIn, @Nullable RayTraceResult result)
    {
        if (this.tutorialStep != null && result != null && worldIn != null)
        {
            this.tutorialStep.onMouseHover(worldIn, result);
        }
    }

    public void onHitBlock(ClientWorld worldIn, BlockPos pos, BlockState state, float diggingStage)
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.onHitBlock(worldIn, pos, state, diggingStage);
        }
    }

    /**
     * Called when the player opens his inventory
     */
    public void openInventory()
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.openInventory();
        }
    }

    /**
     * Called when the player pick up an ItemStack
     */
    public void handleSetSlot(ItemStack stack)
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.handleSetSlot(stack);
        }
    }

    public void stop()
    {
        if (this.tutorialStep != null)
        {
            this.tutorialStep.onStop();
            this.tutorialStep = null;
        }
    }

    /**
     * Reloads the tutorial step from the game settings
     */
    public void reload()
    {
        if (this.tutorialStep != null)
        {
            this.stop();
        }

        this.tutorialStep = this.minecraft.gameSettings.tutorialStep.create(this);
    }

    public void func_244698_a(TutorialToast p_244698_1_, int p_244698_2_)
    {
        this.field_244696_c.add(new Tutorial.ToastTimeInfo(p_244698_1_, p_244698_2_));
        this.minecraft.getToastGui().add(p_244698_1_);
    }

    public void func_244697_a(TutorialToast p_244697_1_)
    {
        this.field_244696_c.removeIf((p_244699_1_) ->
        {
            return p_244699_1_.field_244701_a == p_244697_1_;
        });
        p_244697_1_.hide();
    }

    public void tick()
    {
        this.field_244696_c.removeIf((p_244700_0_) ->
        {
            return p_244700_0_.func_244704_a();
        });

        if (this.tutorialStep != null)
        {
            if (this.minecraft.world != null)
            {
                this.tutorialStep.tick();
            }
            else
            {
                this.stop();
            }
        }
        else if (this.minecraft.world != null)
        {
            this.reload();
        }
    }

    /**
     * Sets a new step to the tutorial
     */
    public void setStep(TutorialSteps step)
    {
        this.minecraft.gameSettings.tutorialStep = step;
        this.minecraft.gameSettings.saveOptions();

        if (this.tutorialStep != null)
        {
            this.tutorialStep.onStop();
            this.tutorialStep = step.create(this);
        }
    }

    public Minecraft getMinecraft()
    {
        return this.minecraft;
    }

    public GameType getGameType()
    {
        return this.minecraft.playerController == null ? GameType.NOT_SET : this.minecraft.playerController.getCurrentGameType();
    }

    public static ITextComponent createKeybindComponent(String keybind)
    {
        return (new KeybindTextComponent("key." + keybind)).mergeStyle(TextFormatting.BOLD);
    }

    static final class ToastTimeInfo
    {
        private final TutorialToast field_244701_a;
        private final int field_244702_b;
        private int field_244703_c;

        private ToastTimeInfo(TutorialToast p_i242134_1_, int p_i242134_2_)
        {
            this.field_244701_a = p_i242134_1_;
            this.field_244702_b = p_i242134_2_;
        }

        private boolean func_244704_a()
        {
            this.field_244701_a.setProgress(Math.min((float)(++this.field_244703_c) / (float)this.field_244702_b, 1.0F));

            if (this.field_244703_c > this.field_244702_b)
            {
                this.field_244701_a.hide();
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
