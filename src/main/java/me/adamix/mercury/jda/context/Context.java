package me.adamix.mercury.jda.context;

import org.jetbrains.annotations.NotNull;


public record Context<T>(@NotNull T component, @NotNull String[] args) {
	public int argCount() {
		return args.length;
	}

	public boolean hasArg(int index) {
		return index >= 0 && index < args.length;
	}

	public @NotNull String arg(int index) {
		return args[index];
	}

	public int intArg(int index) {
		return Integer.parseInt(args[index]);
	}

	public long longArg(int index) {
		return Long.parseLong(args[index]);
	}

	public double doubleArg(int index) {
		return Double.parseDouble(args[index]);
	}

	public boolean boolArg(int index) {
		String value = args[index].toLowerCase();
		return value.equals("true");
	}

	public @NotNull String joinedArgs(int startIndex) {
		if (startIndex >= args.length) return "";
		return String.join(" ", java.util.Arrays.copyOfRange(args, startIndex, args.length));
	}
}