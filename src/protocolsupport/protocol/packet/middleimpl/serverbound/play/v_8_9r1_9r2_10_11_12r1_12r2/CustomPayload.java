package protocolsupport.protocol.packet.middleimpl.serverbound.play.v_8_9r1_9r2_10_11_12r1_12r2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.chat.ChatAPI;
import protocolsupport.protocol.packet.middle.serverbound.play.MiddleCustomPayload;
import protocolsupport.protocol.serializer.MiscSerializer;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.zplatform.ServerPlatform;
import protocolsupport.zplatform.itemstack.NBTTagCompoundWrapper;
import protocolsupport.zplatform.itemstack.NBTTagListWrapper;
import protocolsupport.zplatform.itemstack.NBTTagType;
import protocolsupport.zplatform.itemstack.NetworkItemStack;

public class CustomPayload extends MiddleCustomPayload {

//	protected final ByteBuf newdata = Unpooled.buffer();

	@Override
	public void readFromClientData(ByteBuf clientdata) {
		ProtocolVersion version = connection.getVersion();
		tag = StringSerializer.readString(clientdata, version, 20);
		data = MiscSerializer.readAllBytesWithLimit(clientdata, Short.MAX_VALUE);
//TODO: transform to new packets
//		if (tag.equals("MC|AdvCdm")) {
//			tag = "MC|AdvCmd";
//			data = MiscSerializer.readAllBytes(clientdata);
//		} else if (tag.equals("MC|BSign") || tag.equals("MC|BEdit")) {
//			ItemStackWrapper book = ItemStackSerializer.readItemStack(clientdata, version, cache.getAttributesCache().getLocale(), true);
//			if (!book.isNull()) {
//				book.setType(Material.BOOK_AND_QUILL);
//				if ((version == ProtocolVersion.MINECRAFT_1_8) && tag.equals("MC|BSign")) {
//					remapBookPages(book, cache.getAttributesCache().getLocale());
//				}
//			}
//			ItemStackSerializer.writeItemStack(newdata, ProtocolVersionsHelper.LATEST_PC, cache.getAttributesCache().getLocale(), book, false);
//			data = MiscSerializer.readAllBytes(newdata);
//		} else {
//			data = MiscSerializer.readAllBytes(clientdata);
//		}
	}

	private static void remapBookPages(NetworkItemStack itemstack, String locale) {
		NBTTagCompoundWrapper tag = itemstack.getTag();
		if (!tag.isNull()) {
			if (tag.hasKeyOfType("pages", NBTTagType.LIST)) {
				NBTTagListWrapper pages = tag.getList("pages", NBTTagType.STRING);
				NBTTagListWrapper newPages = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
				for (int i = 0; i < pages.size(); i++) {
					newPages.addString(ChatAPI.fromJSON(pages.getString(i)).toLegacyText(locale));
				}
				tag.setList("pages", newPages);
			}
			itemstack.setTag(tag);
		}
	}

}
