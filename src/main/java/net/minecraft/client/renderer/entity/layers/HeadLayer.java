package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.UUID;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.StringUtils;

public class HeadLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M>
{
    private final float field_239402_a_;
    private final float field_239403_b_;
    private final float field_239404_c_;

    public HeadLayer(IEntityRenderer<T, M> p_i50946_1_)
    {
        this(p_i50946_1_, 1.0F, 1.0F, 1.0F);
    }

    public HeadLayer(IEntityRenderer<T, M> p_i232475_1_, float p_i232475_2_, float p_i232475_3_, float p_i232475_4_)
    {
        super(p_i232475_1_);
        this.field_239402_a_ = p_i232475_2_;
        this.field_239403_b_ = p_i232475_3_;
        this.field_239404_c_ = p_i232475_4_;
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.HEAD);

        if (!itemstack.isEmpty())
        {
            Item item = itemstack.getItem();
            matrixStackIn.push();
            matrixStackIn.scale(this.field_239402_a_, this.field_239403_b_, this.field_239404_c_);
            boolean flag = entitylivingbaseIn instanceof VillagerEntity || entitylivingbaseIn instanceof ZombieVillagerEntity;

            if (entitylivingbaseIn.isChild() && !(entitylivingbaseIn instanceof VillagerEntity))
            {
                float f = 2.0F;
                float f1 = 1.4F;
                matrixStackIn.translate(0.0D, 0.03125D, 0.0D);
                matrixStackIn.scale(0.7F, 0.7F, 0.7F);
                matrixStackIn.translate(0.0D, 1.0D, 0.0D);
            }

            this.getEntityModel().getModelHead().translateRotate(matrixStackIn);

            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock)
            {
                float f3 = 1.1875F;
                matrixStackIn.scale(1.1875F, -1.1875F, -1.1875F);

                if (flag)
                {
                    matrixStackIn.translate(0.0D, 0.0625D, 0.0D);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTag())
                {
                    CompoundNBT compoundnbt = itemstack.getTag();

                    if (compoundnbt.contains("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
                    }
                    else if (compoundnbt.contains("SkullOwner", 8))
                    {
                        String s = compoundnbt.getString("SkullOwner");

                        if (!StringUtils.isBlank(s))
                        {
                            gameprofile = SkullTileEntity.updateGameProfile(new GameProfile((UUID)null, s));
                            compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                        }
                    }
                }

                matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
                SkullTileEntityRenderer.render((Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameprofile, limbSwing, matrixStackIn, bufferIn, packedLightIn);
            }
            else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getEquipmentSlot() != EquipmentSlotType.HEAD)
            {
                float f2 = 0.625F;
                matrixStackIn.translate(0.0D, -0.25D, 0.0D);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F));
                matrixStackIn.scale(0.625F, -0.625F, -0.625F);

                if (flag)
                {
                    matrixStackIn.translate(0.0D, 0.1875D, 0.0D);
                }

                Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD, false, matrixStackIn, bufferIn, packedLightIn);
            }

            matrixStackIn.pop();
        }
    }
}
