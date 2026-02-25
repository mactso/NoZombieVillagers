package com.mactso.nozombievillagers.common.logic;

import java.util.List;

import com.mactso.nozombievillagers.modloader.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Blocks;
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
			MobSpawnType spawnType,
			ZombieVillager zv,
			BaseSpawner spawner
	) {
	
		boolean isSpawner   = (spawnType == MobSpawnType.SPAWNER);
		boolean isNatural   = (spawnType == MobSpawnType.NATURAL);
		boolean isStructure = (spawnType == MobSpawnType.STRUCTURE);
	
		if (!(isSpawner || isNatural || isStructure)) {
			return;
		}
	
	
		boolean replace = shouldReplaceZombieVillager(level.getRandom(), spawnType);
	
		if (isSpawner) {
	
			// ALL spawner null handling lives here
			if (spawner == null || spawner.getSpawnerBlockEntity() == null) {
				return;
			}
	
			CompoundTag tag = spawner.save(new CompoundTag());
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
			MobSpawnType spawnType
	) {
	
		if (spawnType == MobSpawnType.STRUCTURE) {
			return random.nextDouble() * 100 < MyConfig.getOddsStructureJustZombie();
		}
	
		if (spawnType == MobSpawnType.SPAWNER) {
			return random.nextDouble() * 100 < MyConfig.getOddsSpawnerJustZombie();
		}
	
		if (spawnType == MobSpawnType.NATURAL) {
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
	
		int range = tag.getInt("SpawnRange");
		int maxNearby = tag.getInt("MaxNearbyEntities");
	
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
            Mob zombie = EntityType.ZOMBIE.create(level);
            if (zombie != null) {
                zombie.setPos(zv.getX(), zv.getY(), zv.getZ()); // exact float coordinates
                zombie.setBaby(zv.isBaby());
                level.addFreshEntity(zombie);
            }
        }
    }

}
