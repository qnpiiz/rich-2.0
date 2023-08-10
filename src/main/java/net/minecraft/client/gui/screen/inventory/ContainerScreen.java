package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public abstract class ContainerScreen<T extends Container> extends Screen implements IHasContainer<T>
{
    /** The location of the inventory background texture */
    public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");

    /** The X size of the inventory window in pixels. */
    protected int xSize = 176;

    /** The Y size of the inventory window in pixels. */
    protected int ySize = 166;
    protected int titleX;
    protected int titleY;
    protected int playerInventoryTitleX;
    protected int playerInventoryTitleY;

    /** A list of the players inventory slots */
    protected final T container;
    protected final PlayerInventory playerInventory;
    @Nullable

    /** Holds the slot currently hovered */
    protected Slot hoveredSlot;
    @Nullable

    /** Used when touchscreen is enabled */
    private Slot clickedSlot;
    @Nullable
    private Slot returningStackDestSlot;
    @Nullable
    private Slot currentDragTargetSlot;
    @Nullable
    private Slot lastClickSlot;

    /**
     * Starting X position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiLeft;

    /**
     * Starting Y position for the Gui. Inconsistent use for Gui backgrounds.
     */
    protected int guiTop;

    /** Used when touchscreen is enabled. */
    private boolean isRightMouseClick;

    /** Used when touchscreen is enabled */
    private ItemStack draggedStack = ItemStack.EMPTY;
    private int touchUpX;
    private int touchUpY;
    private long returningStackTime;

    /** Used when touchscreen is enabled */
    private ItemStack returningStack = ItemStack.EMPTY;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot = ItemStack.EMPTY;

    public ContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(titleIn);
        this.container = screenContainer;
        this.playerInventory = inv;
        this.ignoreMouseUp = true;
        this.titleX = 8;
        this.titleY = 6;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    protected void init()
    {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)i, (float)j, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        int k = 240;
        int l = 240;
        RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i1 = 0; i1 < this.container.inventorySlots.size(); ++i1)
        {
            Slot slot = this.container.inventorySlots.get(i1);

            if (slot.isEnabled())
            {
                this.moveItems(matrixStack, slot);
            }

            if (this.isSlotSelected(slot, (double)mouseX, (double)mouseY) && slot.isEnabled())
            {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                int j1 = slot.xPos;
                int k1 = slot.yPos;
                RenderSystem.colorMask(true, true, true, false);
                this.fillGradient(matrixStack, j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }

        this.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        PlayerInventory playerinventory = this.mc.player.inventory;
        ItemStack itemstack = this.draggedStack.isEmpty() ? playerinventory.getItemStack() : this.draggedStack;

        if (!itemstack.isEmpty())
        {
            int j2 = 8;
            int k2 = this.draggedStack.isEmpty() ? 8 : 16;
            String s = null;

            if (!this.draggedStack.isEmpty() && this.isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.setCount(this.dragSplittingRemnant);

                if (itemstack.isEmpty())
                {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }

            this.drawItemStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
        }

        if (!this.returningStack.isEmpty())
        {
            float f = (float)(Util.milliTime() - this.returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                this.returningStack = ItemStack.EMPTY;
            }

            int l2 = this.returningStackDestSlot.xPos - this.touchUpX;
            int i3 = this.returningStackDestSlot.yPos - this.touchUpY;
            int l1 = this.touchUpX + (int)((float)l2 * f);
            int i2 = this.touchUpY + (int)((float)i3 * f);
            this.drawItemStack(this.returningStack, l1, i2, (String)null);
        }

        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }

    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y)
    {
        if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack())
        {
            this.renderTooltip(matrixStack, this.hoveredSlot.getStack(), x, y);
        }
    }

    /**
     * Draws an ItemStack.
     *  
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        RenderSystem.translatef(0.0F, 0.0F, 32.0F);
        this.setBlitOffset(200);
        this.itemRenderer.zLevel = 200.0F;
        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
        this.setBlitOffset(0);
        this.itemRenderer.zLevel = 0.0F;
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y)
    {
        this.font.func_243248_b(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
    }

    protected abstract void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y);

    private void moveItems(MatrixStack matrixStack, Slot slot)
    {
        int i = slot.xPos;
        int j = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
        String s = null;

        if (slot == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty())
        {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() / 2);
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slot, itemstack1, true) && this.container.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));

                if (itemstack.getCount() > k)
                {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slot);
                this.updateDragSplitting();
            }
        }

        this.setBlitOffset(100);
        this.itemRenderer.zLevel = 100.0F;

        if (itemstack.isEmpty() && slot.isEnabled())
        {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getBackground();

            if (pair != null)
            {
                TextureAtlasSprite textureatlassprite = this.mc.getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
                this.mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
                blit(matrixStack, i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                fill(matrixStack, i, j, i + 16, j + 16, -2130706433);
            }

            RenderSystem.enableDepthTest();
            this.itemRenderer.renderItemAndEffectIntoGUI(this.mc.player, itemstack, i, j);
            this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
        }

        this.itemRenderer.zLevel = 0.0F;
        this.setBlitOffset(0);
    }

    private void updateDragSplitting()
    {
        ItemStack itemstack = this.mc.player.inventory.getItemStack();

        if (!itemstack.isEmpty() && this.dragSplitting)
        {
            if (this.dragSplittingLimit == 2)
            {
                this.dragSplittingRemnant = itemstack.getMaxStackSize();
            }
            else
            {
                this.dragSplittingRemnant = itemstack.getCount();

                for (Slot slot : this.dragSplittingSlots)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

                    if (itemstack1.getCount() > j)
                    {
                        itemstack1.setCount(j);
                    }

                    this.dragSplittingRemnant -= itemstack1.getCount() - i;
                }
            }
        }
    }

    @Nullable
    private Slot getSelectedSlot(double mouseX, double mouseY)
    {
        for (int i = 0; i < this.container.inventorySlots.size(); ++i)
        {
            Slot slot = this.container.inventorySlots.get(i);

            if (this.isSlotSelected(slot, mouseX, mouseY) && slot.isEnabled())
            {
                return slot;
            }
        }

        return null;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        else
        {
            boolean flag = this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(button);
            Slot slot = this.getSelectedSlot(mouseX, mouseY);
            long i = Util.milliTime();
            this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == button;
            this.ignoreMouseUp = false;

            if (button != 0 && button != 1 && !flag)
            {
                this.hotkeySwapItems(button);
            }
            else
            {
                int j = this.guiLeft;
                int k = this.guiTop;
                boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k, button);
                int l = -1;

                if (slot != null)
                {
                    l = slot.slotNumber;
                }

                if (flag1)
                {
                    l = -999;
                }

                if (this.mc.gameSettings.touchscreen && flag1 && this.mc.player.inventory.getItemStack().isEmpty())
                {
                    this.mc.displayGuiScreen((Screen)null);
                    return true;
                }

                if (l != -1)
                {
                    if (this.mc.gameSettings.touchscreen)
                    {
                        if (slot != null && slot.getHasStack())
                        {
                            this.clickedSlot = slot;
                            this.draggedStack = ItemStack.EMPTY;
                            this.isRightMouseClick = button == 1;
                        }
                        else
                        {
                            this.clickedSlot = null;
                        }
                    }
                    else if (!this.dragSplitting)
                    {
                        if (this.mc.player.inventory.getItemStack().isEmpty())
                        {
                            if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(button))
                            {
                                this.handleMouseClick(slot, l, button, ClickType.CLONE);
                            }
                            else
                            {
                                boolean flag2 = l != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344));
                                ClickType clicktype = ClickType.PICKUP;

                                if (flag2)
                                {
                                    this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                    clicktype = ClickType.QUICK_MOVE;
                                }
                                else if (l == -999)
                                {
                                    clicktype = ClickType.THROW;
                                }

                                this.handleMouseClick(slot, l, button, clicktype);
                            }

                            this.ignoreMouseUp = true;
                        }
                        else
                        {
                            this.dragSplitting = true;
                            this.dragSplittingButton = button;
                            this.dragSplittingSlots.clear();

                            if (button == 0)
                            {
                                this.dragSplittingLimit = 0;
                            }
                            else if (button == 1)
                            {
                                this.dragSplittingLimit = 1;
                            }
                            else if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(button))
                            {
                                this.dragSplittingLimit = 2;
                            }
                        }
                    }
                }
            }

            this.lastClickSlot = slot;
            this.lastClickTime = i;
            this.lastClickButton = button;
            return true;
        }
    }

    private void hotkeySwapItems(int keyCode)
    {
        if (this.hoveredSlot != null && this.mc.player.inventory.getItemStack().isEmpty())
        {
            if (this.mc.gameSettings.keyBindSwapHands.matchesMouseKey(keyCode))
            {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 40, ClickType.SWAP);
                return;
            }

            for (int i = 0; i < 9; ++i)
            {
                if (this.mc.gameSettings.keyBindsHotbar[i].matchesMouseKey(keyCode))
                {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
                }
            }
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton)
    {
        return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        Slot slot = this.getSelectedSlot(mouseX, mouseY);
        ItemStack itemstack = this.mc.player.inventory.getItemStack();

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
        {
            if (button == 0 || button == 1)
            {
                if (this.draggedStack.isEmpty())
                {
                    if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty())
                    {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                }
                else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false))
                {
                    long i = Util.milliTime();

                    if (this.currentDragTargetSlot == slot)
                    {
                        if (i - this.dragItemDropDelay > 500L)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.dragItemDropDelay = i + 750L;
                            this.draggedStack.shrink(1);
                        }
                    }
                    else
                    {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = i;
                    }
                }
            }
        }
        else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.container.canDragIntoSlot(slot))
        {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
        }

        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        Slot slot = this.getSelectedSlot(mouseX, mouseY);
        int i = this.guiLeft;
        int j = this.guiTop;
        boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j, button);
        int k = -1;

        if (slot != null)
        {
            k = slot.slotNumber;
        }

        if (flag)
        {
            k = -999;
        }

        if (this.doubleClick && slot != null && button == 0 && this.container.canMergeSlot(ItemStack.EMPTY, slot))
        {
            if (hasShiftDown())
            {
                if (!this.shiftClickedSlot.isEmpty())
                {
                    for (Slot slot2 : this.container.inventorySlots)
                    {
                        if (slot2 != null && slot2.canTakeStack(this.mc.player) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true))
                        {
                            this.handleMouseClick(slot2, slot2.slotNumber, button, ClickType.QUICK_MOVE);
                        }
                    }
                }
            }
            else
            {
                this.handleMouseClick(slot, k, button, ClickType.PICKUP_ALL);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
        }
        else
        {
            if (this.dragSplitting && this.dragSplittingButton != button)
            {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return true;
            }

            if (this.ignoreMouseUp)
            {
                this.ignoreMouseUp = false;
                return true;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
            {
                if (button == 0 || button == 1)
                {
                    if (this.draggedStack.isEmpty() && slot != this.clickedSlot)
                    {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);

                    if (k != -1 && !this.draggedStack.isEmpty() && flag2)
                    {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, ClickType.PICKUP);
                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);

                        if (this.mc.player.inventory.getItemStack().isEmpty())
                        {
                            this.returningStack = ItemStack.EMPTY;
                        }
                        else
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, ClickType.PICKUP);
                            this.touchUpX = MathHelper.floor(mouseX - (double)i);
                            this.touchUpY = MathHelper.floor(mouseY - (double)j);
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Util.milliTime();
                        }
                    }
                    else if (!this.draggedStack.isEmpty())
                    {
                        this.touchUpX = MathHelper.floor(mouseX - (double)i);
                        this.touchUpY = MathHelper.floor(mouseY - (double)j);
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Util.milliTime();
                    }

                    this.draggedStack = ItemStack.EMPTY;
                    this.clickedSlot = null;
                }
            }
            else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
            {
                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);

                for (Slot slot1 : this.dragSplittingSlots)
                {
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
                }

                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
            }
            else if (!this.mc.player.inventory.getItemStack().isEmpty())
            {
                if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(button))
                {
                    this.handleMouseClick(slot, k, button, ClickType.CLONE);
                }
                else
                {
                    boolean flag1 = k != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344));

                    if (flag1)
                    {
                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                    }

                    this.handleMouseClick(slot, k, button, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.mc.player.inventory.getItemStack().isEmpty())
        {
            this.lastClickTime = 0L;
        }

        this.dragSplitting = false;
        return true;
    }

    private boolean isSlotSelected(Slot slotIn, double mouseX, double mouseY)
    {
        return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY)
    {
        int i = this.guiLeft;
        int j = this.guiTop;
        mouseX = mouseX - (double)i;
        mouseY = mouseY - (double)j;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        if (slotIn != null)
        {
            slotId = slotIn.slotNumber;
        }

        this.mc.playerController.windowClick(this.container.windowId, slotId, mouseButton, type, this.mc.player);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        else if (this.mc.gameSettings.keyBindInventory.matchesKey(keyCode, scanCode))
        {
            this.closeScreen();
            return true;
        }
        else
        {
            this.itemStackMoved(keyCode, scanCode);

            if (this.hoveredSlot != null && this.hoveredSlot.getHasStack())
            {
                if (this.mc.gameSettings.keyBindPickBlock.matchesKey(keyCode, scanCode))
                {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
                }
                else if (this.mc.gameSettings.keyBindDrop.matchesKey(keyCode, scanCode))
                {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, hasControlDown() ? 1 : 0, ClickType.THROW);
                }
            }

            return true;
        }
    }

    protected boolean itemStackMoved(int keyCode, int scanCode)
    {
        if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null)
        {
            if (this.mc.gameSettings.keyBindSwapHands.matchesKey(keyCode, scanCode))
            {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 40, ClickType.SWAP);
                return true;
            }

            for (int i = 0; i < 9; ++i)
            {
                if (this.mc.gameSettings.keyBindsHotbar[i].matchesKey(keyCode, scanCode))
                {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    public void onClose()
    {
        if (this.mc.player != null)
        {
            this.container.onContainerClosed(this.mc.player);
        }
    }

    public boolean isPauseScreen()
    {
        return false;
    }

    public void tick()
    {
        super.tick();

        if (!this.mc.player.isAlive() || this.mc.player.removed)
        {
            this.mc.player.closeScreen();
        }
    }

    public T getContainer()
    {
        return this.container;
    }

    public void closeScreen()
    {
        this.mc.player.closeScreen();
        super.closeScreen();
    }
}
