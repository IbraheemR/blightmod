package com.ibraheemrodrigues.blight;

import com.ibraheemrodrigues.blight.block.Blocks;
import net.fabricmc.api.ModInitializer;


public class BlightMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Blocks.init();
    }
}
