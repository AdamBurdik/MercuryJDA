package me.adamix.mercury.jda.placeholder;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class ArgumentQueue {
	private final Queue<String> args;

	public ArgumentQueue(String[] args) {
		this.args = new ArrayDeque<>(Arrays.asList(args));
	}

	public boolean hasNext() {
		return !args.isEmpty();
	}

	public String pop() {
		return args.poll();
	}

	public String peek() {
		return args.peek();
	}
}