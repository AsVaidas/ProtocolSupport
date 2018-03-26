package protocolsupport.protocol.packet.middleimpl.clientbound.status.v_l;

import java.util.StringJoiner;

import org.bukkit.ChatColor;

import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.ClientBoundPacket;
import protocolsupport.protocol.packet.middle.clientbound.status.MiddleServerInfo;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class ServerInfo extends MiddleServerInfo {

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		ClientBoundPacketData serializer = ClientBoundPacketData.create(ClientBoundPacket.STATUS_SERVER_INFO_ID, null);
		String response = new StringJoiner("§")
		.add(ChatColor.stripColor(ping.getMotd().toLegacyText(cache.getAttributesCache().getLocale())).replace("§", ""))
		.add(String.valueOf(ping.getPlayers().getOnline()))
		.add(String.valueOf(ping.getPlayers().getMax()))
		.toString();
		StringSerializer.writeString(serializer, ProtocolVersion.getOldest(ProtocolType.PC), response);
		return RecyclableSingletonList.create(serializer);
	}

}
