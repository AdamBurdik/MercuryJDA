import me.adamix.mercury.jda.exception.ParsingException;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceholderTest {
	@Test
	public void testPlaceholderManagerParsing() throws ParsingException {
		PlaceholderManager placeholderManager = new PlaceholderManager();

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
}
