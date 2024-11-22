package xyz.funtimes909.serverseekerv2_discord_bot.events;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import xyz.funtimes909.serverseekerv2_discord_bot.commands.Search;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ButtonInteractionEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        event.deferEdit().queue();
        switch (event.getComponentId()) {
            case "SearchButton1":
                executor.execute(() -> Search.serverSelectedButtonEvent(findField(event, 1), (short) 25565, event));
                break;
            case "SearchButton2":
                executor.execute(() -> Search.serverSelectedButtonEvent(findField(event, 2), (short) 25565, event));
                break;
            case "SearchButton3":
                executor.execute(() -> Search.serverSelectedButtonEvent(findField(event, 3), (short) 25565, event));
                break;
            case "SearchButton4":
                executor.execute(() -> Search.serverSelectedButtonEvent(findField(event, 4), (short) 25565, event));
                break;
            case "SearchButton5":
                executor.execute(() -> Search.serverSelectedButtonEvent(findField(event, 5), (short) 25565, event));
                break;
            case "PagePrevious":
                executor.execute(() -> Search.scrollResults(false, false));
                break;
            case "PageNext":
                executor.execute(() -> Search.scrollResults(false, true));
                break;
        }
    }

    private static String findField(ButtonInteractionEvent event, int fieldNumber) {
        String fieldName = event.getMessage().getEmbeds().getFirst().getFields().stream().filter((index) -> index.getName().startsWith(String.valueOf(fieldNumber))).findFirst().get().getName();
        return fieldName.substring(fieldName.indexOf("`") + 2).replaceAll("``", "").split(" ")[0];
    }
}
