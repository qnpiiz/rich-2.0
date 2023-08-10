package net.minecraft.world.gen.feature.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class NetherFossilStructures
{
    private static final ResourceLocation[] field_236993_a_ = new ResourceLocation[] {new ResourceLocation("nether_fossils/fossil_1"), new ResourceLocation("nether_fossils/fossil_2"), new ResourceLocation("nether_fossils/fossil_3"), new ResourceLocation("nether_fossils/fossil_4"), new ResourceLocation("nether_fossils/fossil_5"), new ResourceLocation("nether_fossils/fossil_6"), new ResourceLocation("nether_fossils/fossil_7"), new ResourceLocation("nether_fossils/fossil_8"), new ResourceLocation("nether_fossils/fossil_9"), new ResourceLocation("nether_fossils/fossil_10"), new ResourceLocation("nether_fossils/fossil_11"), new ResourceLocation("nether_fossils/fossil_12"), new ResourceLocation("nether_fossils/fossil_13"), new ResourceLocation("nether_fossils/fossil_14")};

    public static void func_236994_a_(TemplateManager p_236994_0_, List<StructurePiece> p_236994_1_, Random p_236994_2_, BlockPos p_236994_3_)
    {
        Rotation rotation = Rotation.randomRotation(p_236994_2_);
        p_236994_1_.add(new NetherFossilStructures.Piece(p_236994_0_, Util.getRandomObject(field_236993_a_, p_236994_2_), p_236994_3_, rotation));
    }

    public static class Piece extends TemplateStructurePiece
    {
        private final ResourceLocation field_236995_d_;
        private final Rotation field_236996_e_;

        public Piece(TemplateManager p_i232108_1_, ResourceLocation p_i232108_2_, BlockPos p_i232108_3_, Rotation p_i232108_4_)
        {
            super(IStructurePieceType.NETHER_FOSSIL, 0);
            this.field_236995_d_ = p_i232108_2_;
            this.templatePosition = p_i232108_3_;
            this.field_236996_e_ = p_i232108_4_;
            this.func_236997_a_(p_i232108_1_);
        }

        public Piece(TemplateManager p_i232107_1_, CompoundNBT p_i232107_2_)
        {
            super(IStructurePieceType.NETHER_FOSSIL, p_i232107_2_);
            this.field_236995_d_ = new ResourceLocation(p_i232107_2_.getString("Template"));
            this.field_236996_e_ = Rotation.valueOf(p_i232107_2_.getString("Rot"));
            this.func_236997_a_(p_i232107_1_);
        }

        private void func_236997_a_(TemplateManager p_236997_1_)
        {
            Template template = p_236997_1_.getTemplateDefaulted(this.field_236995_d_);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_236996_e_).setMirror(Mirror.NONE).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, placementsettings);
        }

        protected void readAdditional(CompoundNBT tagCompound)
        {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.field_236995_d_.toString());
            tagCompound.putString("Rot", this.field_236996_e_.name());
        }

        protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb)
        {
        }

        public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_)
        {
            p_230383_5_.expandTo(this.template.getMutableBoundingBox(this.placeSettings, this.templatePosition));
            return super.func_230383_a_(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_6_, p_230383_7_);
        }
    }
}
