package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

import net.minecraft.entity.Entity;

public interface DamageEntityEventListener {

	void onDamageEntity(Entity entity);

	class DamageEntityEvent extends AbstractEvent<DamageEntityEventListener> {

		public static final int ID = 15;
		private final Entity entity;

		public DamageEntityEvent(Entity entity) {
			this.entity = entity;
		}

		@Override
		public void call(DamageEntityEventListener listener) {
			listener.onDamageEntity(entity);
		}
	}
}
