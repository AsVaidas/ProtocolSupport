package protocolsupport.protocol.packet.middleimpl.serverbound.play.v_pe;

import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.Validate;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.middle.serverbound.play.MiddleChat;
import protocolsupport.protocol.serializer.StringSerializer;

import java.text.MessageFormat;

public class Chat extends MiddleChat {

	private static final int validChatType = 1;

	@Override
	public void readFromClientData(ByteBuf clientdata) {
		ProtocolVersion version = connection.getVersion();
		int type = clientdata.readUnsignedByte();
		Validate.isTrue(type == validChatType, MessageFormat.format("Unexcepted serverbound chat type, expected {0}, but received {1}", validChatType, type));
		clientdata.readUnsignedByte(); //skip unknown byte (seems to be always 0)
		StringSerializer.readString(clientdata, version); //skip sender
		message = StringSerializer.readString(clientdata, version);
	}

}