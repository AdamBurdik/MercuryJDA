[![](https://www.jitpack.io/v/adamBurdik/MercuryJDA.svg)](https://www.jitpack.io/#adamBurdik/MercuryJDA)

# MercuryJDA
MercuryJDA is a library that provides simple component creation in [JDA](https://github.com/discord-jda/JDA).

# Disclaimer
This library is a work in progress — many features may be added or changed in future updates.

# How To Build
1. Clone the repository
```bash
git clone https://github.com/adamBurdik/MercuryJDA
```
2. Navigate to the directory
```bash
cd MercuryJDA
```
3. Build the module
```bash
gradlew build
```

# How To Use
1. Add MercuryJDA as a dependency to your project, using jitpack
```kotlin
maven { url = uri("https://www.jitpack.io") }

dependencies {
    implementation("com.github.adamBurdik:MercuryJDA:VERSION") // Get version from https://github.com/AdamBurdik/MercuryJDA/releases
    // Or use commit
}
```
2. Add MercuryJDAListener to your bot
```java
JDA jda = JDABuilder.createDefault("YOUR_BOT_TOKEN")
        .addEventListeners(new MercuryJDAListener())
        .build();
```
3. Create a message template with button and select menu handlers
```java
private static final MessageTemplate TEMPLATE = MessageTemplate.create()
        .button("test", (event, ctx) -> {
            event.deferReply(true).queue();
            event.getHook().sendMessage("Button clicked! Args: " + Arrays.toString(ctx.args())).queue();
            return Result.SUCCESS;
        })
        .select("dropdown", (event, ctx) -> {
            event.deferReply(true).queue();
            event.getHook().sendMessage("Selected: " + event.getValues().get(0)).queue();
            return Result.SUCCESS;
        })
        .build();
```
4. Create message instances with components and arguments
```java
@Override
public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    event.deferReply(true).queue();

    TEMPLATE.instance(event.getUser().getIdLong())
            .button("test", 0, "Click Me", ButtonStyle.PRIMARY, null, false, true, "arg1", "arg2")
            .stringSelect("dropdown", 1, "Choose option")
                .option("Option 1", "value1")
                .option("Option 2", "value2")
                .build()
            .sendMessage("Hello! Click the button or select an option:", event.getHook())
            .queue();
}
```

# Custom Listener
You can create your own listener to handle interaction results with custom messages:

```java
public class CustomMercuryListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Result result = MessageComponentManager.handleButton(event);
        if (result == Result.EXPIRED) {
            event.reply("⏰ This button has expired!").setEphemeral(true).queue();
        } else if (result == Result.DIFFERENT_AUTHOR) {
            event.reply("❌ This button is not for you!").setEphemeral(true).queue();
        }
        // Result.SUCCESS is handled by template handlers
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        Result result = MessageComponentManager.handleSelect(event);
        if (result == Result.EXPIRED) {
            event.reply("⏰ This menu has expired!").setEphemeral(true).queue();
        } else if (result == Result.DIFFERENT_AUTHOR) {
            event.reply("❌ This menu is not for you!").setEphemeral(true).queue();
        }
    }
}
```

Then register your custom listener instead of the default one:
```java
JDA jda = JDABuilder.createDefault("YOUR_BOT_TOKEN")
        .addEventListeners(new CustomMercuryListener())
        .build();
```

# Contributing
Contributions are welcome!

If you have suggestions, ideas, or issues, feel free to open an issue or contact me on discord: @adamix.dev.

# Credits
- [JDA](https://github.com/discord-jda/JDA) - The Java Discord API library

