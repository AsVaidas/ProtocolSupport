package protocolsupport.protocol.typeremapper.itemstack.complex;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NetworkItemStack;

public abstract class ItemStackNBTComplexRemapper implements ItemStackComplexRemapper {

	@Override
	public NetworkItemStack remap(ProtocolVersion version, String locale, NetworkItemStack itemstack) {
		NBTTagCompoundWrapper tag = itemstack.getNBT();
		if (!tag.isNull()) {
			itemstack.setNBT(remapTag(version, locale, itemstack, tag));
		}
		return itemstack;
	}

	public abstract NBTTagCompoundWrapper remapTag(ProtocolVersion version, String locale, NetworkItemStack itemstack, NBTTagCompoundWrapper tag);

}
