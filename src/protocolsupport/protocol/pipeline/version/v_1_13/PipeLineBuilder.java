package protocolsupport.protocol.pipeline.version.v_1_13;

import io.netty.channel.Channel;
import protocolsupport.api.Connection;
import protocolsupport.protocol.pipeline.version.util.builder.AbstractVarIntFramingPipeLineBuilder;

public class PipeLineBuilder extends AbstractVarIntFramingPipeLineBuilder {

	@Override
	public void buildCodec(Channel channel, Connection connection) {
		//TODO: implement after implementing encoder and decoder
	}

}
