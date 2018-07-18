package protocolsupport.protocol.typeremapper.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.serializer.VarNumberSerializer;

public abstract class ChunkTransformer {

	public static enum BlockFormat {
		VARIES_LEGACY, //format for 1.12-
		SHORT, //format for 1.8
		BYTE, //format for 1.7-
	}

	public static ChunkTransformer create(BlockFormat format) {
		switch (format) {
			case VARIES_LEGACY: {
				return new ChunkTransformerVariesLegacy();
			}
			case SHORT: {
				return new ChunkTransformerShort();
			}
			case BYTE: {
				return new ChunkTransformerByte();
			}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}

	protected int columnsCount;
	protected boolean hasSkyLight;
	protected boolean hasBiomeData;
	protected final ChunkSection[] sections = new ChunkSection[16];
	protected final byte[] biomeData = new byte[256];

	public void loadData(byte[] data, int bitmap, boolean hasSkyLight, boolean hasBiomeData) {
		this.columnsCount = Integer.bitCount(bitmap);
		this.hasSkyLight = hasSkyLight;
		this.hasBiomeData = hasBiomeData;
		ByteBuf chunkdata = Unpooled.wrappedBuffer(data);
		for (int i = 0; i < sections.length; i++) {
			if ((bitmap & (1 << i)) != 0) {
				sections[i] = new ChunkSection(chunkdata, hasSkyLight);
			} else {
				sections[i] = null;
			}
		}
		if (hasBiomeData) {
			chunkdata.readBytes(biomeData);
		}
	}

	public abstract byte[] toLegacyData(ProtocolVersion version);

	protected static final int blocksInSection = 16 * 16 * 16;

	protected static class ChunkSection {

		protected static final int globalPaletteBitsPerBlock = 14;
		protected static final int[] globalPaletteData = new int[Short.MAX_VALUE * 2];
		static {
			for (int i = 0; i < globalPaletteData.length; i++) {
				globalPaletteData[i] = i;
			}
		}

		protected final BlockStorageReader blockdata;
		protected final byte[] blocklight = new byte[2048];
		protected final byte[] skylight = new byte[2048];

		public ChunkSection(ByteBuf datastream, boolean hasSkyLight) {
			byte bitsPerBlock = datastream.readByte();
			int[] palette = globalPaletteData;
			if (bitsPerBlock != globalPaletteBitsPerBlock) {
				palette = new int[VarNumberSerializer.readVarInt(datastream)];
				for (int i = 0; i < palette.length; i++) {
					palette[i] = VarNumberSerializer.readVarInt(datastream);
				}
			}
			this.blockdata = new BlockStorageReader(palette, bitsPerBlock, VarNumberSerializer.readVarInt(datastream));
			this.blockdata.readFromStream(datastream);
			datastream.readBytes(blocklight);
			if (hasSkyLight) {
				datastream.readBytes(skylight);
			}
		}

	}

}
