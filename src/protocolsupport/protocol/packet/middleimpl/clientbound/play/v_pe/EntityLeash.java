package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_pe;

import protocolsupport.protocol.packet.middle.clientbound.play.MiddleEntityLeash;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.utils.types.NetworkEntity;
import protocolsupport.utils.recyclable.RecyclableArrayList;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableEmptyList;

public class EntityLeash extends MiddleEntityLeash {

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		NetworkEntity wentity = cache.getWatchedEntity(entityId);
		if (wentity == null) {
			return RecyclableEmptyList.get();
		}
		RecyclableArrayList<ClientBoundPacketData> packets = RecyclableArrayList.create();
		packets.add(EntityMetadata.createFaux(wentity, cache.getLocale(), connection.getVersion()));
		if (vehicleId == -1) {
			packets.add(EntityStatus.create(wentity, EntityStatus.PE_UNLEASH, connection.getVersion()));
		}
		return packets;
	}

}
