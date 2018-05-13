package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_4_5_6_7;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.ClientBoundPacket;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddlePlayerListSetEntry;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.protocol.storage.netcache.PlayerListCache;
import protocolsupport.utils.Utils;
import protocolsupport.utils.recyclable.RecyclableArrayList;
import protocolsupport.utils.recyclable.RecyclableCollection;

public class PlayerListSetEntry extends MiddlePlayerListSetEntry {

	protected final HashMap<UUID, String> oldNames = new HashMap<>();

	@Override
	public boolean postFromServerRead() {
		PlayerListCache plcache = cache.getPlayerListCache();
		oldNames.clear();
		Arrays.stream(infos).forEach(info -> oldNames.put(info.uuid, plcache.getEntry(info.uuid).getName(cache.getAttributesCache().getLocale())));
		return super.postFromServerRead();
	}

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		ProtocolVersion version = connection.getVersion();
		RecyclableArrayList<ClientBoundPacketData> packets = RecyclableArrayList.create();
		switch (action) {
			case ADD: {
				Arrays.stream(infos).forEach(info -> packets.add(create(version, info.getName(cache.getAttributesCache().getLocale()), true, (short) info.ping)));
				break;
			}
			case DISPLAY_NAME: {
				Arrays.stream(infos).forEach(info -> {
					packets.add(create(version, oldNames.get(info.uuid), false, (short) info.ping));
					packets.add(create(version, info.getName(cache.getAttributesCache().getLocale()), true, (short) info.ping));
				});
				break;
			}
			case REMOVE: {
				Arrays.stream(infos).forEach(info -> packets.add(create(version, oldNames.get(info.uuid), false, (short) info.ping)));
				break;
			}
			case PING: {
				Arrays.stream(infos).forEach(info -> packets.add(create(version, oldNames.get(info.uuid), true, (short) info.ping)));
				break;
			}
			default: {
				break;
			}
		}
		return packets;
	}

	protected static ClientBoundPacketData create(ProtocolVersion version, String name, boolean add, short ping) {
		ClientBoundPacketData serializer = ClientBoundPacketData.create(ClientBoundPacket.PLAY_PLAYER_INFO_ID);
		StringSerializer.writeString(serializer, version, Utils.clampString(name, 16));
		serializer.writeBoolean(add);
		serializer.writeShort(ping);
		return serializer;
	}

}
