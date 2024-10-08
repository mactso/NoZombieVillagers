package com.mactso.nozombievillagers;

import com.mactso.nozombievillagers.config.MyConfig;
import com.mactso.nozombievillagers.events.SpawnEventHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("nozombievillagers")
public class Main {

	    public static final String MODID = "nozombievillagers"; 
	    
	    public Main()
	    {
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
//			ModLoadingContext.get().registerExtensionPoint(DisplayTest.class,
//					() -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));	
	    	System.out.println(MODID + ": Registering Mod.");
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
	    }
	    
		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
			MinecraftForge.EVENT_BUS.register(new SpawnEventHandler());
		}  

}
