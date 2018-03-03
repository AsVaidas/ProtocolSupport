package protocolsupport.protocol.pipeline.version.util.encoder;

import io.netty.buffer.ByteBuf;
import protocolsupport.api.Connection;
import protocolsupport.protocol.storage.netcache.NetworkDataCache;

public abstract class AbstractLegacyPacketEncoder extends AbstractPacketEncoder {

	public AbstractLegacyPacketEncoder(Connection connection, NetworkDataCache storage) {
		super(connection, storage);
	}

	@Override
	protected void writePacketId(ByteBuf to, int packetId) {
		to.writeByte(packetId);
	}

}
