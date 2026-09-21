package dev.fishy.hidesandhandicrafts;

import dev.fishy.hidesandhandicrafts.item.ModDataComponents;
import dev.fishy.hidesandhandicrafts.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.entity.EntityTypes;
import java.util.Map;

public class HidesAndHandcrafts implements ModInitializer {
	public static final String MOD_ID = "hides-and-handcrafts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Map<EntityType<?>, Item> ANIMAL_TO_HIDE = Map.of(
			EntityTypes.COW, ModItems.COW_HIDE,
			EntityTypes.PIG, ModItems.PIG_HIDE
	);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModDataComponents.initialize();

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, killed, source) -> {
			if (killer instanceof Player player && player.getMainHandItem().is(ModItems.FIELD_DRESSING_TOOL)) {
				Item hide = ANIMAL_TO_HIDE.get(killed.getType());
				if (hide != null) {
					world.addFreshEntity(new ItemEntity(world, killed.getX(), killed.getY(), killed.getZ(), new ItemStack(hide)));
				}
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}