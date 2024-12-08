package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import xyz.funtimes909.serverseekerv2_discord_bot.util.CommandRegisterer;

import java.util.List;

public class AutoCompleteBot extends ListenerAdapter {

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("search") && event.getFocusedOption().getName().equalsIgnoreCase("country")) {

            List<Command.Choice> options = CommandRegisterer.countries.entrySet().stream()
                    .filter(event.getFocusedOption().getValue().isEmpty() ?
                            word -> word.getKey().startsWith("A") :
                            word -> word.getKey().startsWith(event.getFocusedOption().getValue())
                    )
                    .map(word -> new Command.Choice(
                            word.getKey(),
                            word.getValue())
                    )
                    .limit(25)
                    .toList();

            event.replyChoices(options).queue();
        }
    }
}