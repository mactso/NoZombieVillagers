package com.mactso.nozombievillagers.modloader.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.nozombievillagers.modloader.main.Main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	static {

		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static double getOddsSpawnerJustZombie() {
		return oddsSpawnerJustZombie;
	}

	public static double getOddsNaturalJustZombie() {
		return oddsNaturalJustZombie;
	}

	public static double getOddsStructureJustZombie() {
		return oddsStructureJustZombie;
	}

	public static double oddsSpawnerJustZombie;
	private static double oddsNaturalJustZombie;
	private static double oddsStructureJustZombie;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC) {
			bakeConfig();
		}
	}

	public static void bakeConfig() {
		oddsSpawnerJustZombie = COMMON.oddsSpawnerJustZombie.get();
		oddsNaturalJustZombie = COMMON.oddsNaturalJustZombie.get();
		oddsStructureJustZombie = COMMON.oddsStructureJustZombie.get();
	}

	public static class Common {

		public final DoubleValue oddsSpawnerJustZombie;
		public final DoubleValue oddsNaturalJustZombie;
		public final DoubleValue oddsStructureJustZombie;
		
		public Common(ForgeConfigSpec.Builder builder) {

			oddsSpawnerJustZombie = builder.comment("Odds Spawner ZV is just a Zombie")
					.translation(Main.MODID + ".config." + "oddsSpawnerJustZombie")
					.defineInRange("oddsSpawnerJustZombie", () -> 96.0, 0.0, 100.0);

			oddsNaturalJustZombie = builder.comment("Odds Natural ZV is just a Zombie")
					.translation(Main.MODID + ".config." + "oddsNaturalJustZombie")
					.defineInRange("oddsNaturalJustZombie", () -> 0.0, 0.0, 100.0);

			oddsStructureJustZombie = builder.comment("Odds Structure Zombie Villagers are just a Zombie.")
					.translation(Main.MODID + ".config." + "oddsStructureJustZombie")
					.defineInRange("oddsStructureJustZombie", () -> 99.9, 0.0, 100.0);
			
		}
	}

}
