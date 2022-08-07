package com.mactso.nozombievillagers.events;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.nozombievillagers.Main;
import com.mactso.nozombievillagers.config.MyConfig;
import com.mactso.nozombievillagers.util.Utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class SpawnEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {

		int x = 3;
		if (!(event.getWorld() instanceof ServerLevel)) {
			return;
		}

		if (event.getEntityLiving() instanceof ZombieVillager zv) {

			MobSpawnType reason = event.getSpawnReason();
			boolean isSpawner = (reason == MobSpawnType.SPAWNER);
			boolean isNatural = (reason == MobSpawnType.NATURAL);
			if (isSpawner || isNatural) {
				ServerLevel serverLevel = (ServerLevel) event.getWorld();
				Random rand = serverLevel.getRandom();

				if (isSpawner) {
					if (rand.nextDouble() * 100 < MyConfig.getOddsSpawnerJustZombie()) {
						return;
					}
				}

				if (isNatural) {
					if (rand.nextDouble() * 100 < MyConfig.getOddsNaturalJustZombie()) {
						return;
					}
				}

				if (event.isCancelable()) {
					Utility.populateEntityType(EntityType.ZOMBIE, serverLevel, zv.blockPosition(), 1, 1, false, false);
					event.setCanceled(true);
					return;
				}

			}

		}

	}

}
