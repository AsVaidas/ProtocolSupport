package protocolsupport.protocol.typeremapper.chunk;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.block.LegacyBlockData;
import protocolsupport.protocol.typeremapper.block.PreFlatteningBlockIdData;
import protocolsupport.protocol.typeremapper.utils.RemappingTable.ArrayBasedIdRemappingTable;

public class ChunkTransformerByte extends ChunkTransformer {

	@Override
	public byte[] toLegacyData(ProtocolVersion version) {
		ArrayBasedIdRemappingTable table = LegacyBlockData.REGISTRY.getTable(version);
		byte[] data = new byte[((hasSkyLight ? 10240 : 8192) * columnsCount) + 256];
		int blockIdIndex = 0;
		int blockDataIndex = 4096 * columnsCount;
		int blockLightIndex = 6144 * columnsCount;
		int skyLightIndex = 8192 * columnsCount;
		for (int i = 0; i < sections.length; i++) {
			ChunkSection section = sections[i];
			if (section != null) {
				BlockStorageReader storage = section.blockdata;
				int blockdataacc = 0;
				for (int block = 0; block < blocksInSection; block++) {
					int blockstate = PreFlatteningBlockIdData.getLegacyCombinedId(table.getRemap(storage.getBlockState(block)));
					data[blockIdIndex + block] = (byte) PreFlatteningBlockIdData.getIdFromLegacyCombinedId(blockstate);
					byte blockdata = (byte) PreFlatteningBlockIdData.getDataFromLegacyCombinedId(blockstate);
					if ((block & 1) == 0) {
						blockdataacc = blockdata;
					} else {
						blockdataacc |= (blockdata << 4);
						data[(block >> 1) + blockDataIndex] = (byte) blockdataacc;
					}
				}
				blockIdIndex += 4096;
				blockDataIndex += 2048;
				System.arraycopy(section.blocklight, 0, data, blockLightIndex, 2048);
				blockLightIndex += 2048;
				if (hasSkyLight) {
					System.arraycopy(section.skylight, 0, data, skyLightIndex, 2048);
					skyLightIndex += 2048;
				}
			}
		}
		if (hasBiomeData) {
			for (int i = 0; i < biomeData.length; i++) {
				data[skyLightIndex + i] = (byte) biomeData[i];
			}
		}
		return data;
	}

}
