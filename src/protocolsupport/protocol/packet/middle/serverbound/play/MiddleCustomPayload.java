package protocolsupport.protocol.packet.middle.serverbound.play;

import io.netty.buffer.ByteBuf;
import protocolsupport.protocol.packet.ServerBoundPacket;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.ServerBoundPacketData;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.protocol.utils.ProtocolVersionsHelper;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;


public abstract class MiddleCustomPayload extends ServerBoundMiddlePacket {

	protected String tag;
	protected byte[] data;

	@Override
	public RecyclableCollection<ServerBoundPacketData> toNative() {
		return RecyclableSingletonList.create(create(tag, data));
	}

	public static ServerBoundPacketData create(String tag, byte[] data) {
		ServerBoundPacketData serializer = ServerBoundPacketData.create(ServerBoundPacket.PLAY_CUSTOM_PAYLOAD);
		StringSerializer.writeString(serializer, ProtocolVersionsHelper.LATEST_PC, tag);
		serializer.writeBytes(data);
		return serializer;
	}

	public static ServerBoundPacketData create(String tag, ByteBuf data) {
		ServerBoundPacketData serializer = ServerBoundPacketData.create(ServerBoundPacket.PLAY_CUSTOM_PAYLOAD);
		StringSerializer.writeString(serializer, ProtocolVersionsHelper.LATEST_PC, tag);
		serializer.writeBytes(data);
		return serializer;
	}

}
