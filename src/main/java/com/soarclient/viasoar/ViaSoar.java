package com.soarclient.viasoar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.utils.file.FileLocation;
import com.soarclient.viasoar.api.ProtocolVLInjector;
import com.soarclient.viasoar.api.ProtocolVLLegacyPipeline;
import com.soarclient.viasoar.api.ProtocolVLLoader;
import com.soarclient.viasoar.api.VSNetworkManager;
import com.soarclient.viasoar.api.VSPlatform;
import com.soarclient.viasoar.gui.AsyncVersionSlider;
import com.viaversion.vialoader.ViaLoader;
import com.viaversion.vialoader.impl.platform.ViaBackwardsPlatformImpl;
import com.viaversion.vialoader.impl.platform.ViaRewindPlatformImpl;
import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.vialoader.netty.CompressionReorderEvent;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;

public class ViaSoar {

	private ProtocolVersion targetVersion = ProtocolVersion.v1_8;
	public static final AttributeKey<UserConnection> LOCAL_VIA_USER = AttributeKey.valueOf("local_via_user");
	public static final AttributeKey<VSNetworkManager> VS_NETWORK_MANAGER = AttributeKey.valueOf("encryption_setup");
	private static ViaSoar manager;
	private static List<ProtocolVersion> versions = new ArrayList<>();

	private static AsyncVersionSlider asyncVersionSlider;

	public static void init(final VSPlatform platform) {

		if (manager != null) {
			return;
		}

		manager = new ViaSoar();
		final File mainFolder = new File(FileLocation.MAIN_DIR, "ViaSoar");

		ViaLoader.init(new ViaVersionPlatformImpl(mainFolder), new ProtocolVLLoader(), new ProtocolVLInjector(), null,
				ViaBackwardsPlatformImpl::new, ViaRewindPlatformImpl::new);

		versions.addAll(ProtocolVersion.getProtocols());

		asyncVersionSlider = new AsyncVersionSlider(-1, 5, 5, 110, 20);
		asyncVersionSlider.setVersion(ViaSoar.getManager().getTargetVersion());
	}

	public void inject(final Channel channel, final VSNetworkManager networkManager) {

		if (channel instanceof SocketChannel && !targetVersion.equals(ProtocolVersion.v1_8)) {

			final UserConnection user = new UserConnectionImpl(channel, true);

			new ProtocolPipelineImpl(user);

			channel.attr(LOCAL_VIA_USER).set(user);
			channel.attr(VS_NETWORK_MANAGER).set(networkManager);

			channel.pipeline().addLast(new ProtocolVLLegacyPipeline(user, targetVersion));
		}
	}

	public ProtocolVersion getTargetVersion() {
		return targetVersion;
	}

	public void setTargetVersion(final ProtocolVersion targetVersion) {
		this.targetVersion = targetVersion;
	}

	public void reorderCompression(final Channel channel) {
		channel.pipeline().fireUserEventTriggered(CompressionReorderEvent.INSTANCE);
	}

	public static ViaSoar getManager() {
		return manager;
	}

	public static AsyncVersionSlider getAsyncVersionSlider() {
		return asyncVersionSlider;
	}

	public static List<ProtocolVersion> getVersions() {
		return versions;
	}
}
