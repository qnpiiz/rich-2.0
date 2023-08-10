package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.JigsawBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.network.play.client.CJigsawBlockGeneratePacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class JigsawScreen extends Screen
{
    private static final ITextComponent field_243346_a = new TranslationTextComponent("jigsaw_block.joint_label");
    private static final ITextComponent field_243347_b = new TranslationTextComponent("jigsaw_block.pool");
    private static final ITextComponent field_243348_c = new TranslationTextComponent("jigsaw_block.name");
    private static final ITextComponent field_243349_p = new TranslationTextComponent("jigsaw_block.target");
    private static final ITextComponent field_243350_q = new TranslationTextComponent("jigsaw_block.final_state");
    private final JigsawTileEntity field_214259_a;
    private TextFieldWidget field_238818_b_;
    private TextFieldWidget field_238819_c_;
    private TextFieldWidget field_238820_p_;
    private TextFieldWidget finalStateField;
    private int field_238821_r_;
    private boolean field_238822_s_ = true;
    private Button field_238823_t_;
    private Button doneButton;
    private JigsawTileEntity.OrientationType field_238824_v_;

    public JigsawScreen(JigsawTileEntity p_i51083_1_)
    {
        super(NarratorChatListener.EMPTY);
        this.field_214259_a = p_i51083_1_;
    }

    public void tick()
    {
        this.field_238818_b_.tick();
        this.field_238819_c_.tick();
        this.field_238820_p_.tick();
        this.finalStateField.tick();
    }

    private void func_214256_b()
    {
        this.func_214258_d();
        this.mc.displayGuiScreen((Screen)null);
    }

    private void func_214257_c()
    {
        this.mc.displayGuiScreen((Screen)null);
    }

    private void func_214258_d()
    {
        this.mc.getConnection().sendPacket(new CUpdateJigsawBlockPacket(this.field_214259_a.getPos(), new ResourceLocation(this.field_238818_b_.getText()), new ResourceLocation(this.field_238819_c_.getText()), new ResourceLocation(this.field_238820_p_.getText()), this.finalStateField.getText(), this.field_238824_v_));
    }

    private void func_238835_m_()
    {
        this.mc.getConnection().sendPacket(new CJigsawBlockGeneratePacket(this.field_214259_a.getPos(), this.field_238821_r_, this.field_238822_s_));
    }

    public void closeScreen()
    {
        this.func_214257_c();
    }

    protected void init()
    {
        this.mc.keyboardListener.enableRepeatEvents(true);
        this.field_238820_p_ = new TextFieldWidget(this.font, this.width / 2 - 152, 20, 300, 20, new TranslationTextComponent("jigsaw_block.pool"));
        this.field_238820_p_.setMaxStringLength(128);
        this.field_238820_p_.setText(this.field_214259_a.func_235670_g_().toString());
        this.field_238820_p_.setResponder((p_238833_1_) ->
        {
            this.func_214253_a();
        });
        this.children.add(this.field_238820_p_);
        this.field_238818_b_ = new TextFieldWidget(this.font, this.width / 2 - 152, 55, 300, 20, new TranslationTextComponent("jigsaw_block.name"));
        this.field_238818_b_.setMaxStringLength(128);
        this.field_238818_b_.setText(this.field_214259_a.func_235668_d_().toString());
        this.field_238818_b_.setResponder((p_238830_1_) ->
        {
            this.func_214253_a();
        });
        this.children.add(this.field_238818_b_);
        this.field_238819_c_ = new TextFieldWidget(this.font, this.width / 2 - 152, 90, 300, 20, new TranslationTextComponent("jigsaw_block.target"));
        this.field_238819_c_.setMaxStringLength(128);
        this.field_238819_c_.setText(this.field_214259_a.func_235669_f_().toString());
        this.field_238819_c_.setResponder((p_214254_1_) ->
        {
            this.func_214253_a();
        });
        this.children.add(this.field_238819_c_);
        this.finalStateField = new TextFieldWidget(this.font, this.width / 2 - 152, 125, 300, 20, new TranslationTextComponent("jigsaw_block.final_state"));
        this.finalStateField.setMaxStringLength(256);
        this.finalStateField.setText(this.field_214259_a.getFinalState());
        this.children.add(this.finalStateField);
        this.field_238824_v_ = this.field_214259_a.func_235671_j_();
        int i = this.font.getStringPropertyWidth(field_243346_a) + 10;
        this.field_238823_t_ = this.addButton(new Button(this.width / 2 - 152 + i, 150, 300 - i, 20, this.func_238836_u_(), (p_238834_1_) ->
        {
            JigsawTileEntity.OrientationType[] ajigsawtileentity$orientationtype = JigsawTileEntity.OrientationType.values();
            int j = (this.field_238824_v_.ordinal() + 1) % ajigsawtileentity$orientationtype.length;
            this.field_238824_v_ = ajigsawtileentity$orientationtype[j];
            p_238834_1_.setMessage(this.func_238836_u_());
        }));
        boolean flag = JigsawBlock.getConnectingDirection(this.field_214259_a.getBlockState()).getAxis().isVertical();
        this.field_238823_t_.active = flag;
        this.field_238823_t_.visible = flag;
        this.addButton(new AbstractSlider(this.width / 2 - 154, 180, 100, 20, StringTextComponent.EMPTY, 0.0D)
        {
            {
                this.func_230979_b_();
            }
            protected void func_230979_b_()
            {
                this.setMessage(new TranslationTextComponent("jigsaw_block.levels", JigsawScreen.this.field_238821_r_));
            }
            protected void func_230972_a_()
            {
                JigsawScreen.this.field_238821_r_ = MathHelper.floor(MathHelper.clampedLerp(0.0D, 7.0D, this.sliderValue));
            }
        });
        this.addButton(new Button(this.width / 2 - 50, 180, 100, 20, new TranslationTextComponent("jigsaw_block.keep_jigsaws"), (p_238832_1_) ->
        {
            this.field_238822_s_ = !this.field_238822_s_;
            p_238832_1_.queueNarration(250);
        })
        {
            public ITextComponent getMessage()
            {
                return DialogTexts.getComposedOptionMessage(super.getMessage(), JigsawScreen.this.field_238822_s_);
            }
        });
        this.addButton(new Button(this.width / 2 + 54, 180, 100, 20, new TranslationTextComponent("jigsaw_block.generate"), (p_238831_1_) ->
        {
            this.func_214256_b();
            this.func_238835_m_();
        }));
        this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, DialogTexts.GUI_DONE, (p_238828_1_) ->
        {
            this.func_214256_b();
        }));
        this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, DialogTexts.GUI_CANCEL, (p_238825_1_) ->
        {
            this.func_214257_c();
        }));
        this.setFocusedDefault(this.field_238820_p_);
        this.func_214253_a();
    }

    private void func_214253_a()
    {
        this.doneButton.active = ResourceLocation.isResouceNameValid(this.field_238818_b_.getText()) && ResourceLocation.isResouceNameValid(this.field_238819_c_.getText()) && ResourceLocation.isResouceNameValid(this.field_238820_p_.getText());
    }

    public void resize(Minecraft minecraft, int width, int height)
    {
        String s = this.field_238818_b_.getText();
        String s1 = this.field_238819_c_.getText();
        String s2 = this.field_238820_p_.getText();
        String s3 = this.finalStateField.getText();
        int i = this.field_238821_r_;
        JigsawTileEntity.OrientationType jigsawtileentity$orientationtype = this.field_238824_v_;
        this.init(minecraft, width, height);
        this.field_238818_b_.setText(s);
        this.field_238819_c_.setText(s1);
        this.field_238820_p_.setText(s2);
        this.finalStateField.setText(s3);
        this.field_238821_r_ = i;
        this.field_238824_v_ = jigsawtileentity$orientationtype;
        this.field_238823_t_.setMessage(this.func_238836_u_());
    }

    private ITextComponent func_238836_u_()
    {
        return new TranslationTextComponent("jigsaw_block.joint." + this.field_238824_v_.getString());
    }

    public void onClose()
    {
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (!this.doneButton.active || keyCode != 257 && keyCode != 335)
        {
            return false;
        }
        else
        {
            this.func_214256_b();
            return true;
        }
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        drawString(matrixStack, this.font, field_243347_b, this.width / 2 - 153, 10, 10526880);
        this.field_238820_p_.render(matrixStack, mouseX, mouseY, partialTicks);
        drawString(matrixStack, this.font, field_243348_c, this.width / 2 - 153, 45, 10526880);
        this.field_238818_b_.render(matrixStack, mouseX, mouseY, partialTicks);
        drawString(matrixStack, this.font, field_243349_p, this.width / 2 - 153, 80, 10526880);
        this.field_238819_c_.render(matrixStack, mouseX, mouseY, partialTicks);
        drawString(matrixStack, this.font, field_243350_q, this.width / 2 - 153, 115, 10526880);
        this.finalStateField.render(matrixStack, mouseX, mouseY, partialTicks);

        if (JigsawBlock.getConnectingDirection(this.field_214259_a.getBlockState()).getAxis().isVertical())
        {
            drawString(matrixStack, this.font, field_243346_a, this.width / 2 - 153, 156, 16777215);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
