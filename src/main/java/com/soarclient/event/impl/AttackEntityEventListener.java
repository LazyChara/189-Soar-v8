package com.soarclient.event.impl;

import com.soarclient.event.api.AbstractEvent;

import net.minecraft.entity.Entity;

public interface AttackEntityEventListener {

	void onAttackEntity(Entity entity);

	class AttackEntityEvent extends AbstractEvent<AttackEntityEventListener> {

		public static final int ID = 14;
		private final Entity entity;

		public AttackEntityEvent(Entity entity) {
			this.entity = entity;
		}

		@Override
		public void call(AttackEntityEventListener listener) {
			listener.onAttackEntity(entity);
		}
	}
}
