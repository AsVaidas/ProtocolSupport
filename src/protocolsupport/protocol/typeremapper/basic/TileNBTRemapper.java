package protocolsupport.protocol.typeremapper.basic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.complex.toclient.PlayerHeadToLegacyOwnerComplexRemapper;
import protocolsupport.protocol.typeremapper.legacy.LegacyEntityId;
import protocolsupport.protocol.utils.ProtocolVersionsHelper;
import protocolsupport.protocol.utils.networkentity.NetworkEntityType;
import protocolsupport.protocol.utils.types.Position;
import protocolsupport.protocol.utils.types.TileEntityType;
import protocolsupport.protocol.utils.types.nbt.NBTCompound;
import protocolsupport.protocol.utils.types.nbt.NBTString;
import protocolsupport.protocol.utils.types.nbt.NBTType;

public class TileNBTRemapper {

	protected static final String tileEntityTypeKey = "id";

	protected static final EnumMap<TileEntityType, String> newToOldType = new EnumMap<>(TileEntityType.class);
	static {
		newToOldType.put(TileEntityType.MOB_SPAWNER, "MobSpawner");
		newToOldType.put(TileEntityType.COMMAND_BLOCK, "Control");
		newToOldType.put(TileEntityType.BEACON, "Beacon");
		newToOldType.put(TileEntityType.SKULL, "Skull");
		newToOldType.put(TileEntityType.BANNER, "Banner");
		newToOldType.put(TileEntityType.STRUCTURE, "Structure");
		newToOldType.put(TileEntityType.END_GATEWAY, "Airportal");
		newToOldType.put(TileEntityType.SIGN, "Sign");
	}

	//TODO make version a main key, so remapping table can be extracted by middle packets
	protected static final EnumMap<TileEntityType, EnumMap<ProtocolVersion, List<BiFunction<ProtocolVersion, NBTCompound, NBTCompound>>>> registry = new EnumMap<>(TileEntityType.class);

	protected static void register(TileEntityType type, BiFunction<ProtocolVersion, NBTCompound, NBTCompound> transformer, ProtocolVersion... versions) {
		EnumMap<ProtocolVersion, List<BiFunction<ProtocolVersion, NBTCompound, NBTCompound>>> map = registry.computeIfAbsent(type, k -> new EnumMap<>(ProtocolVersion.class));
		for (ProtocolVersion version : versions) {
			map.computeIfAbsent(version, k -> new ArrayList<>()).add(transformer);
		}
	}

	static {
		for (TileEntityType type : TileEntityType.values()) {
			register(
				type,
				(version, input) -> {
					input.setTag(tileEntityTypeKey, new NBTString(newToOldType.getOrDefault(type, type.getRegistryId())));
					return input;
				},
				ProtocolVersionsHelper.BEFORE_1_11
			);
		}
		register(
			TileEntityType.MOB_SPAWNER,
			(version, input) -> {
				if (input.getTagOfType("SpawnData", NBTType.COMPOUND) == null) {
					NBTCompound spawndata = new NBTCompound();
					spawndata.setTag("id", new NBTString(NetworkEntityType.PIG.getKey()));
					input.setTag("SpawnData", spawndata);
				}
				return input;
			},
			ProtocolVersionsHelper.ALL_PC
		);
		register(
			TileEntityType.MOB_SPAWNER,
			(version, input) -> {
				NBTCompound spawndata = input.getTagOfType("SpawnData", NBTType.COMPOUND);
				if (spawndata != null) {
					NetworkEntityType type = NetworkEntityType.getByRegistrySTypeId(NBTString.getValueOrNull(spawndata.getTagOfType("id", NBTType.STRING)));
					if (type != NetworkEntityType.NONE) {
						spawndata.setTag("id", new NBTString(LegacyEntityId.getLegacyName(type)));
					}
				}
				return input;
			},
			ProtocolVersionsHelper.BEFORE_1_11
		);
		register(
			TileEntityType.MOB_SPAWNER,
			(version, input) -> {
				if (input.getTagOfType("SpawnData", NBTType.COMPOUND) != null) {
					input.removeTag("SpawnPotentials");
					input.removeTag("SpawnData");
				}
				return input;
			},
			ProtocolVersionsHelper.BEFORE_1_9
		);
		register(
			TileEntityType.SKULL,
			(version, input) -> {
				//TODO
				return input;
			},
			ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_8)
		);
		register(
			TileEntityType.SKULL,
			(version, input) -> {
				PlayerHeadToLegacyOwnerComplexRemapper.remap(input, "Owner", "ExtraType");
				return input;
			},
			ProtocolVersion.getAllBeforeI(ProtocolVersion.MINECRAFT_1_7_5)
		);
	}

	public static String getTileType(NBTCompound tag) {
		return NBTString.getValueOrNull(tag.getTagOfType(tileEntityTypeKey, NBTType.STRING));
	}

	public static Position getPosition(NBTCompound tag) {
		return new Position(tag.getNumberTag("x").getAsInt(), tag.getNumberTag("y").getAsInt(), tag.getNumberTag("z").getAsInt());
	}

	public static String[] getSignLines(NBTCompound tag) {
		String[] lines = new String[4];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = NBTString.getValueOrDefault(tag.getTagOfType("Text" + (i + 1), NBTType.STRING), "");
		}
		return lines;
	}

	public static NBTCompound remap(ProtocolVersion version, NBTCompound compound) {
		EnumMap<ProtocolVersion, List<BiFunction<ProtocolVersion, NBTCompound, NBTCompound>>> map = registry.get(TileEntityType.getByRegistryId(getTileType(compound)));
		if (map != null) {
			List<BiFunction<ProtocolVersion, NBTCompound, NBTCompound>> transformers = map.get(version);
			if (transformers != null) {
				for (BiFunction<ProtocolVersion, NBTCompound, NBTCompound> transformer : transformers) {
					compound = transformer.apply(version, compound);
				}
				return compound;
			}
		}
		return compound;
	}

}
