package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ButtonInteractionEventListener extends ListenerAdapter {
    private static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Search searchCommand = SlashCommandListener.searchCommands.get(event.getMessageIdLong());
        if (event.getComponentId().startsWith("Search") && searchCommand == null) {
            return;
        }

        event.deferEdit().queue();

        // Handle selection buttons for searches and player history
        if (event.getComponentId().startsWith("SearchButton")) {
            executor.execute(() -> {
                HashMap<String, Short> map = getAddress(event, Integer.parseInt(event.getComponentId().split("SearchButton")[1]));
                Map.Entry<String, Short> entry = map.entrySet().iterator().next();

                searchCommand.optionSelected(
                        entry.getKey(),
                        entry.getValue(),
                        event
                );
            });

            return;
        }

        switch (event.getComponentId()) {
            case "SearchPrevious":
                searchCommand.offset -= 5;
                searchCommand.pointer -= 5;
                executor.execute(searchCommand::runQuery);
                break;
            case "SearchNext":
                searchCommand.offset += 5;
                searchCommand.pointer += 5;
                executor.execute(searchCommand::runQuery);
                break;
        }
    }

    private static HashMap<String, Short> getAddress(ButtonInteractionEvent event, int fieldNumber) {
        String[] field = event.getMessage()
                .getEmbeds()
                .getFirst()
                .getFields()
                .stream()
                .filter(i -> i.getName().startsWith(String.valueOf(fieldNumber)))
                .findFirst()
                .get()
                .getName()
                .split("``")[1]
                .split("``")[0]
                .split(":");

        HashMap<String, Short> server = new HashMap<>();
        server.put(field[0], Short.valueOf(field[1].replaceAll(" ", "")));
        return server;
    }
}