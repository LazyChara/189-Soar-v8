package com.soarclient.viasoar.api;

import com.viaversion.vialoader.impl.viaversion.VLInjector;
import com.viaversion.vialoader.netty.VLLegacyPipeline;

public class ProtocolVLInjector extends VLInjector {

	@Override
	public String getDecoderName() {
		return VLLegacyPipeline.VIA_DECODER_NAME;
	}

	@Override
	public String getEncoderName() {
		return VLLegacyPipeline.VIA_ENCODER_NAME;
	}
}