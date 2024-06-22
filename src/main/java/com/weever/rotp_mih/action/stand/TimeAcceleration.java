package com.weever.rotp_mih.action.stand;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.weever.rotp_mih.RotpMadeInHeavenAddon;
import com.weever.rotp_mih.entity.stand.stands.MihEntity;

import com.weever.rotp_mih.init.InitStands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class TimeAcceleration extends StandEntityAction {
    public TimeAcceleration(Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target) {
        if(power.getUser().getTags().contains("weever_heaven")) {
            return InitStands.MIH_UNIVERSE_RESET.get();
        }
        return super.replaceAction(power, target);
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (world.isClientSide()) { return; }
        MihEntity MiH = (MihEntity) standEntity;
        MiH.setTimeAccel(true);
    }

    @Override
    public StandAction[] getExtraUnlockable() {
        return new StandAction[] { InitStands.MIH_UNIVERSE_RESET.get()};
    }
}