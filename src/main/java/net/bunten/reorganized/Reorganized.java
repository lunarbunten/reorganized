package net.bunten.reorganized;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reorganized implements ModInitializer {

    public static final String MOD_ID = "reorganized";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " IS LOADED BIG MOTHER FUCKER!");
    }
}