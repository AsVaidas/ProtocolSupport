package protocolsupport.protocol.typeremapper.itemstack.complex.toclient;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.complex.ItemStackComplexRemapper;
import protocolsupport.protocol.utils.types.NetworkItemStack;
import protocolsupport.protocol.utils.types.nbt.NBTCompound;
import protocolsupport.protocol.utils.types.nbt.NBTList;
import protocolsupport.protocol.utils.types.nbt.NBTType;
import protocolsupport.zplatform.ServerPlatform;

public class FireworkToPETagSpecificRemapper implements ItemStackComplexRemapper {

	@Override
	public NetworkItemStack remap(ProtocolVersion version, String locale, NetworkItemStack itemstack) {
		NBTCompound tag = itemstack.getNBT();
		if (tag == null) {
			return itemstack;
		}
		NBTCompound explosion = tag.getTagOfType("Explosion", NBTType.COMPOUND);
		if (explosion != null) {
			tag.setTag("Explosion", remapExplosion(explosion));
		}
		NBTCompound fireworks = tag.getTagOfType("Fireworks", NBTType.COMPOUND);
		if (fireworks != null) {
			NBTList<NBTCompound> explosions = tag.getTagListOfType("Explosions", NBTType.COMPOUND); 
			if (explosions != null) {
				fireworks.setTag("Explosions", remapExplosions(explosions));
			}
		}
		return itemstack;
	}

	private NBTList<NBTCompound> remapExplosions(NBTList<NBTCompound> pcExplosions) {
		NBTList<NBTCompound> peExplosions = new NBTList<>(NBTType.COMPOUND);
		for (int i = 0; i < pcExplosions.size(); i++) {
			peExplosions.addTag(remapExplosion(pcExplosions.getTag(i)));
		}
		return peExplosions;
	}

	private NBTCompound remapExplosion(NBTCompound pcExplosion) {
		NBTCompound peExplosion = new NBTCompound();
		if (pcExplosion.hasKeyOfType("Colors", 		NBTTagType.INT_ARRAY)) { peExplosion.setByteArray("FireworkColor", remapColors(pcExplosion.getIntArray("Colors"))); }
		if (pcExplosion.hasKeyOfType("FadeColors", 	NBTTagType.INT_ARRAY)) { peExplosion.setByteArray("FireworkFade", remapColors(pcExplosion.getIntArray("FadeColors"))); }
		if (pcExplosion.hasKeyOfType("Flicker", 	NBTTagType.BYTE)) 	   { peExplosion.setByte("FireworkFlicker", pcExplosion.getByteNumber("Flicker")); }
		if (pcExplosion.hasKeyOfType("Trail", 		NBTTagType.BYTE))	   { peExplosion.setByte("FireworkTrail", pcExplosion.getByteNumber("Trail")); }
		if (pcExplosion.hasKeyOfType("Type",	 	NBTTagType.BYTE))	   { peExplosion.setByte("FireworkType", pcExplosion.getByteNumber("Type")); }
		return peExplosion;
	}

	private byte[] remapColors(int[] pcColors) {
		byte[] peColors = new byte[pcColors.length];
		for(int i = 0; i < pcColors.length; i++) {
			peColors[i] = remapColor(pcColors[i]);
		}
		return peColors;
	}

	//Fireworks have a custom color system. Since PE only supports the Dye colors, here's a basic switcher for now.
	private byte remapColor(int pcColor) {
		switch(pcColor) {
			case 1973019:				return  0;
			case 11743532:				return  1;
			case 3887386:				return  2;
			case 5320730:				return  3;
			case 2437522:				return  4;
			case 8073150:				return  5;
			case 2651799:				return  6;
			case 11250603:				return  7;
			case 4408131:				return  8;
			case 14188952:				return  9;
			case 4312372:				return 10;
			case 14602026:				return 11;
			case 6719955:				return 12;
			case 12801229:				return 13;
			case 15435844: 				return 14;
			default: case 15790320: 	return 15;
		}
	}

}
