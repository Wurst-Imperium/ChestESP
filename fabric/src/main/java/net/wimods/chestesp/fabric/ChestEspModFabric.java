package net.wimods.chestesp.fabric;

import net.fabricmc.api.ModInitializer;
import net.wimods.chestesp.ChestEspModInitializer;

public class ChestEspModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ChestEspModInitializer.init();
    }
}
