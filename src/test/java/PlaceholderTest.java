import me.adamix.mercury.jda.exception.ParsingException;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlaceholderTest {
	private final PlaceholderManager placeholderManager = new PlaceholderManager();

	@Test
	public void testDataPlaceholder() throws ParsingException {
		record Data(
				@NotNull String name,
				int age
		) {}

		placeholderManager.registerPlaceholder("test", Data.class, (args, data) -> {
			if (!args.hasNext()) return data.toString();

			return String.valueOf(switch (args.pop()) {
				case "name" -> data.name;
				case "age" -> data.age;
				default -> throw new IllegalStateException("Unexpected value: " + args.pop());
			});
		});

		Data data = new Data("john doe", 20);
		String text = "Hello, <test:name>! You are <test:age>yo.";
		String parsed = placeholderManager.parse(text, List.of(data));
		assertEquals(parsed, "Hello, " + data.name + "! You are " + data.age + "yo.");
	}

	@Test
	public void testGlobalPlaceholder() throws ParsingException {
		placeholderManager.registerGlobalPlaceholder("ping", args -> "Pong!");

		String text = "Hello, <ping>";
		String parsed = placeholderManager.parse(text);
		assertEquals("Hello, " + "Pong!", parsed);
	}

	@Test
	public void testRedirectPlaceholder() throws ParsingException {
		record Data(
				@NotNull String name,
				int age
		) {}

		record Container(
				int id,
				@NotNull Data data
		) {}

		Data data = new Data("John Doe", 25);
		Container container = new Container(10, data);

		placeholderManager.registerPlaceholder("test", Data.class, (args, d) -> {
			if (!args.hasNext()) return d.toString();

			return String.valueOf(switch (args.pop()) {
				case "name" -> d.name;
				case "age" -> d.age;
				default -> throw new IllegalStateException("Unexpected value: " + args.pop());
			});
		});

		placeholderManager.registerRedirect(Container.class, cont -> List.of(cont.data));

		String text = "Hello, <test:name>! You are <test:age>yo.";
		String parsed = placeholderManager.parse(text, List.of(container));
		assertEquals(parsed, "Hello, " + data.name + "! You are " + data.age + "yo.");
	}

	@Test
	public void testPlaceholderEscaping() throws ParsingException {
		record Data(
				@NotNull String name,
				int age
		) {}

		placeholderManager.registerPlaceholder("test", Data.class, (args, data) -> {
			if (!args.hasNext()) return data.toString();

			return String.valueOf(switch (args.pop()) {
				case "name" -> data.name;
				case "age" -> data.age;
				default -> throw new IllegalStateException("Unexpected value: " + args.pop());
			});
		});

		Data data = new Data("john doe", 20);
		String text = "Hello, <\\test:name>! You are <\\test:age>yo.";
		String parsed = placeholderManager.parse(text, List.of(data));
		assertEquals("Hello, <test:name>! You are <test:age>yo.", parsed);
	}
}
