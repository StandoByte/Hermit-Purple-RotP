package com.zeml.rotp_zhp.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonUtil;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zhp.entity.damaging.projectile.HPVineGrabEntity;
import com.zeml.rotp_zhp.init.InitStands;
import com.zeml.rotp_zhp.util.StandHamonDamage;
import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HPGrabOverdrive extends StandEntityAction {
    public HPGrabOverdrive(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        Optional<Boolean> overdrive = INonStandPower.getNonStandPowerOptional(user).map(ipower ->{
            boolean returning = false;
            Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
            if(hamonOp.isPresent()){
                HamonData hamon = hamonOp.get();
                returning = hamon.isSkillLearned(ModHamonSkills.SUNLIGHT_YELLOW_OVERDRIVE.get());
            }
            return returning;
        });
        boolean condition = overdrive.orElse(false);
        return condition?ActionConditionResult.POSITIVE:ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide()) {
            if(getLandedVineStand(standEntity).isPresent()){
                LivingEntity target = getLandedVineStand(standEntity).get().getEntityAttachedTo();
                if(target != null){
                    INonStandPower.getNonStandPowerOptional(userPower.getUser()).ifPresent(ipower->{
                        Optional<HamonData> hamonOp = ipower.getTypeSpecificData(ModPowers.HAMON.get());
                        if(hamonOp.isPresent()){
                            HamonData hamon = hamonOp.get();
                            hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH,5);
                            StandHamonDamage.dealHamonDamage(target, 5,userPower.getUser() , null, attack -> attack.hamonParticle(ModParticles.HAMON_SPARK.get()), userPower,1.5F,1);
                            ipower.consumeEnergy(.2F*ipower.getMaxEnergy());
                        }
                    });
                }
            }
        }
    }


    @Override
    public StandAction[] getExtraUnlockable() {
        return new StandAction[] { InitStands.HP_HEAL_VINE.get()};
    }

    public static Optional<HPVineGrabEntity> getLandedVineStand(StandEntity stand) {
        List<HPVineGrabEntity> vineLanded = stand.level.getEntitiesOfClass(HPVineGrabEntity.class,
                stand.getBoundingBox().inflate(16), redBind -> stand.is(redBind.getOwner()) && redBind.isAttachedToAnEntity());
        return !vineLanded.isEmpty() ? Optional.of(vineLanded.get(0)) : Optional.empty();
    }
}
