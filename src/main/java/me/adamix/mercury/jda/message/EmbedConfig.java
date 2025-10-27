package me.adamix.mercury.jda.message;

import me.adamix.mercury.jda.exception.ParsingException;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record EmbedConfig(
		@Nullable String title,
		@Nullable String description,
		@Nullable String url,
		@Nullable String timestamp,
		@Nullable Integer color,
		@Nullable Footer footer,
		@Nullable Image image,
		@Nullable Thumbnail thumbnail,
		@Nullable Video video,
		@Nullable Provider provider,
		@Nullable Author author,
		@Nullable List<Field> fields
) {
	public record Footer(
			@Nullable String text,
			@Nullable String iconUrl,
			@Nullable String proxyIconUrl
	) {}

	public record Image(
			@Nullable String url,
			@Nullable String proxyUrl,
			@Nullable Integer height,
			@Nullable Integer width
	) {}

	public record Thumbnail(
			@Nullable String url,
			@Nullable String proxyUrl,
			@Nullable Integer height,
			@Nullable Integer width
	) {}

	public record Video(
			@Nullable String url,
			@Nullable Integer height,
			@Nullable Integer width
	) {}

	public record Provider(
			@Nullable String name,
			@Nullable String url
	) {}

	public record Author(
			@Nullable String name,
			@Nullable String url,
			@Nullable String iconUrl,
			@Nullable String proxyIconUrl
	) {}

	public record Field(
			@Nullable String name,
			@Nullable String value,
			@Nullable Boolean inline
	) {}

	public @NotNull MessageEmbed create(
			@NotNull PlaceholderManager placeholderManager,
			@NotNull List<Object> args
	) throws ParsingException {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle(placeholderManager.parse(title, args), null);
		builder.setDescription(placeholderManager.parse(description, args));
		builder.setUrl(placeholderManager.parse(url, args));
		builder.setTimestamp(timestamp != null ? java.time.OffsetDateTime.parse(placeholderManager.parse(timestamp, args)) : null);
		if (color != null) builder.setColor(color);

		if (footer != null) {
			builder.setFooter(
					placeholderManager.parse(footer.text(), args),
					placeholderManager.parse(footer.iconUrl(), args)
			);
		}

		if (image != null) {
			builder.setImage(placeholderManager.parse(image.url(), args));
		}

		if (thumbnail != null) {
			builder.setThumbnail(placeholderManager.parse(thumbnail.url(), args));
		}

		if (author != null) {
			builder.setAuthor(
					placeholderManager.parse(author.name(), args),
					placeholderManager.parse(author.url(), args),
					placeholderManager.parse(author.iconUrl(), args)
			);
		}

		if (fields != null) {
			for (Field field : fields) {
				builder.addField(
						placeholderManager.parse(field.name(), args),
						placeholderManager.parse(field.value(), args),
						field.inline() != null ? field.inline() : false
				);
			}
		}

		return builder.build();
	}
}
