package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "SearchButton1":
                Search.buttonEvent(1);
                break;
            case "SearchButton2":
                Search.buttonEvent(2);
                break;
            case "SearchButton3":
                Search.buttonEvent(3);
                break;
            case "SearchButton4":
                Search.buttonEvent(4);
                break;
            case "SearchButton5":
                Search.buttonEvent(5);
                break;
            case "PagePrevious":
                Search.scrollResults(-10, false);
                break;
            case "PageNext":
                Search.scrollResults(0, false);
                break;
        }
        event.deferEdit().complete();
    }
}
