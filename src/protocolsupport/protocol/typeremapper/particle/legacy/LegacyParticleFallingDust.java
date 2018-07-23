package protocolsupport.protocol.typeremapper.particle.legacy;

import io.netty.buffer.ByteBuf;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.serializer.VarNumberSerializer;
import protocolsupport.protocol.typeremapper.block.LegacyBlockData;
import protocolsupport.protocol.typeremapper.block.LegacyBlockId;

public class LegacyParticleFallingDust extends LegacyParticle {

	protected int blockstate;

	public LegacyParticleFallingDust(int id, String name, int blockstate) {
		super(id, name);
		this.blockstate = blockstate;
	}

	@Override
	public void remap(ProtocolVersion version, String locale) {
		blockstate = LegacyBlockId.getLegacyObjDataFromLegacyBlockState(LegacyBlockId.getLegacyCombinedId(LegacyBlockData.REGISTRY.getTable(version).getRemap(blockstate)));
	}

	@Override
	public void writeData(ByteBuf buf) {
		VarNumberSerializer.writeVarInt(buf, blockstate);
	}

	@Override
	public int getFirstParameter() {
		return blockstate;
	}

}
