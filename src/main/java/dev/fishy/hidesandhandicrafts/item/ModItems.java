package dev.fishy.hidesandhandicrafts.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static final ResourceKey<Item> FIELD_DRESSING_TOOL_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath("hides-and-handcrafts", "field_dressing_tool")
            );

    public static final Item FIELD_DRESSING_TOOL =
            register(FIELD_DRESSING_TOOL_KEY, Item::new, new Item.Properties());

    private static Item register(
            ResourceKey<Item> key,
            Function<Item.Properties, Item> factory,
            Item.Properties properties
    ) {
        Item item = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
    }
}