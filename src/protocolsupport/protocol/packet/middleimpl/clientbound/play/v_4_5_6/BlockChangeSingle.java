package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_4_5_6;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.ClientBoundPacket;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleBlockChangeSingle;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.serializer.PositionSerializer;
import protocolsupport.protocol.typeremapper.id.IdRemapper;
import protocolsupport.protocol.typeremapper.legacy.LegacyBlockId;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class BlockChangeSingle extends MiddleBlockChangeSingle {

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		ProtocolVersion version = connection.getVersion();
		ClientBoundPacketData serializer = ClientBoundPacketData.create(ClientBoundPacket.PLAY_BLOCK_CHANGE_SINGLE_ID);
		PositionSerializer.writeLegacyPositionB(serializer, position);
		id = LegacyBlockId.getCombinedId(IdRemapper.BLOCK.getTable(version).getRemap(id));
		serializer.writeShort(LegacyBlockId.getIdFromCombinedId(id));
		serializer.writeByte(LegacyBlockId.getDataFromCombinedId(id));
		return RecyclableSingletonList.create(serializer);
	}

}
