package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ButtonInteractionEventListener extends ListenerAdapter {
    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Search command = SlashCommandListener.searchCommands.get(event.getUser().getId());
        if (command == null) return;
        event.deferEdit().queue();

        switch (event.getComponentId()) {
            case "SearchButton1" -> executor.execute(() -> command.serverSelectedButtonEvent(findField(event, 1), (short) 25565, event));
            case "SearchButton2" -> executor.execute(() -> command.serverSelectedButtonEvent(findField(event, 2), (short) 25565, event));
            case "SearchButton3" -> executor.execute(() -> command.serverSelectedButtonEvent(findField(event, 3), (short) 25565, event));
            case "SearchButton4" -> executor.execute(() -> command.serverSelectedButtonEvent(findField(event, 4), (short) 25565, event));
            case "SearchButton5" -> executor.execute(() -> command.serverSelectedButtonEvent(findField(event, 5), (short) 25565, event));
            case "PagePrevious" -> {
                if (command.pointer <= 6 && command.totalRows >= 6) {
                    command.offset -= 100;
                    command.pointer = 1;
                    command.runQuery(false);
                }
                executor.execute(() -> command.scrollResults(false, false));
            }
            case "PageNext" -> {
                if (command.pointer >= 46) {
                    command.offset += 50;
                    command.pointer = 1;
                    command.runQuery(false);
                }
                executor.execute(() -> command.scrollResults(false, true));
            }
        }
    }

    private static String findField(ButtonInteractionEvent event, int fieldNumber) {
        String fieldName = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith(String.valueOf(fieldNumber))).findFirst().get().getName();
        return fieldName.substring(fieldName.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
    }
}
