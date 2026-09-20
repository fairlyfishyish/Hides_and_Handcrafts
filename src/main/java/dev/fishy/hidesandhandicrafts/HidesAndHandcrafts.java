package dev.fishy.hidesandhandicrafts;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.resources.Identifier;

import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HidesAndHandcrafts implements ModInitializer {
	public static final String MOD_ID = "hides-and-handcrafts";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killed, source) -> {
			if (killer instanceof Player player && killed instanceof Cow && player.getMainHandItem().is(Items.STICK)) {
				world.addFreshEntity(new ItemEntity(world, killed.getX(), killed.getY(), killed.getZ(), new ItemStack(Items.SPONGE)));
			}
		});
	}
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
