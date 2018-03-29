package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_pe;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleSpawnObject;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.utils.types.networkentity.NetworkEntityItemDataCache;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableEmptyList;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class SpawnObject extends MiddleSpawnObject {

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		ProtocolVersion version = connection.getVersion();
		switch (entity.getType()) {
			case ITEM: {
				((NetworkEntityItemDataCache) entity.getDataCache()).setData(x, y, z, motX / 8000F, motY / 8000F, motZ / 8000F);
				return RecyclableEmptyList.get();
			}
			case ITEM_FRAME: {
				return RecyclableEmptyList.get();
			}
			default: {
				return RecyclableSingletonList.create(SpawnLiving.create(
					version, cache.getAttributesCache().getLocale(),
					entity,
					x, y, z,
					motX / 8.000F, motY / 8000.F, motZ / 8000.F,
					pitch, yaw,
					null //TODO: Add spawnmeta to something like sand.
				));
			}
		}
	}

}
