package com.mactso.nozombievillagers.config;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.nozombievillagers.Main;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {

	private static final Logger LOGGER = LogManager.getLogger();
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

	public static double oddsSpawnerJustZombie;
	private static double oddsNaturalJustZombie;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC) {
			bakeConfig();
		}
	}

	public static void bakeConfig() {
		oddsSpawnerJustZombie = COMMON.oddsSpawnerJustZombie.get();
		oddsNaturalJustZombie = COMMON.oddsNaturalJustZombie.get();
	}

	public static class Common {
		public DoubleValue getOddsSpawnerJustZombie() {
			return oddsSpawnerJustZombie;
		}

		public DoubleValue getOddsNaturalJustZombie() {
			return oddsNaturalJustZombie;
		}

		public final DoubleValue oddsSpawnerJustZombie;
		public final DoubleValue oddsNaturalJustZombie;

		public Common(ForgeConfigSpec.Builder builder) {
			String baseTrans = Main.MODID + ".config.";
			String sectionTrans;

			sectionTrans = baseTrans + "general.";

			oddsSpawnerJustZombie = builder.comment("Odds Spawner ZV is just a Zombie")
					.translation(Main.MODID + ".config." + "oddsSpawnerJustZombie")
					.defineInRange("oddsSpawnerJustZombie", () -> 96.0, 0.0, 100.0);

			oddsNaturalJustZombie = builder.comment("Odds Natural ZV is just a Zombie")
					.translation(Main.MODID + ".config." + "oddsNaturalJustZombie")
					.defineInRange("oddsNaturalJustZombie", () -> 0.0, 0.0, 100.0);

		}
	}

}
