package protocolsupport.protocol.packet.middleimpl.serverbound.handshake.v_pe;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.packet.ServerBoundPacket;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.ServerBoundPacketData;
import protocolsupport.protocol.serializer.ArraySerializer;
import protocolsupport.protocol.serializer.MiscSerializer;
import protocolsupport.protocol.serializer.StringSerializer;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.utils.Utils;
import protocolsupport.utils.recyclable.RecyclableArrayList;
import protocolsupport.utils.recyclable.RecyclableCollection;

public class ClientLogin extends ServerBoundMiddlePacket {

	protected String username;
	protected String host;
	protected int port;

	@Override
	public RecyclableCollection<ServerBoundPacketData> toNative() {
		RecyclableArrayList<ServerBoundPacketData> packets = RecyclableArrayList.create();
		ServerBoundPacketData hsscreator = ServerBoundPacketData.create(ServerBoundPacket.HANDSHAKE_START);
		VarNumberSerializer.writeVarInt(hsscreator, ProtocolVersion.getLatest(ProtocolType.PC).getId());
		StringSerializer.writeString(hsscreator, ProtocolVersion.getLatest(ProtocolType.PC), host);
		hsscreator.writeShort(port);
		VarNumberSerializer.writeVarInt(hsscreator, 2);
		packets.add(hsscreator);
		ServerBoundPacketData lscreator = ServerBoundPacketData.create(ServerBoundPacket.LOGIN_START);
		StringSerializer.writeString(lscreator, ProtocolVersion.getLatest(ProtocolType.PC), username);
		packets.add(lscreator);
		return packets;
	}

	@Override
	public void readFromClientData(ByteBuf clientdata) {
		clientdata.readInt(); //protocol version
		ByteBuf logindata = Unpooled.wrappedBuffer(ArraySerializer.readByteArray(clientdata, connection.getVersion()));
		@SuppressWarnings("serial")
		Map<String, List<String>> maindata = Utils.GSON.fromJson(
			new InputStreamReader(new ByteBufInputStream(logindata, logindata.readIntLE())),
			new TypeToken<Map<String, List<String>>>() {}.getType()
		);
		UUID clientUUID = null;
		for (String c : maindata.get("chain")) {
			JsonObject chainMap = decodeToken(c);
			if ((chainMap != null) && chainMap.has("extraData")) {
				JsonObject extra = chainMap.get("extraData").getAsJsonObject();
				if (extra.has("displayName")) {
					username = extra.get("displayName").getAsString();
				}
				if (extra.has("identity")) {
					clientUUID = UUID.fromString(extra.get("identity").getAsString());
				}
				if (extra.has("locale")) {
					cache.setLocale(extra.get("locale").getAsString());
				}
			}
		}
		if (clientUUID == null) {
			throw new DecoderException("Client uuid (identity) is missing");
		}
		cache.setClientUUID(clientUUID);
		String[] additionaldata = new String(MiscSerializer.readBytes(logindata, logindata.readIntLE())).split("[.]");
		if (additionaldata.length >= 2) {
			@SuppressWarnings("serial")
			Map<String, String> clientinfo = Utils.GSON.fromJson(
				new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(additionaldata[1]))),
				new TypeToken<Map<String, String>>() {}.getType()
			);
			String rserveraddress = clientinfo.get("ServerAddress");
			if (rserveraddress != null) {
				String[] rserveraddresssplit = rserveraddress.split("[:]");
				host = rserveraddresssplit[0];
				port = Integer.parseInt(rserveraddresssplit[1]);
			}
		}
	}

	private JsonObject decodeToken(String token) {
		String[] base = token.split("\\.");
		if (base.length < 2) {
			return null;
		}
		return Utils.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder().decode(base[1]))), JsonObject.class);
	}

}
