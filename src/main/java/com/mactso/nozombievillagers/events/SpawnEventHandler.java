package com.mactso.nozombievillagers.events;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.nozombievillagers.Main;
import com.mactso.nozombievillagers.config.MyConfig;
import com.mactso.nozombievillagers.util.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class SpawnEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {

		if (!(event.getLevel() instanceof ServerLevel)) {
			return;
		}

		if (event.getEntity() instanceof ZombieVillager zv) {
			MobSpawnType reason = event.getSpawnType();
			boolean isSpawner = (reason == MobSpawnType.SPAWNER);
			boolean isNatural = (reason == MobSpawnType.NATURAL);
			boolean replace = false;
			if (isSpawner || isNatural) {

				ServerLevel serverLevel = (ServerLevel) event.getLevel();
				RandomSource rand = serverLevel.getRandom();
				if (isSpawner) {
					if (rand.nextDouble() * 100 < MyConfig.getOddsSpawnerJustZombie()) {
						replace = true;
					}
				}

				if (isNatural) {

					if (rand.nextDouble() * 100 < MyConfig.getOddsNaturalJustZombie()) {

						replace = true;
					}

				}

				if (isSpawner) {
					CompoundTag tag = new CompoundTag();
					tag = event.getSpawner().save(tag);
					int range = tag.getInt("SpawnRange");
					int maxZ = tag.getInt("MaxNearbyEntities");
					BlockPos pos = event.getSpawner().getSpawnerBlockEntity().getBlockPos();


					List<Zombie> listZ = serverLevel.getEntitiesOfClass(Zombie.class,
								new AABB(pos.west(range).getX(), pos.below(3).getY(), pos.north(range).getZ(),pos.east(range).getX(),pos.above(3).getY(),pos.south(range).getZ()));
					int zCount = listZ.size();
					for (Zombie z : listZ) {
						if (z instanceof ZombieVillager) {
							zCount--;
						}
					}
					List<ZombieVillager> listZV = serverLevel.getEntitiesOfClass(ZombieVillager.class,
							new AABB(pos.west(range).getX(), pos.below(3).getY(), pos.north(range).getZ(),pos.east(range).getX(),pos.above(3).getY(),pos.south(range).getZ()));
					int localZ = zCount + listZV.size();

					if (localZ >= maxZ) {
						sendToCornfield(zv);
						return;
					}
				}

				if (replace) {

					Block b = serverLevel.getBlockState(event.getEntity().blockPosition()).getBlock();
					if (b == Blocks.AIR || b == Blocks.CAVE_AIR) {
						Utility.populateXEntityType(EntityType.ZOMBIE, serverLevel, zv.blockPosition(), 1, zv.isBaby());
					}
					sendToCornfield(zv);
				}
			}
		}
	}

	private void sendToCornfield(ZombieVillager zv) {

		zv.setPosRaw(zv.getX(), -66, zv.getZ());
	}
}
