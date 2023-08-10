package net.minecraft.entity.monster.piglin;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public abstract class AbstractPiglinEntity extends MonsterEntity
{
    protected static final DataParameter<Boolean> field_242333_b = EntityDataManager.createKey(AbstractPiglinEntity.class, DataSerializers.BOOLEAN);
    protected int field_242334_c = 0;

    public AbstractPiglinEntity(EntityType <? extends AbstractPiglinEntity > p_i241915_1_, World p_i241915_2_)
    {
        super(p_i241915_1_, p_i241915_2_);
        this.setCanPickUpLoot(true);
        this.func_242339_eS();
        this.setPathPriority(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    private void func_242339_eS()
    {
        if (GroundPathHelper.isGroundNavigator(this))
        {
            ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
        }
    }

    protected abstract boolean func_234422_eK_();

    public void func_242340_t(boolean p_242340_1_)
    {
        this.getDataManager().set(field_242333_b, p_242340_1_);
    }

    protected boolean func_242335_eK()
    {
        return this.getDataManager().get(field_242333_b);
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(field_242333_b, false);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);

        if (this.func_242335_eK())
        {
            compound.putBoolean("IsImmuneToZombification", true);
        }

        compound.putInt("TimeInOverworld", this.field_242334_c);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return this.isChild() ? -0.05D : -0.45D;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.func_242340_t(compound.getBoolean("IsImmuneToZombification"));
        this.field_242334_c = compound.getInt("TimeInOverworld");
    }

    protected void updateAITasks()
    {
        super.updateAITasks();

        if (this.func_242336_eL())
        {
            ++this.field_242334_c;
        }
        else
        {
            this.field_242334_c = 0;
        }

        if (this.field_242334_c > 300)
        {
            this.func_241848_eP();
            this.func_234416_a_((ServerWorld)this.world);
        }
    }

    public boolean func_242336_eL()
    {
        return !this.world.getDimensionType().isPiglinSafe() && !this.func_242335_eK() && !this.isAIDisabled();
    }

    protected void func_234416_a_(ServerWorld p_234416_1_)
    {
        ZombifiedPiglinEntity zombifiedpiglinentity = this.func_233656_b_(EntityType.ZOMBIFIED_PIGLIN, true);

        if (zombifiedpiglinentity != null)
        {
            zombifiedpiglinentity.addPotionEffect(new EffectInstance(Effects.NAUSEA, 200, 0));
        }
    }

    public boolean func_242337_eM()
    {
        return !this.isChild();
    }

    public abstract PiglinAction func_234424_eM_();

    @Nullable

    /**
     * Gets the active target the Task system uses for tracking
     */
    public LivingEntity getAttackTarget()
    {
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity)null);
    }

    protected boolean func_242338_eO()
    {
        return this.getHeldItemMainhand().getItem() instanceof TieredItem;
    }

    /**
     * Plays living's sound at its position
     */
    public void playAmbientSound()
    {
        if (PiglinTasks.func_234520_i_(this))
        {
            super.playAmbientSound();
        }
    }

    protected void sendDebugPackets()
    {
        super.sendDebugPackets();
        DebugPacketSender.sendLivingEntity(this);
    }

    protected abstract void func_241848_eP();
}
