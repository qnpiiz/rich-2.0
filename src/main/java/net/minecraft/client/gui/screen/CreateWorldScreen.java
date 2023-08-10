package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateWorldScreen extends Screen
{
    private static final Logger field_238935_p_ = LogManager.getLogger();
    private static final ITextComponent field_243417_q = new TranslationTextComponent("selectWorld.gameMode");
    private static final ITextComponent field_243418_r = new TranslationTextComponent("selectWorld.enterSeed");
    private static final ITextComponent field_243419_s = new TranslationTextComponent("selectWorld.seedInfo");
    private static final ITextComponent field_243420_t = new TranslationTextComponent("selectWorld.enterName");
    private static final ITextComponent field_243421_u = new TranslationTextComponent("selectWorld.resultFolder");
    private static final ITextComponent field_243422_v = new TranslationTextComponent("selectWorld.allowCommands.info");
    private final Screen parentScreen;
    private TextFieldWidget worldNameField;
    private String saveDirName;
    private CreateWorldScreen.GameMode field_228197_f_ = CreateWorldScreen.GameMode.SURVIVAL;
    @Nullable
    private CreateWorldScreen.GameMode field_228198_g_;
    private Difficulty field_238936_v_ = Difficulty.NORMAL;
    private Difficulty field_238937_w_ = Difficulty.NORMAL;

    /** If cheats are allowed */
    private boolean allowCheats;

    /**
     * User explicitly clicked "Allow Cheats" at some point
     * Prevents value changes due to changing game mode
     */
    private boolean allowCheatsWasSetByUser;

    /** Set to true when "hardcore" is the currently-selected gamemode */
    public boolean hardCoreMode;
    protected DatapackCodec field_238933_b_;
    @Nullable
    private Path field_238928_A_;
    @Nullable
    private ResourcePackList field_243416_G;
    private boolean inMoreWorldOptionsDisplay;
    private Button btnCreateWorld;
    private Button btnGameMode;
    private Button field_238929_E_;
    private Button btnMoreOptions;
    private Button field_238930_G_;
    private Button field_238931_H_;
    private Button btnAllowCommands;
    private ITextComponent gameModeDesc1;
    private ITextComponent gameModeDesc2;
    private String worldName;
    private GameRules field_238932_M_ = new GameRules();
    public final WorldOptionsScreen field_238934_c_;

    public CreateWorldScreen(@Nullable Screen p_i242064_1_, WorldSettings p_i242064_2_, DimensionGeneratorSettings p_i242064_3_, @Nullable Path p_i242064_4_, DatapackCodec p_i242064_5_, DynamicRegistries.Impl p_i242064_6_)
    {
        this(p_i242064_1_, p_i242064_5_, new WorldOptionsScreen(p_i242064_6_, p_i242064_3_, BiomeGeneratorTypeScreens.func_239079_a_(p_i242064_3_), OptionalLong.of(p_i242064_3_.getSeed())));
        this.worldName = p_i242064_2_.getWorldName();
        this.allowCheats = p_i242064_2_.isCommandsAllowed();
        this.allowCheatsWasSetByUser = true;
        this.field_238936_v_ = p_i242064_2_.getDifficulty();
        this.field_238937_w_ = this.field_238936_v_;
        this.field_238932_M_.func_234899_a_(p_i242064_2_.getGameRules(), (MinecraftServer)null);

        if (p_i242064_2_.isHardcoreEnabled())
        {
            this.field_228197_f_ = CreateWorldScreen.GameMode.HARDCORE;
        }
        else if (p_i242064_2_.getGameType().isSurvivalOrAdventure())
        {
            this.field_228197_f_ = CreateWorldScreen.GameMode.SURVIVAL;
        }
        else if (p_i242064_2_.getGameType().isCreative())
        {
            this.field_228197_f_ = CreateWorldScreen.GameMode.CREATIVE;
        }

        this.field_238928_A_ = p_i242064_4_;
    }

    public static CreateWorldScreen func_243425_a(@Nullable Screen p_243425_0_)
    {
        DynamicRegistries.Impl dynamicregistries$impl = DynamicRegistries.func_239770_b_();
        return new CreateWorldScreen(p_243425_0_, DatapackCodec.VANILLA_CODEC, new WorldOptionsScreen(dynamicregistries$impl, DimensionGeneratorSettings.func_242751_a(dynamicregistries$impl.getRegistry(Registry.DIMENSION_TYPE_KEY), dynamicregistries$impl.getRegistry(Registry.BIOME_KEY), dynamicregistries$impl.getRegistry(Registry.NOISE_SETTINGS_KEY)), Optional.of(BiomeGeneratorTypeScreens.field_239066_a_), OptionalLong.empty()));
    }

    private CreateWorldScreen(@Nullable Screen p_i242063_1_, DatapackCodec p_i242063_2_, WorldOptionsScreen p_i242063_3_)
    {
        super(new TranslationTextComponent("selectWorld.create"));
        this.parentScreen = p_i242063_1_;
        this.worldName = I18n.format("selectWorld.newWorld");
        this.field_238933_b_ = p_i242063_2_;
        this.field_238934_c_ = p_i242063_3_;
    }

    public void tick()
    {
        this.worldNameField.tick();
        this.field_238934_c_.tick();
    }

    protected void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.worldNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, new TranslationTextComponent("selectWorld.enterName"))
        {
            protected IFormattableTextComponent getNarrationMessage()
            {
                return super.getNarrationMessage().appendString(". ").append(new TranslationTextComponent("selectWorld.resultFolder")).appendString(" ").appendString(CreateWorldScreen.this.saveDirName);
            }
        };
        this.worldNameField.setText(this.worldName);
        this.worldNameField.setResponder((p_214319_1_) ->
        {
            this.worldName = p_214319_1_;
            this.btnCreateWorld.active = !this.worldNameField.getText().isEmpty();
            this.calcSaveDirName();
        });
        this.children.add(this.worldNameField);
        int i = this.width / 2 - 155;
        int j = this.width / 2 + 5;
        this.btnGameMode = this.addButton(new Button(i, 100, 150, 20, StringTextComponent.EMPTY, (p_214316_1_) ->
        {
            switch (this.field_228197_f_)
            {
                case SURVIVAL:
                    this.func_228200_a_(CreateWorldScreen.GameMode.HARDCORE);
                    break;

                case HARDCORE:
                    this.func_228200_a_(CreateWorldScreen.GameMode.CREATIVE);
                    break;

                case CREATIVE:
                    this.func_228200_a_(CreateWorldScreen.GameMode.SURVIVAL);
            }

            p_214316_1_.queueNarration(250);
        })
        {
            public ITextComponent getMessage()
            {
                return new TranslationTextComponent("options.generic_value", CreateWorldScreen.field_243417_q, new TranslationTextComponent("selectWorld.gameMode." + CreateWorldScreen.this.field_228197_f_.field_228217_e_));
            }
            protected IFormattableTextComponent getNarrationMessage()
            {
                return super.getNarrationMessage().appendString(". ").append(CreateWorldScreen.this.gameModeDesc1).appendString(" ").append(CreateWorldScreen.this.gameModeDesc2);
            }
        });
        this.field_238929_E_ = this.addButton(new Button(j, 100, 150, 20, new TranslationTextComponent("options.difficulty"), (p_238956_1_) ->
        {
            this.field_238936_v_ = this.field_238936_v_.getNextDifficulty();
            this.field_238937_w_ = this.field_238936_v_;
            p_238956_1_.queueNarration(250);
        })
        {
            public ITextComponent getMessage()
            {
                return (new TranslationTextComponent("options.difficulty")).appendString(": ").append(CreateWorldScreen.this.field_238937_w_.getDisplayName());
            }
        });
        this.btnAllowCommands = this.addButton(new Button(i, 151, 150, 20, new TranslationTextComponent("selectWorld.allowCommands"), (p_214322_1_) ->
        {
            this.allowCheatsWasSetByUser = true;
            this.allowCheats = !this.allowCheats;
            p_214322_1_.queueNarration(250);
        })
        {
            public ITextComponent getMessage()
            {
                return DialogTexts.getComposedOptionMessage(super.getMessage(), CreateWorldScreen.this.allowCheats && !CreateWorldScreen.this.hardCoreMode);
            }
            protected IFormattableTextComponent getNarrationMessage()
            {
                return super.getNarrationMessage().appendString(". ").append(new TranslationTextComponent("selectWorld.allowCommands.info"));
            }
        });
        this.field_238931_H_ = this.addButton(new Button(j, 151, 150, 20, new TranslationTextComponent("selectWorld.dataPacks"), (p_214320_1_) ->
        {
            this.func_238958_v_();
        }));
        this.field_238930_G_ = this.addButton(new Button(i, 185, 150, 20, new TranslationTextComponent("selectWorld.gameRules"), (p_214312_1_) ->
        {
            this.mc.displayGuiScreen(new EditGamerulesScreen(this.field_238932_M_.clone(), (p_238946_1_) -> {
                this.mc.displayGuiScreen(this);
                p_238946_1_.ifPresent((p_238941_1_) -> {
                    this.field_238932_M_ = p_238941_1_;
                });
            }));
        }));
        this.field_238934_c_.func_239048_a_(this, this.mc, this.font);
        this.btnMoreOptions = this.addButton(new Button(j, 185, 150, 20, new TranslationTextComponent("selectWorld.moreWorldOptions"), (p_214321_1_) ->
        {
            this.toggleMoreWorldOptions();
        }));
        this.btnCreateWorld = this.addButton(new Button(i, this.height - 28, 150, 20, new TranslationTextComponent("selectWorld.create"), (p_214318_1_) ->
        {
            this.createWorld();
        }));
        this.btnCreateWorld.active = !this.worldName.isEmpty();
        this.addButton(new Button(j, this.height - 28, 150, 20, DialogTexts.GUI_CANCEL, (p_214317_1_) ->
        {
            this.func_243430_k();
        }));
        this.func_238955_g_();
        this.setFocusedDefault(this.worldNameField);
        this.func_228200_a_(this.field_228197_f_);
        this.calcSaveDirName();
    }

    private void func_228199_a_()
    {
        this.gameModeDesc1 = new TranslationTextComponent("selectWorld.gameMode." + this.field_228197_f_.field_228217_e_ + ".line1");
        this.gameModeDesc2 = new TranslationTextComponent("selectWorld.gameMode." + this.field_228197_f_.field_228217_e_ + ".line2");
    }

    /**
     * Determine a save-directory name from the world name
     */
    private void calcSaveDirName()
    {
        this.saveDirName = this.worldNameField.getText().trim();

        if (this.saveDirName.isEmpty())
        {
            this.saveDirName = "World";
        }

        try
        {
            this.saveDirName = FileUtil.findAvailableName(this.mc.getSaveLoader().getSavesDir(), this.saveDirName, "");
        }
        catch (Exception exception1)
        {
            this.saveDirName = "World";

            try
            {
                this.saveDirName = FileUtil.findAvailableName(this.mc.getSaveLoader().getSavesDir(), this.saveDirName, "");
            }
            catch (Exception exception)
            {
                throw new RuntimeException("Could not create save folder", exception);
            }
        }
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    private void createWorld()
    {
        this.mc.forcedScreenTick(new DirtMessageScreen(new TranslationTextComponent("createWorld.preparing")));

        if (this.func_238960_x_())
        {
            this.func_243432_s();
            DimensionGeneratorSettings dimensiongeneratorsettings = this.field_238934_c_.func_239054_a_(this.hardCoreMode);
            WorldSettings worldsettings;

            if (dimensiongeneratorsettings.func_236227_h_())
            {
                GameRules gamerules = new GameRules();
                gamerules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, (MinecraftServer)null);
                worldsettings = new WorldSettings(this.worldNameField.getText().trim(), GameType.SPECTATOR, false, Difficulty.PEACEFUL, true, gamerules, DatapackCodec.VANILLA_CODEC);
            }
            else
            {
                worldsettings = new WorldSettings(this.worldNameField.getText().trim(), this.field_228197_f_.field_228218_f_, this.hardCoreMode, this.field_238937_w_, this.allowCheats && !this.hardCoreMode, this.field_238932_M_, this.field_238933_b_);
            }

            this.mc.createWorld(this.saveDirName, worldsettings, this.field_238934_c_.func_239055_b_(), dimensiongeneratorsettings);
        }
    }

    /**
     * Toggles between initial world-creation display, and "more options" display.
     * Called when user clicks "More World Options..." or "Done" (same button, different labels depending on current
     * display).
     */
    private void toggleMoreWorldOptions()
    {
        this.showMoreWorldOptions(!this.inMoreWorldOptionsDisplay);
    }

    private void func_228200_a_(CreateWorldScreen.GameMode p_228200_1_)
    {
        if (!this.allowCheatsWasSetByUser)
        {
            this.allowCheats = p_228200_1_ == CreateWorldScreen.GameMode.CREATIVE;
        }

        if (p_228200_1_ == CreateWorldScreen.GameMode.HARDCORE)
        {
            this.hardCoreMode = true;
            this.btnAllowCommands.active = false;
            this.field_238934_c_.field_239027_a_.active = false;
            this.field_238937_w_ = Difficulty.HARD;
            this.field_238929_E_.active = false;
        }
        else
        {
            this.hardCoreMode = false;
            this.btnAllowCommands.active = true;
            this.field_238934_c_.field_239027_a_.active = true;
            this.field_238937_w_ = this.field_238936_v_;
            this.field_238929_E_.active = true;
        }

        this.field_228197_f_ = p_228200_1_;
        this.func_228199_a_();
    }

    public void func_238955_g_()
    {
        this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
    }

    /**
     * Shows additional world-creation options if toggle is true, otherwise shows main world-creation elements
     */
    private void showMoreWorldOptions(boolean toggle)
    {
        this.inMoreWorldOptionsDisplay = toggle;
        this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
        this.field_238929_E_.visible = !this.inMoreWorldOptionsDisplay;

        if (this.field_238934_c_.func_239042_a_())
        {
            this.field_238931_H_.visible = false;
            this.btnGameMode.active = false;

            if (this.field_228198_g_ == null)
            {
                this.field_228198_g_ = this.field_228197_f_;
            }

            this.func_228200_a_(CreateWorldScreen.GameMode.DEBUG);
            this.btnAllowCommands.visible = false;
        }
        else
        {
            this.btnGameMode.active = true;

            if (this.field_228198_g_ != null)
            {
                this.func_228200_a_(this.field_228198_g_);
            }

            this.btnAllowCommands.visible = !this.inMoreWorldOptionsDisplay;
            this.field_238931_H_.visible = !this.inMoreWorldOptionsDisplay;
        }

        this.field_238934_c_.func_239059_b_(this.inMoreWorldOptionsDisplay);
        this.worldNameField.setVisible(!this.inMoreWorldOptionsDisplay);

        if (this.inMoreWorldOptionsDisplay)
        {
            this.btnMoreOptions.setMessage(DialogTexts.GUI_DONE);
        }
        else
        {
            this.btnMoreOptions.setMessage(new TranslationTextComponent("selectWorld.moreWorldOptions"));
        }

        this.field_238930_G_.visible = !this.inMoreWorldOptionsDisplay;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (keyCode != 257 && keyCode != 335)
        {
            return false;
        }
        else
        {
            this.createWorld();
            return true;
        }
    }

    public void closeScreen()
    {
        if (this.inMoreWorldOptionsDisplay)
        {
            this.showMoreWorldOptions(false);
        }
        else
        {
            this.func_243430_k();
        }
    }

    public void func_243430_k()
    {
        this.mc.displayGuiScreen(this.parentScreen);
        this.func_243432_s();
    }

    private void func_243432_s()
    {
        if (this.field_243416_G != null)
        {
            this.field_243416_G.close();
        }

        this.func_238959_w_();
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, -1);

        if (this.inMoreWorldOptionsDisplay)
        {
            drawString(matrixStack, this.font, field_243418_r, this.width / 2 - 100, 47, -6250336);
            drawString(matrixStack, this.font, field_243419_s, this.width / 2 - 100, 85, -6250336);
            this.field_238934_c_.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
        {
            drawString(matrixStack, this.font, field_243420_t, this.width / 2 - 100, 47, -6250336);
            drawString(matrixStack, this.font, (new StringTextComponent("")).append(field_243421_u).appendString(" ").appendString(this.saveDirName), this.width / 2 - 100, 85, -6250336);
            this.worldNameField.render(matrixStack, mouseX, mouseY, partialTicks);
            drawString(matrixStack, this.font, this.gameModeDesc1, this.width / 2 - 150, 122, -6250336);
            drawString(matrixStack, this.font, this.gameModeDesc2, this.width / 2 - 150, 134, -6250336);

            if (this.btnAllowCommands.visible)
            {
                drawString(matrixStack, this.font, field_243422_v, this.width / 2 - 150, 172, -6250336);
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected <T extends IGuiEventListener> T addListener(T listener)
    {
        return super.addListener(listener);
    }

    protected <T extends Widget> T addButton(T button)
    {
        return super.addButton(button);
    }

    @Nullable
    protected Path func_238957_j_()
    {
        if (this.field_238928_A_ == null)
        {
            try
            {
                this.field_238928_A_ = Files.createTempDirectory("mcworld-");
            }
            catch (IOException ioexception)
            {
                field_238935_p_.warn("Failed to create temporary dir", (Throwable)ioexception);
                SystemToast.func_238539_c_(this.mc, this.saveDirName);
                this.func_243430_k();
            }
        }

        return this.field_238928_A_;
    }

    private void func_238958_v_()
    {
        Pair<File, ResourcePackList> pair = this.func_243423_B();

        if (pair != null)
        {
            this.mc.displayGuiScreen(new PackScreen(this, pair.getSecond(), this::func_241621_a_, pair.getFirst(), new TranslationTextComponent("dataPack.title")));
        }
    }

    private void func_241621_a_(ResourcePackList p_241621_1_)
    {
        List<String> list = ImmutableList.copyOf(p_241621_1_.func_232621_d_());
        List<String> list1 = p_241621_1_.func_232616_b_().stream().filter((p_241626_1_) ->
        {
            return !list.contains(p_241626_1_);
        }).collect(ImmutableList.toImmutableList());
        DatapackCodec datapackcodec = new DatapackCodec(list, list1);

        if (list.equals(this.field_238933_b_.getEnabled()))
        {
            this.field_238933_b_ = datapackcodec;
        }
        else
        {
            this.mc.enqueue(() ->
            {
                this.mc.displayGuiScreen(new DirtMessageScreen(new TranslationTextComponent("dataPack.validation.working")));
            });
            DataPackRegistries.func_240961_a_(p_241621_1_.func_232623_f_(), Commands.EnvironmentType.INTEGRATED, 2, Util.getServerExecutor(), this.mc).handle((p_241623_2_, p_241623_3_) ->
            {
                if (p_241623_3_ != null)
                {
                    field_238935_p_.warn("Failed to validate datapack", p_241623_3_);
                    this.mc.enqueue(() ->
                    {
                        this.mc.displayGuiScreen(new ConfirmScreen((p_241630_1_) -> {
                            if (p_241630_1_)
                            {
                                this.func_238958_v_();
                            }
                            else {
                                this.field_238933_b_ = DatapackCodec.VANILLA_CODEC;
                                this.mc.displayGuiScreen(this);
                            }
                        }, new TranslationTextComponent("dataPack.validation.failed"), StringTextComponent.EMPTY, new TranslationTextComponent("dataPack.validation.back"), new TranslationTextComponent("dataPack.validation.reset")));
                    });
                }
                else {
                    this.mc.enqueue(() -> {
                        this.field_238933_b_ = datapackcodec;
                        this.field_238934_c_.func_243447_a(p_241623_2_);
                        p_241623_2_.close();
                        this.mc.displayGuiScreen(this);
                    });
                }

                return null;
            });
        }
    }

    private void func_238959_w_()
    {
        if (this.field_238928_A_ != null)
        {
            try (Stream<Path> stream = Files.walk(this.field_238928_A_))
            {
                stream.sorted(Comparator.reverseOrder()).forEach((p_238948_0_) ->
                {
                    try {
                        Files.delete(p_238948_0_);
                    }
                    catch (IOException ioexception1)
                    {
                        field_238935_p_.warn("Failed to remove temporary file {}", p_238948_0_, ioexception1);
                    }
                });
            }
            catch (IOException ioexception)
            {
                field_238935_p_.warn("Failed to list temporary dir {}", (Object)this.field_238928_A_);
            }

            this.field_238928_A_ = null;
        }
    }

    private static void func_238945_a_(Path p_238945_0_, Path p_238945_1_, Path p_238945_2_)
    {
        try
        {
            Util.func_240984_a_(p_238945_0_, p_238945_1_, p_238945_2_);
        }
        catch (IOException ioexception)
        {
            field_238935_p_.warn("Failed to copy datapack file from {} to {}", p_238945_2_, p_238945_1_);
            throw new CreateWorldScreen.DatapackException(ioexception);
        }
    }

    private boolean func_238960_x_()
    {
        if (this.field_238928_A_ != null)
        {
            try (
                    SaveFormat.LevelSave saveformat$levelsave = this.mc.getSaveLoader().getLevelSave(this.saveDirName);
                    Stream<Path> stream = Files.walk(this.field_238928_A_);
                )
            {
                Path path = saveformat$levelsave.resolveFilePath(FolderName.DATAPACKS);
                Files.createDirectories(path);
                stream.filter((p_238942_1_) ->
                {
                    return !p_238942_1_.equals(this.field_238928_A_);
                }).forEach((p_238949_2_) ->
                {
                    func_238945_a_(this.field_238928_A_, path, p_238949_2_);
                });
            }
            catch (CreateWorldScreen.DatapackException | IOException ioexception)
            {
                field_238935_p_.warn("Failed to copy datapacks to world {}", this.saveDirName, ioexception);
                SystemToast.func_238539_c_(this.mc, this.saveDirName);
                this.func_243430_k();
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static Path func_238943_a_(Path p_238943_0_, Minecraft p_238943_1_)
    {
        MutableObject<Path> mutableobject = new MutableObject<>();

        try (Stream<Path> stream = Files.walk(p_238943_0_))
        {
            stream.filter((p_238944_1_) ->
            {
                return !p_238944_1_.equals(p_238943_0_);
            }).forEach((p_238947_2_) ->
            {
                Path path = mutableobject.getValue();

                if (path == null)
                {
                    try
                    {
                        path = Files.createTempDirectory("mcworld-");
                    }
                    catch (IOException ioexception1)
                    {
                        field_238935_p_.warn("Failed to create temporary dir");
                        throw new CreateWorldScreen.DatapackException(ioexception1);
                    }

                    mutableobject.setValue(path);
                }

                func_238945_a_(p_238943_0_, path, p_238947_2_);
            });
        }
        catch (CreateWorldScreen.DatapackException | IOException ioexception)
        {
            field_238935_p_.warn("Failed to copy datapacks from world {}", p_238943_0_, ioexception);
            SystemToast.func_238539_c_(p_238943_1_, p_238943_0_.toString());
            return null;
        }

        return mutableobject.getValue();
    }

    @Nullable
    private Pair<File, ResourcePackList> func_243423_B()
    {
        Path path = this.func_238957_j_();

        if (path != null)
        {
            File file1 = path.toFile();

            if (this.field_243416_G == null)
            {
                this.field_243416_G = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(file1, IPackNameDecorator.PLAIN));
                this.field_243416_G.reloadPacksFromFinders();
            }

            this.field_243416_G.setEnabledPacks(this.field_238933_b_.getEnabled());
            return Pair.of(file1, this.field_243416_G);
        }
        else
        {
            return null;
        }
    }

    static class DatapackException extends RuntimeException
    {
        public DatapackException(Throwable p_i232309_1_)
        {
            super(p_i232309_1_);
        }
    }

    static enum GameMode
    {
        SURVIVAL("survival", GameType.SURVIVAL),
        HARDCORE("hardcore", GameType.SURVIVAL),
        CREATIVE("creative", GameType.CREATIVE),
        DEBUG("spectator", GameType.SPECTATOR);

        private final String field_228217_e_;
        private final GameType field_228218_f_;

        private GameMode(String p_i225940_3_, GameType p_i225940_4_)
        {
            this.field_228217_e_ = p_i225940_3_;
            this.field_228218_f_ = p_i225940_4_;
        }
    }
}
