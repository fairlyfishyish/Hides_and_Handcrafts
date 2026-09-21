package dev.fishy.hidesandhandicrafts.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final ResourceKey<CreativeModeTab> HIDES_AND_HANDCRAFTS_KEY =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    Identifier.fromNamespaceAndPath("hides-and-handcrafts", "hides_and_handcrafts")
            );

    public static final CreativeModeTab HIDES_AND_HANDCRAFTS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            HIDES_AND_HANDCRAFTS_KEY,
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.hides-and-handcrafts.hides_and_handcrafts"))
                    .icon(() -> new ItemStack(ModItems.FIELD_DRESSING_TOOL))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.FIELD_DRESSING_TOOL);
                        output.accept(ModItems.COW_HIDE);
                        output.accept(ModItems.PIG_HIDE);
                        output.accept(ModItems.TANNED_HIDE);
                        output.accept(ModItems.SATCHEL);
                        output.accept(ModItems.COW_SATCHEL);
                    })
                    .build()
    );

    public static void initialize() {
    }
}