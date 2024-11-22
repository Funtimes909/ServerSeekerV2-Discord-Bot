package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        switch (event.getComponentId()) {
            case "SearchButton1":
                Search.serverSelectedButtonEvent(findField(event, 1), (short) 25565, event);
                break;
            case "SearchButton2":
                Search.serverSelectedButtonEvent(findField(event, 2), (short) 25565, event);
                break;
            case "SearchButton3":
                Search.serverSelectedButtonEvent(findField(event, 3), (short) 25565, event);
                break;
            case "SearchButton4":
                Search.serverSelectedButtonEvent(findField(event, 4), (short) 25565, event);
                break;
            case "SearchButton5":
                Search.serverSelectedButtonEvent(findField(event, 5), (short) 25565, event);
                break;
            case "PagePrevious":
                Search.scrollResults(false, false);
                break;
            case "PageNext":
                Search.scrollResults(false, true);
                break;
        }
    }

    private static String findField(ButtonInteractionEvent event, int fieldNumber) {
        String fieldName = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith(String.valueOf(fieldNumber))).findFirst().get().getName();
        return fieldName.substring(fieldName.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
    }
}
