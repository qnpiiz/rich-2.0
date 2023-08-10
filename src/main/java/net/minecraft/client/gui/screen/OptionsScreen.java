package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen
{
    private static final AbstractOption[] SCREEN_OPTIONS = new AbstractOption[] {AbstractOption.FOV};
    private final Screen lastScreen;

    /** Reference to the GameSettings object. */
    private final GameSettings settings;
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty worldDifficulty;

    public OptionsScreen(Screen parentScreen, GameSettings gameSettingsObj)
    {
        super(new TranslationTextComponent("options.title"));
        this.lastScreen = parentScreen;
        this.settings = gameSettingsObj;
    }

    protected void init()
    {
        int i = 0;

        for (AbstractOption abstractoption : SCREEN_OPTIONS)
        {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 - 12 + 24 * (i >> 1);
            this.addButton(abstractoption.createWidget(this.mc.gameSettings, j, k, 150));
            ++i;
        }

        if (this.mc.world != null)
        {
            this.worldDifficulty = this.mc.world.getDifficulty();
            this.difficultyButton = this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.func_238630_a_(this.worldDifficulty), (p_213051_1_) ->
            {
                this.worldDifficulty = Difficulty.byId(this.worldDifficulty.getId() + 1);
                this.mc.getConnection().sendPacket(new CSetDifficultyPacket(this.worldDifficulty));
                this.difficultyButton.setMessage(this.func_238630_a_(this.worldDifficulty));
            }));

            if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcore())
            {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (p_213054_1_) ->
                {
                    this.mc.displayGuiScreen(new ConfirmScreen(this::accept, new TranslationTextComponent("difficulty.lock.title"), new TranslationTextComponent("difficulty.lock.question", new TranslationTextComponent("options.difficulty." + this.mc.world.getWorldInfo().getDifficulty().getTranslationKey()))));
                }));
                this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
            }
            else
            {
                this.difficultyButton.active = false;
            }
        }
        else
        {
            this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, AbstractOption.REALMS_NOTIFICATIONS, AbstractOption.REALMS_NOTIFICATIONS.func_238152_c_(this.settings), (p_213057_1_) ->
            {
                AbstractOption.REALMS_NOTIFICATIONS.nextValue(this.settings);
                this.settings.saveOptions();
                p_213057_1_.setMessage(AbstractOption.REALMS_NOTIFICATIONS.func_238152_c_(this.settings));
            }));
        }

        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, new TranslationTextComponent("options.skinCustomisation"), (p_213055_1_) ->
        {
            this.mc.displayGuiScreen(new CustomizeSkinScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, new TranslationTextComponent("options.sounds"), (p_213061_1_) ->
        {
            this.mc.displayGuiScreen(new OptionsSoundsScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, new TranslationTextComponent("options.video"), (p_213059_1_) ->
        {
            this.mc.displayGuiScreen(new VideoSettingsScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TranslationTextComponent("options.controls"), (p_213052_1_) ->
        {
            this.mc.displayGuiScreen(new ControlsScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, new TranslationTextComponent("options.language"), (p_213053_1_) ->
        {
            this.mc.displayGuiScreen(new LanguageScreen(this, this.settings, this.mc.getLanguageManager()));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, new TranslationTextComponent("options.chat.title"), (p_213049_1_) ->
        {
            this.mc.displayGuiScreen(new ChatOptionsScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, new TranslationTextComponent("options.resourcepack"), (p_213060_1_) ->
        {
            this.mc.displayGuiScreen(new PackScreen(this, this.mc.getResourcePackList(), this::func_241584_a_, this.mc.getFileResourcePacks(), new TranslationTextComponent("resourcePack.title")));
        }));
        this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, new TranslationTextComponent("options.accessibility.title"), (p_213058_1_) ->
        {
            this.mc.displayGuiScreen(new AccessibilityScreen(this, this.settings));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, DialogTexts.GUI_DONE, (p_213056_1_) ->
        {
            this.mc.displayGuiScreen(this.lastScreen);
        }));
    }

    private void func_241584_a_(ResourcePackList p_241584_1_)
    {
        List<String> list = ImmutableList.copyOf(this.settings.resourcePacks);
        this.settings.resourcePacks.clear();
        this.settings.incompatibleResourcePacks.clear();

        for (ResourcePackInfo resourcepackinfo : p_241584_1_.getEnabledPacks())
        {
            if (!resourcepackinfo.isOrderLocked())
            {
                this.settings.resourcePacks.add(resourcepackinfo.getName());

                if (!resourcepackinfo.getCompatibility().isCompatible())
                {
                    this.settings.incompatibleResourcePacks.add(resourcepackinfo.getName());
                }
            }
        }

        this.settings.saveOptions();
        List<String> list1 = ImmutableList.copyOf(this.settings.resourcePacks);

        if (!list1.equals(list))
        {
            this.mc.reloadResources();
        }
    }

    private ITextComponent func_238630_a_(Difficulty p_238630_1_)
    {
        return (new TranslationTextComponent("options.difficulty")).appendString(": ").append(p_238630_1_.getDisplayName());
    }

    private void accept(boolean value)
    {
        this.mc.displayGuiScreen(this);

        if (value && this.mc.world != null)
        {
            this.mc.getConnection().sendPacket(new CLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    public void onClose()
    {
        this.settings.saveOptions();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
