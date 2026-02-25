package com.mactso.nozombievillagers.modloader.events;

import com.mactso.nozombievillagers.common.logic.ReplaceZombieVillager;
import com.mactso.nozombievillagers.modloader.main.Main;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.BaseSpawner;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Forge event adapter for {@link ZombieVillager} spawn finalization.
 * <p>
 * Filters server-side {@link MobSpawnEvent.FinalizeSpawn} events and delegates
 * all decision-making and side effects to {@code ReplaceZombieVillager}.
 * <p>
 * This class contains no game logic and exists solely to bridge Forge events
 * with the mod’s reusable, testable spawn-handling logic.
 */

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class SpawnEventHandler {


	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {

		if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
			return;
		}

		if (!(event.getEntity() instanceof ZombieVillager zv)) {
			return;
		}

		MobSpawnType spawnType = event.getSpawnType();
		BaseSpawner spawner = event.getSpawner();
		
		ReplaceZombieVillager.handleZombieVillagerSpawn(
				serverLevel,
				spawnType,
				zv,
				spawner
		);
	}


}
