package protocolsupport.protocol.typeremapper.itemstack.toclient;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.EntityType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.ItemStackSpecificRemapper;
import protocolsupport.protocol.typeremapper.legacy.LegacyMonsterEgg;
import protocolsupport.protocol.typeremapper.pe.PEDataValues;
import protocolsupport.protocol.utils.minecraftdata.MinecraftData;
import protocolsupport.protocol.utils.types.NetworkEntityType;
import protocolsupport.zplatform.itemstack.ItemStackWrapper;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NBTTagType;

public class MonsterEggToPEIdSpecificRemapper implements ItemStackSpecificRemapper {

	@Override
	public ItemStackWrapper remap(ProtocolVersion version, String locale, ItemStackWrapper itemstack) {
		NBTTagCompoundWrapper tag = itemstack.getTag();
		if (tag.isNull()) {
			return itemstack;
		}
		String id = tag.getCompound("EntityTag").getString("id");

		if (StringUtils.isEmpty(id)) {
			return itemstack;
		}

		EntityType entityType = EntityType.fromName(MinecraftData.removeNamespacePrefix(id));
		NetworkEntityType networkEntityType = NetworkEntityType.getMobByTypeId(entityType.getTypeId());
		itemstack.setData(PEDataValues.getLivingEntityTypeId(networkEntityType));
		return itemstack;
	}

}
