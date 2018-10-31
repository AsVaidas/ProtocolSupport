package protocolsupport.protocol.packet.middle.clientbound.play;

import io.netty.buffer.ByteBuf;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.ArraySerializer;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.utils.types.Position;
import protocolsupport.utils.Utils;

public abstract class MiddleBlockChangeMulti extends ClientBoundMiddlePacket {

	public MiddleBlockChangeMulti(ConnectionImpl connection) {
		super(connection);
	}

	protected int chunkX;
	protected int chunkZ;
	protected Record[] records;

	@Override
	public void readFromServerData(ByteBuf serverdata) {
		chunkX = serverdata.readInt();
		chunkZ = serverdata.readInt();
		records = ArraySerializer.readVarIntTArray(
			serverdata, 
			Record.class, 
			from -> {
				short horizCoord = from.readUnsignedByte();
				short yCoord = from.readUnsignedByte();
				int id = VarNumberSerializer.readVarInt(from);
				return new Record(horizCoord, yCoord, id);
			}
		);
	}

	public Position fromLocalPosition(Record record) {
		return new Position((
				(record.horizCoord >> 4) & 0xF) + (chunkX * 16), 
				record.yCoord,
				(record.horizCoord & 0xF) + (chunkZ * 16)
			);
	}

	public static class Record {
		public final short horizCoord;
		public final short yCoord;
		public final int id;
		public Record(short horizCoord, short yCoord, int id) {
			this.horizCoord = horizCoord;
			this.yCoord = yCoord;
			this.id = id;
		}
		@Override
		public String toString() {
			return Utils.toStringAllFields(this);
		}
	}

}
