package com.mactso.nozombievillagers.common.logic;

import java.util.List;
import java.util.Optional;

import com.mactso.nozombievillagers.modloader.config.MyConfig;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter.ScopedCollector;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;


/**
 * Handles all spawn-time logic for {@link ZombieVillager} entities.
 * <p>
 * Determines whether a spawned zombie villager should be replaced by a normal zombie
 * based on spawn type and configurable probabilities.
 * <p>
 * Enforces spawner population limits, handles structure and natural spawns,
 * and despawns replaced villagers using {@link ZombieVillager#discard()}.
 * <p>
 * This class is self-contained, stateless, and contains no Forge event references.
 */

public class ReplaceZombieVillager {
	
	private static final org.slf4j.Logger SPAWNERLOGGER = LogUtils.getLogger();

    /**
     * Evaluates a single {@link ZombieVillager} spawn and applies replacement logic.
     * <p>
     * Checks spawn type, applies random replacement probabilities, and enforces
     * spawner population caps. If a replacement occurs, spawns a normal {@link Zombie}
     * at the exact same coordinates and despawns the original villager.
     *
     * @param level     The server world
     * @param spawnType Type of spawn (NATURAL, SPAWNER, STRUCTURE)
     * @param zv        The zombie villager being spawned
     * @param spawner   The spawner, if present
     */
	public static void handleZombieVillagerSpawn(
			ServerLevel level,
			EntitySpawnReason spawnType,
			ZombieVillager zv,
			BaseSpawner spawner
	) {
	
		boolean isSpawner   = (spawnType == EntitySpawnReason.SPAWNER);
		boolean isNatural   = (spawnType == EntitySpawnReason.NATURAL);
		boolean isStructure = (spawnType == EntitySpawnReason.STRUCTURE);
	
		if (!(isSpawner || isNatural || isStructure)) {
			return;
		}
	

		boolean replace = shouldReplaceZombieVillager(level.getRandom(), spawnType);
	
		if (isSpawner) {
			
			// ALL spawner null handling lives here
			if (spawner == null || spawner.getSpawnerBlockEntity() == null) {
				return;
			}

			CompoundTag tag = saveSpawnerToTag (spawner);
			BlockPos pos = spawner.getSpawnerBlockEntity().getBlockPos();
	
			if (isSpawnerOverCap(level, tag, pos)) {
				zv.discard();
				return;
			}
		}
	
		if (replace) {
			spawnReplacementZombie(level, zv);
			zv.discard();
		}
	}

	/** 
	 * Returns true if the given spawn type should be replaced by a normal zombie,
	 * based on configured probabilities in {@link MyConfig}.
	 */
	private static boolean shouldReplaceZombieVillager(
			RandomSource random,
			EntitySpawnReason spawnType
	) {
	
		if (spawnType == EntitySpawnReason.STRUCTURE) {
			return random.nextDouble() * 100 < MyConfig.getOddsStructureJustZombie();
		}
	
		if (spawnType == EntitySpawnReason.SPAWNER) {
			return random.nextDouble() * 100 < MyConfig.getOddsSpawnerJustZombie();
		}
	
		if (spawnType == EntitySpawnReason.NATURAL) {
			return random.nextDouble() * 100 < MyConfig.getOddsNaturalJustZombie();
		}
	
		return false;
	}

	/** 
	 * Returns true if the spawner has reached or exceeded its {@code MaxNearbyEntities}
	 * limit, taking into account nearby zombies and zombie villagers.
	 */
	private static boolean isSpawnerOverCap(
			ServerLevel level,
			CompoundTag tag,
			BlockPos pos
	) {
	
		Optional<Integer> optRange = tag.getInt("SpawnRange");
		if (optRange.isEmpty()) 
			return false;
		Optional<Integer> optMaxNearby = tag.getInt("MaxNearbyEntities");
		if (optMaxNearby.isEmpty())
			return false;
		int range = optRange.get().intValue();
		int maxNearby = optMaxNearby.get().intValue();
		
		AABB box = new AABB(
				pos.west(range).getX(), pos.below(3).getY(), pos.north(range).getZ(),
				pos.east(range).getX(), pos.above(3).getY(), pos.south(range).getZ()
		);
	
		List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, box);
		List<ZombieVillager> villagers = level.getEntitiesOfClass(ZombieVillager.class, box);
	
		int nonVillagerZombies = zombies.size();
		for (Zombie z : zombies) {
			if (z instanceof ZombieVillager) {
				nonVillagerZombies--;
			}
		}
	
		return nonVillagerZombies + villagers.size() >= maxNearby;
	}

	/**
	 * Spawns a replacement {@link Zombie} at the exact float coordinates of the
	 * original {@link ZombieVillager}, preserving baby/adult state.
	 */
    private static void spawnReplacementZombie(ServerLevel level, ZombieVillager zv) {
        var block = level.getBlockState(zv.blockPosition()).getBlock();
        if (block == Blocks.AIR || block == Blocks.CAVE_AIR) {

            Mob zombie = EntityType.ZOMBIE.create(level,EntitySpawnReason.NATURAL);
            if (zombie != null) {
                zombie.setPos(zv.getX(), zv.getY(), zv.getZ()); // exact float coordinates
                zombie.setBaby(zv.isBaby());
                level.addFreshEntity(zombie);
            }
        }
    }
    
	/** 
	 * Serializes a SpawnerBlockEntity into a CompoundTag. 
	 * @param sbe the spawner block entity 
	 * @return serialized NBT representing the spawner 
	 */
	public static CompoundTag saveSpawnerToTag(BaseSpawner spawner) {
		ScopedCollector problemReporter = new ScopedCollector(SPAWNERLOGGER);
	    TagValueOutput output = TagValueOutput.createWithoutContext(problemReporter);
	    spawner.save(output);
	    return output.buildResult();
	}

}
