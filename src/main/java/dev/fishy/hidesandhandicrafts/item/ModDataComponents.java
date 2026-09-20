package dev.fishy.hidesandhandicrafts.item;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class ModDataComponents {

    public static final DataComponentType<SatchelContents> SATCHEL_CONTENTS =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath("hides-and-handcrafts", "satchel_contents"),
                    DataComponentType.<SatchelContents>builder()
                            .persistent(SatchelContents.CODEC)
                            .networkSynchronized(SatchelContents.STREAM_CODEC)
                            .build()
            );

    public static void initialize() {
    }
}