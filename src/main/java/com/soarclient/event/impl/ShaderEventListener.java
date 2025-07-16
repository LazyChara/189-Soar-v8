package com.soarclient.event.impl;

import java.util.ArrayList;
import java.util.List;

import com.soarclient.event.api.AbstractEvent;

import net.minecraft.client.shader.ShaderGroup;

public interface ShaderEventListener {

	void onShader(ShaderEvent event);

	class ShaderEvent extends AbstractEvent<ShaderEventListener> {

		public static final int ID = 22;
		private List<ShaderGroup> groups = new ArrayList<>();

		@Override
		public void call(ShaderEventListener listener) {
			listener.onShader(this);
		}

		public List<ShaderGroup> getGroups() {
			return groups;
		}
	}
}
