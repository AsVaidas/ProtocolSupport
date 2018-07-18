package protocolsupport.protocol.utils.spoofedata;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;

import protocolsupport.api.utils.ProfileProperty;

public class PaperSpoofedDataParser implements Function<String, SpoofedData> {

	@SuppressWarnings("serial")
	protected static final Type properties_type = new TypeToken<Collection<ProfileProperty>>() {}.getType();

	@Override
	public SpoofedData apply(String hostname) {
//TODO: uncomment when paper releases
//		if (PlayerHandshakeEvent.getHandlerList().getRegisteredListeners().length != 0) {
//			PlayerHandshakeEvent handshakeEvent = new PlayerHandshakeEvent(hostname, false);
//			Bukkit.getPluginManager().callEvent(handshakeEvent);
//			if (!handshakeEvent.isCancelled()) {
//				if (handshakeEvent.isFailed()) {
//					return new SpoofedData(handshakeEvent.getFailMessage());
//				}
//				return new SpoofedData(
//					handshakeEvent.getServerHostname(),
//					handshakeEvent.getSocketAddressHostname(),
//					handshakeEvent.getUniqueId(),
//					Utils.GSON.fromJson(handshakeEvent.getPropertiesJson(), properties_type)
//				);
//			}
//		}
		return null;
	}

}
