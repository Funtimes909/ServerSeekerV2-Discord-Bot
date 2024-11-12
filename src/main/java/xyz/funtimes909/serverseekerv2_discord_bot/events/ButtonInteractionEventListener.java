package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String name;
        String address;
        switch (event.getComponentId()) {
            case "SearchButton1":
                name = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith("1")).findFirst().get().getName();
                address = name.substring(name.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
                Search.serverSelectedButtonEvent(address, (short) 25565);
                break;
            case "SearchButton2":
                name = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith("2")).findFirst().get().getName();
                address = name.substring(name.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
                Search.serverSelectedButtonEvent(address, (short) 25565);
                break;
            case "SearchButton3":
                name = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith("3")).findFirst().get().getName();
                address = name.substring(name.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
                Search.serverSelectedButtonEvent(address, (short) 25565);
                break;
            case "SearchButton4":
                name = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith("4")).findFirst().get().getName();
                address = name.substring(name.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
                Search.serverSelectedButtonEvent(address, (short) 25565);
                break;
            case "SearchButton5":
                name = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith("5")).findFirst().get().getName();
                address = name.substring(name.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
                Search.serverSelectedButtonEvent(address, (short) 25565);
                break;
            case "PagePrevious":
                if (Search.page != 1) Search.page -= 1;
                Search.scrollResults(-10, false);
                break;
            case "PageNext":
                if (Search.page != Search.rowCount / 5) Search.page += 1;
                Search.scrollResults(0, false);
                break;
        }
        event.deferEdit().complete();
    }
}
