package protocolsupport.protocol.typeremapper.itemstack.complex.toclient;

import org.bukkit.enchantments.Enchantment;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.complex.ItemStackNBTComplexRemapper;
import protocolsupport.protocol.typeremapper.legacy.LegacyEnchantmentId;
import protocolsupport.protocol.utils.CommonNBT;
import protocolsupport.protocol.utils.NamespacedKeyUtils;
import protocolsupport.zplatform.ServerPlatform;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NBTTagListWrapper;
import protocolsupport.zplatform.itemstack.NBTTagType;
import protocolsupport.zplatform.itemstack.NetworkItemStack;

public class EnchantToLegacyIdComplexRemapper extends ItemStackNBTComplexRemapper {

	@Override
	public NBTTagCompoundWrapper remapTag(ProtocolVersion version, String locale, NetworkItemStack itemstack, NBTTagCompoundWrapper tag) {
		if (tag.hasKeyOfType(CommonNBT.MODERN_ENCHANTMENTS, NBTTagType.LIST)) {
			tag.setList(CommonNBT.LEGACY_ENCHANTMENTS, remapEnchantList(tag.getList(CommonNBT.MODERN_ENCHANTMENTS, NBTTagType.COMPOUND)));
			tag.remove(CommonNBT.MODERN_ENCHANTMENTS);
		}
		if (tag.hasKeyOfType(CommonNBT.BOOK_ENCHANTMENTS, NBTTagType.LIST)) {
			tag.setList(CommonNBT.BOOK_ENCHANTMENTS, remapEnchantList(tag.getList(CommonNBT.BOOK_ENCHANTMENTS, NBTTagType.COMPOUND)));
		}
		return tag;
	}

	protected NBTTagListWrapper remapEnchantList(NBTTagListWrapper oldList) {
		NBTTagListWrapper newList = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
		for (int i = 0; i < oldList.size(); i++) {
			NBTTagCompoundWrapper enchData = oldList.getCompound(i);
			Enchantment ench = Enchantment.getByKey(NamespacedKeyUtils.fromString(enchData.getString("id")));
			if (ench != null) {
				enchData.setShort("id", LegacyEnchantmentId.getId(ench));
				newList.addCompound(enchData);
			}
		}
		return newList;
	}

}
