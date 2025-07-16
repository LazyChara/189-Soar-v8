package com.soarclient.event;

import java.util.function.Consumer;

import com.soarclient.event.api.AbstractEvent;

public class EventBus {

	private static final EventBus INSTANCE = new EventBus(32, Throwable::printStackTrace);

	public static EventBus getInstance() {
		return INSTANCE;
	}

	private Object[][] registers;
	public Consumer<Throwable> errorHandler;

	public EventBus(final int eventCapacity) {
		this(eventCapacity, Throwable::printStackTrace);
	}

	public EventBus(final int eventCapacity, final Consumer<Throwable> errorHandler) {
		if (eventCapacity < 1) {
			throw new IllegalArgumentException("Event capacity must be at least 1");
		}
		this.registers = new Object[eventCapacity][0];

		if (errorHandler == null) {
			throw new IllegalArgumentException("Error handler must not be null");
		}
		this.errorHandler = errorHandler;
	}

	private void setEventCapacity(final int eventCapacity) {
		if (eventCapacity < 1) {
			throw new IllegalArgumentException("Event capacity must be at least 1");
		}
		final Object[][] subscribers = this.registers;

		this.registers = new Object[eventCapacity][0];

		for (int i = 0; i < subscribers.length; i++) {
			this.registers[i] = subscribers[i];
		}
	}

	public void registers(final Object object, final int... ids) {
		for (int id : ids) {
			register(object, id);
		}
	}

	public void register(final Object object, final int id) {
		if (registers.length <= id)
			setEventCapacity(id + 1);

		final Object[] subscriberArr = registers[id];

		final Object[] newSubscriberArr = new Object[subscriberArr.length + 1];
		System.arraycopy(subscriberArr, 0, newSubscriberArr, 0, subscriberArr.length);
		newSubscriberArr[subscriberArr.length] = object;

		registers[id] = newSubscriberArr;
	}

	public void unregisters(final Object object, final int... ids) {
		for (int id : ids) {
			unregister(object, id);
		}
	}

	public void unregister(final Object object, final int id) {
		Object[] subscriberArr = registers[id];

		int removeIndex = -1;
		for (int i = 0; i < subscriberArr.length; i++) {
			if (subscriberArr[i] == object) {
				removeIndex = i;
				break;
			}
		}

		if (removeIndex == -1)
			return;

		Object[] newSubscriberArr = new Object[subscriberArr.length - 1];
		if (removeIndex > 0) {
			System.arraycopy(subscriberArr, 0, newSubscriberArr, 0, removeIndex);
		}
		System.arraycopy(subscriberArr, removeIndex + 1, newSubscriberArr, removeIndex,
				subscriberArr.length - removeIndex - 1);

		registers[id] = newSubscriberArr;
	}

	@SuppressWarnings("rawtypes")
	public void call(final AbstractEvent event, final int id) {
		if (registers.length > id) {
			try {
				callUnsafe(event, id);
			} catch (final Throwable t) {
				this.errorHandler.accept(t);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void callUnsafe(final AbstractEvent event, final int id) {
		final Object[] subscriber = registers[id];

		for (Object o : subscriber) {
			event.call(o);
		}
	}
}