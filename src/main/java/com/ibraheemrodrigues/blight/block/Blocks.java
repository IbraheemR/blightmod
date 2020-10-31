package com.ibraheemrodrigues.blight.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

import static com.ibraheemrodrigues.blight.Util.getId;

public class Blocks {

    public static final Block BLIGHT_BLOCK = new BlightBlock(FabricBlockSettings.of(Material.AGGREGATE).hardness(0.7f).ticksRandomly());

    public static void init() {
        Registry.register(Registry.BLOCK, getId("blight"), BLIGHT_BLOCK);
        Registry.register(Registry.ITEM, getId("blight"), new BlockItem(BLIGHT_BLOCK, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
    }
}
