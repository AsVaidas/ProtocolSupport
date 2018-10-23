package protocolsupport.protocol.packet.middleimpl.serverbound.play.v_pe;

import io.netty.buffer.ByteBuf;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.ServerBoundPacketData;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableEmptyList;

public class BookEdit extends ServerBoundMiddlePacket {

	public BookEdit(ConnectionImpl connection) {
		super(connection);
	}

	protected byte type;
	protected byte slot;
	protected byte pagenum;
	protected byte pagenum2;
	protected String text;
	protected String photo;
	protected String title;
	protected String author;

	@Override
	public void readFromClientData(ByteBuf clientdata) {
		ProtocolVersion version = connection.getVersion();
		type = clientdata.readByte();
		slot = clientdata.readByte();
		switch(type) {
			case TYPE_REPLACE: {
				pagenum = clientdata.readByte();
				text = StringSerializer.readString(clientdata, version);
				photo = StringSerializer.readString(clientdata, version);
				break;
			}
			case TYPE_ADD: {
				pagenum = clientdata.readByte();
				text = StringSerializer.readString(clientdata, version);
				photo = StringSerializer.readString(clientdata, version);
				break;
			}
			case TYPE_DELETE: {
				pagenum = clientdata.readByte();
				break;
			}
			case TYPE_SWAP: {
				pagenum = clientdata.readByte();
				pagenum2 = clientdata.readByte();
				break;
			}
			case TYPE_SIGN: {
				title = StringSerializer.readString(clientdata, version);
				author = StringSerializer.readString(clientdata, version);
				break;
			}
		}
	}

	private static final int TYPE_REPLACE = 0;
	private static final int TYPE_ADD = 1;
	private static final int TYPE_DELETE = 2;
	private static final int TYPE_SWAP = 3;
	private static final int TYPE_SIGN = 4;

	@Override
	public RecyclableCollection<ServerBoundPacketData> toNative() {
		//TODO rewrite NBT
//		NetworkItemStack bookItem = cache.getPEInventoryCache().getItemInHand();
//		if (!bookItem.isNull() && MaterialAPI.getItemByNetworkId(bookItem.getTypeId()) == Material.WRITABLE_BOOK) {
//			NBTCompund bookNBT = bookItem.getNBT() != null && !bookItem.getNBT().isNull() ? bookItem.getNBT() : ServerPlatform.get().getWrapperFactory().createEmptyNBTCompound();
//			NBTList pages = bookNBT.hasKeyOfType("pages", NBTTagType.LIST) ? bookNBT.getList("pages"): ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
//			int maxpage = ((pagenum + 1) > pages.size()) ? (pagenum + 1) : (pages.size());
//			switch(type) {
//				case TYPE_REPLACE: {
//					//Cut the crap. No text changed? No packet.
//					if ((pages.isEmpty() && text == "") || (pages.getString(pagenum).equals(text))) {
//						return RecyclableEmptyList.get();
//					}
//					//We can't set the text at an index, so we have to reiterate and construct a new pages list.
//					NBTTagListWrapper newpages = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
//					for (int i = 0; i < maxpage; i++) {
//						if (i == pagenum) {
//							newpages.addString(text);
//						} else {
//							if (i >= pages.size()) {
//								newpages.addString("");
//							} else {
//								newpages.addString(pages.getString(i));
//							}
//						}
//					}
//					bookNBT.setList("pages", newpages);
//					bookItem.setNBT(bookNBT);
//					return RecyclableSingletonList.create(editBook(bookItem));
//				}
//				case TYPE_ADD: {
//					//Adding a page, means shifting too all the other pages, unless the page isn't there yet.
//					NBTTagListWrapper newpages = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
//					for (int i = 0; i < maxpage; i++) {
//						if (i == pagenum) {
//							newpages.addString(text);
//						} else if (i >= pages.size()) {
//							newpages.addString("");
//						} if (i < pages.size()) {
//							newpages.addString(pages.getString(i));
//						}
//					}
//					bookNBT.setList("pages", newpages);
//					bookItem.setNBT(bookNBT);
//					return RecyclableSingletonList.create(editBook(bookItem));
//				}
//				case TYPE_DELETE: {
//					//Deleting a page means sending everything except that page.
//					NBTTagListWrapper newpages = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
//					for (int i = 0; i < pages.size(); i++) {
//						if (i != pagenum) {
//							newpages.addString(pages.getString(i));
//						}
//					}
//					bookNBT.setList("pages", newpages);
//					bookItem.setNBT(bookNBT);
//					return RecyclableSingletonList.create(editBook(bookItem));
//				}
//				case TYPE_SWAP: {
//					//Swaping a page means sending everything, but two pages are swapped.
//					NBTTagListWrapper newpages = ServerPlatform.get().getWrapperFactory().createEmptyNBTList();
//					for (int i = 0; i < pages.size(); i++) {
//						if (i == pagenum) {
//							newpages.addString(pages.getString(pagenum2));
//						} else if (i == pagenum2) {
//							newpages.addString(pages.getString(pagenum));
//						} else {
//							newpages.addString(pages.getString(i));
//						}
//					}
//					bookNBT.setList("pages", newpages);
//					bookItem.setNBT(bookNBT);
//					return RecyclableSingletonList.create(editBook(bookItem));
//				}
//				case TYPE_SIGN: {
//					bookNBT.setString("author", author);
//					bookNBT.setString("title", title);
//					bookNBT.setList("pages", pages);
//					bookItem.setNBT(bookNBT);
//					return RecyclableSingletonList.create(signBook(bookItem));
//				}
//			}
//		}
		return RecyclableEmptyList.get();
	}
	
//	private static ServerBoundPacketData editBook(NetworkItemStack book) {
//		ByteBuf payload = Unpooled.buffer();
//		ItemStackSerializer.writeItemStack(payload, ProtocolVersionsHelper.LATEST_PC, I18NData.DEFAULT_LOCALE, book, false);
//		return MiddleCustomPayload.create("minecraft:BEdit", MiscSerializer.readAllBytes(payload));
//	}
//	
//	private static ServerBoundPacketData signBook(NetworkItemStack book) {
//		ByteBuf payload = Unpooled.buffer();
//		ItemStackSerializer.writeItemStack(payload, ProtocolVersionsHelper.LATEST_PC, I18NData.DEFAULT_LOCALE, book, false);
//		return MiddleCustomPayload.create("minecraft:BSign", MiscSerializer.readAllBytes(payload));
//	}

}
