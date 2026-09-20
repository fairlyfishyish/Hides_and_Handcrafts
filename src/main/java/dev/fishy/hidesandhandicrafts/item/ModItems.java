package dev.fishy.hidesandhandicrafts.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Function;

public class ModItems {

    public static final ResourceKey<Item> FIELD_DRESSING_TOOL_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath("hides-and-handcrafts", "field_dressing_tool")
            );
    public static final ResourceKey<Item> COW_HIDE_KEY =
            ResourceKey.create(
                    Registries.ITEM,
                    Identifier.fromNamespaceAndPath("hides-and-handcrafts", "cow_hide")
            );

    public static final Item COW_HIDE =
            register(COW_HIDE_KEY, Item::new, new Item.Properties());
    public static final Item FIELD_DRESSING_TOOL =
            register(
                    FIELD_DRESSING_TOOL_KEY,
                    Item::new,
                    new Item.Properties().attributes(
                            ItemAttributeModifiers.builder()
                                    .add(
                                            Attributes.ATTACK_DAMAGE,
                                            new AttributeModifier(
                                                    Item.BASE_ATTACK_DAMAGE_ID,
                                                    6.0 - 1.0, // target 6.0, minus base 1.0 unarmed damage
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.MAINHAND
                                    )
                                    .add(
                                            Attributes.ATTACK_SPEED,
                                            new AttributeModifier(
                                                    Item.BASE_ATTACK_SPEED_ID,
                                                    1.6 - 4.0, // target 1.6, minus base 4.0 unarmed speed
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.MAINHAND
                                    )
                                    .build()
                    )
            );

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