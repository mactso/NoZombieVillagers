package com.mactso.nozombievillagers.modloader.main;

import com.mactso.nozombievillagers.modloader.config.MyConfig;
import com.mactso.nozombievillagers.modloader.events.SpawnEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("nozombievillagers")
public class Main {

	    public static final String MODID = "nozombievillagers"; 
	    
	    public Main(FMLJavaModLoadingContext context)
	    {
	    	System.out.println(MODID + ": Registering Mod.");
	    	context.getModEventBus().register(this);
			context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
	    }
	    
		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
			MinecraftForge.EVENT_BUS.register(new SpawnEventHandler());
		}  

}
