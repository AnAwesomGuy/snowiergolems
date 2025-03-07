package net.anawesomguy.snowiergolems.entity;

import net.anawesomguy.snowiergolems.GolemObjects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EnchantedSnowball extends Snowball {
    protected static final EntityDataAccessor<Byte> PIERCE_LEVEL =
        SynchedEntityData.defineId(EnchantedSnowball.class, EntityDataSerializers.BYTE);
    public static final int IGNITE_TICKS = 400; //2 seconds

    public float baseDamage = 0;

    protected final ItemStack shotFrom;
    @Nullable
    private EnchantedItemInUse itemInUse;

    public EnchantedSnowball(EntityType<? extends EnchantedSnowball> entityType, Level level, ItemStack shotFrom) {
        super(entityType, level);

        ItemEnchantments enchantments = shotFrom.getTagEnchantments();
        if (!enchantments.isEmpty())
            getItem().set(DataComponents.ENCHANTMENTS, enchantments);

        this.shotFrom = Objects.requireNonNull(shotFrom);

        if (level instanceof ServerLevel && !shotFrom.isEmpty()) {
            EnchantedItemInUse itemInUse = getOrCreateItemInUse(null);
            EnchantmentHelper.runIterationOnItem(
                shotFrom, (enchant, lvl) -> enchant.value().onProjectileSpawned((ServerLevel)level, lvl, itemInUse, this));
        }
    }

    public EnchantedSnowball(EntityType<? extends EnchantedSnowball> entityType, Level level) {
        this(entityType, level, ItemStack.EMPTY);
    }

    public EnchantedSnowball(Level level, double x, double y, double z, ItemStack shotFrom) {
        this(GolemObjects.ENCHANTED_SNOWBALL, level, shotFrom);
        setPos(x, y, z);
    }

    public EnchantedSnowball(Level level, LivingEntity shooter, ItemStack shotFrom) {
        this(level, shooter.getX(), shooter.getEyeY() - 0.1F, shooter.getZ(), shotFrom);
        setOwner(shooter);
        if (shooter instanceof SnowGolem && shooter.getItemBySlot(EquipmentSlot.HEAD).is(GolemObjects.GOLEM_HAT_ITEM))
            getOrCreateItemInUse(EquipmentSlot.HEAD);
    }

    public ItemStack getShotFrom() {
        return shotFrom;
    }

    protected EnchantedItemInUse getOrCreateItemInUse(@Nullable EquipmentSlot slot) {
        if (itemInUse == null) {
            LivingEntity owner = getOwner() instanceof LivingEntity living ? living : null;
            return itemInUse = (owner != null && slot != null) ?
                new EnchantedItemInUse(shotFrom, slot, owner) :
                new EnchantedItemInUse(shotFrom, slot, owner, item -> {});
        }
        return itemInUse;
    }

    @Override
    protected void defineSynchedData(Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PIERCE_LEVEL, (byte)0);
    }

    protected void setPierceLevel(byte pierceLevel) {
        entityData.set(PIERCE_LEVEL, pierceLevel);
    }

    public byte getPierceLevel() {
        return entityData.get(PIERCE_LEVEL);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (baseDamage != 0F)
            compound.putDouble("damage", baseDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.baseDamage = compound.getFloat("damage"); // 0 is default
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (level() instanceof ServerLevel level) {
            Vec3 vec3 = result.getBlockPos().clampLocationWithin(result.getLocation());
            EnchantmentHelper.onHitBlock(
                level,
                shotFrom,
                getOwner() instanceof LivingEntity owner ? owner : null,
                this,
                null,
                vec3,
                level.getBlockState(result.getBlockPos()),
                item -> {}
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // no super call (should be fine) (might break some mods tho)
        Entity entity = result.getEntity();
        ItemStack shotFrom = this.shotFrom;
        DamageSource source = this.damageSources().thrown(this, getOwner());

        float damage;
        if (level() instanceof ServerLevel level && !this.shotFrom.isEmpty()) {
            // calculate damage with enchants
            damage = EnchantmentHelper.modifyDamage(level, shotFrom, this, source, baseDamage);
            if (entity instanceof Blaze)
                damage += 3; // blaze's damage three more

            // AbstractArrow#doKnockback
            if (entity instanceof LivingEntity living) {
                double knockback = EnchantmentHelper.modifyKnockback(level, shotFrom, entity, source, 0F);
                if (knockback > 0F) {
                    double resistance = Math.max(0, 1 - living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 = getDeltaMovement().multiply(1, 0, 1).normalize().scale(knockback * resistance * 0.6);
                    if (vec3.lengthSqr() > 0)
                        entity.push(vec3.x, 0.1, vec3.z);
                }
            }

            // post attack effects (like freeze or smth)
            EnchantmentHelper.doPostAttackEffectsWithItemSource(level, this, source, shotFrom);
        } else
            damage = baseDamage;
        entity.hurt(source, damage);

        if (isOnFire()) // ignite if on fire (flame enchant)
            entity.igniteForTicks(IGNITE_TICKS);
    }
}
