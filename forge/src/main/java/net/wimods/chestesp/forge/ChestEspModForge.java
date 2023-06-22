package net.wimods.chestesp.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.wimods.chestesp.ChestEspMod;
import net.wimods.chestesp.ChestEspModInitializer;

@Mod(ChestEspMod.MOD_ID)
public class ChestEspModForge {
    public ChestEspModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ChestEspMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ChestEspModInitializer.init();
    }
}
