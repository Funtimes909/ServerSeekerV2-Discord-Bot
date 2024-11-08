package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;
import xyz.funtimes909.serverseekerv2_discord_bot.util.Rescan;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "SearchButton1":
                Search.serverSelectedButtonEvent(1);
                break;
            case "SearchButton2":
                Search.serverSelectedButtonEvent(2);
                break;
            case "SearchButton3":
                Search.serverSelectedButtonEvent(3);
                break;
            case "SearchButton4":
                Search.serverSelectedButtonEvent(4);
                break;
            case "SearchButton5":
                Search.serverSelectedButtonEvent(5);
                break;
            case "PagePrevious":
                if (Search.page != 1) Search.page -= 1;
                Search.scrollResults(-10, false);
                break;
            case "PageNext":
                if (Search.page != Search.rowCount / 5) Search.page += 1;
                Search.scrollResults(0, false);
                break;
            case "Rescan":
                Search.rescan();
                break;
        }
        event.deferEdit().complete();
    }
}
