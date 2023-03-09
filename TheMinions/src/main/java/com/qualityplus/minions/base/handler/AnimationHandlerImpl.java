package com.qualityplus.minions.base.handler;

import com.qualityplus.assistant.util.armorstand.ArmorStandUtil;
import com.qualityplus.assistant.util.math.MathUtils;
import com.qualityplus.assistant.util.random.RandomSelector;
import com.qualityplus.minions.api.handler.AnimationHandler;
import com.qualityplus.minions.api.handler.ArmorStandHandler;
import com.qualityplus.minions.base.minions.entity.getter.LayoutGetter;
import com.qualityplus.minions.base.minions.entity.mob.MinionMobEntity;
import com.qualityplus.minions.base.minions.minion.Minion;
import com.qualityplus.minions.base.minions.minion.layout.LayoutType;
import com.qualityplus.minions.base.minions.minion.mob.MinionMob;
import com.qualityplus.minions.util.MinionAnimationUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
public final class AnimationHandlerImpl implements AnimationHandler, LayoutGetter {
    private final UUID minionUniqueId;
    private final Minion minion;

    @Override
    public CompletableFuture<Block> getBlockToRotate(ArmorStandHandler handler) {
        CompletableFuture<Block> future = new CompletableFuture<>();

        handler.manipulateEntity(armorStand1 -> armorStand1.setHeadPose(new EulerAngle(-24.5, 0, 0)));

        List<Vector> vectors = getMinionLayout(minion).equals(LayoutType.THREE_X_THREE) ? MinionAnimationUtil.getThree() : MinionAnimationUtil.getSecond();

        Vector vector = /*vectors.get(0)*/RandomSelector.getRandom(vectors);

        Location location = Optional.ofNullable(handler)
                .map(ArmorStandHandler::getLocation)
                .filter(Objects::nonNull)
                .map(e -> e.subtract(new Vector(0, 1, 0)))
                .orElse(null);

        if(location == null){
            future.complete(null);
            return future;
        }

        if(vector == null) {
            future.complete(location.getBlock());
            return future;
        }

        Location newLocation = location.clone().add(vector);

        handler.manipulateEntity(armorStand -> ArmorStandUtil.rotate(armorStand, newLocation));

        future.complete(newLocation.getBlock());

        return future;
    }

    @Override
    public CompletableFuture<MinionMobEntity> getEntityToRotate(ArmorStandHandler handler) {
        CompletableFuture<MinionMobEntity> future = new CompletableFuture<>();

        //handler.manipulateEntity(armorStand1 -> armorStand1.setHeadPose(new EulerAngle(-24.5, 0, 0)));

        Location location = Optional.ofNullable(handler)
                .map(ArmorStandHandler::getLocation)
                .filter(Objects::nonNull)
                .orElse(null);

        if(location == null){
            future.complete(MinionMobEntity.builder().build());
            return future;
        }

        Entity entity = getNearEntity(location);

        int random = MathUtils.randomUpTo(100);

        if(entity == null || random > 50) {
            double x = MathUtils.randomBetween(1, 2);
            double z = MathUtils.randomBetween(1, 2);

            Location newLocation = location.clone()
                    .add(x, 0, z);

            handler.manipulateEntity(armorStand -> ArmorStandUtil.rotate(armorStand, newLocation));

            future.complete(MinionMobEntity.builder().location(newLocation).build());
            return future;
        }

        Vector vector = entity.getLocation().getDirection().normalize();

        Location newLocation = location.clone().add(vector);

        handler.manipulateEntity(armorStand -> ArmorStandUtil.rotate(armorStand, newLocation));

        future.complete(MinionMobEntity.builder().entity(entity).build());

        return future;
    }

    private Entity getNearEntity(Location location){
        MinionMob minionMob = minion.getMinionLayout().getMinionMob();

        try {
            return location.getWorld().getNearbyEntities(location, 2,2,2).stream()
                    .filter(entity -> entity.getType().equals(minionMob.getEntityType()))
                    .findFirst()
                    .orElse(null);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public UUID getMinionUniqueId() {
        return minionUniqueId;
    }

            /*TODO SUGAR MINION
        int time = 0;
        for(Map.Entry<Integer, VectorSection> sectionEntry : MinionAnimationUtil.SUGAR_WATER_POSITIONS.entrySet()){

            //AtomicInteger time = new AtomicInteger();

            (new BukkitRunnable() {
                public void run() {

                    sectionEntry.getValue().getFirsts().forEach(vectorr -> {
                        Location newLocation = location.clone().add(vectorr);
                        newLocation.getBlock().setType(XMaterial.DIAMOND_BLOCK.parseMaterial());
                    });

                }
            }).runTaskLater(TheMinions.getInstance(), 20 + time);

            time+=10;
        }
        */
}
