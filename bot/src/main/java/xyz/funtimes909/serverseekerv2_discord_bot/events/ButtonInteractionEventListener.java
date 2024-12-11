package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Playerhistory;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ButtonInteractionEventListener extends ListenerAdapter {
    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Search command = SlashCommandListener.searchCommands.get(event.getUser().getId());
        if (event.getComponentId().startsWith("SearchButton") && command == null) {
            return;
        }

        event.deferEdit().queue();

        if (event.getComponentId().startsWith("SearchButton")) {
            int button = Integer.parseInt(event.getComponentId().split("SearchButton")[1]);
            executor.execute(() -> command.serverSelectedButtonEvent(findField(event, button), (short) 25565, event));
            return;
        } else if (event.getComponentId().startsWith("PlayerHistory")) {
            executor.execute(() -> Playerhistory.optionSelected(event));
            return;
        }

        switch (event.getComponentId()) {
            case "PagePrevious":
                if (command.pointer <= 6 && command.totalRows >= 6) {
                    command.offset -= 100;
                    command.pointer = 1;
                    command.runQuery(false);
                }
                executor.execute(() -> command.scrollResults(false, false));
                break;
            case "PageNext":
                if (command.pointer >= 46) {
                    command.offset += 50;
                    command.pointer = 1;
                    command.runQuery(false);
                }
                executor.execute(() -> command.scrollResults(false, true));
                break;
        }
    }

    private static String findField(ButtonInteractionEvent event, int fieldNumber) {
        String fieldName = event.getMessage()
                .getEmbeds()
                .getFirst()
                .getFields()
                .stream()
                .filter(index -> index.getName()
                .startsWith(String.valueOf(fieldNumber)))
                .findFirst()
                .get()
                .getName();

        return fieldName.substring(fieldName.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
    }
}
