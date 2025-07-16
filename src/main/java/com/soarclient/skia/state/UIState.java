package com.soarclient.skia.state;

import java.util.Stack;

public class UIState {

	private static final Stack<State> stateStack = new Stack<>();

	public static void backup() {
		State currentState = new State();
		currentState.backupCurrentState();
		stateStack.push(currentState);
	}

	public static void restore() {
		if (!stateStack.isEmpty()) {
			State previousState = stateStack.pop();
			previousState.restorePreviousState();
		}
	}
}
