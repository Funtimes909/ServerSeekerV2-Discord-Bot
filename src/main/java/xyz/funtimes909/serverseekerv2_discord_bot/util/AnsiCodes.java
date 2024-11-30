package xyz.funtimes909.serverseekerv2_discord_bot.util;

import java.util.HashMap;

public enum AnsiCodes {
    BLACK('0', "black"),
    DARK_BLUE('1', "dark_blue"),
    DARK_GREEN('2', "dark_green"),
    DARK_AQUA('3', "dark_AQUA"),
    DARK_RED('4', "dark_red"),
    PURPLE('5', "dark_purple"),
    GOLD('6', "gold"),
    GRAY('7', "gray"),
    DARK_GRAY('8', "dark_gray"),
    BLUE('9', "blue"),
    GREEN('a', "green"),
    AQUA('b', "aqua"),
    RED('c', "red"),
    PINK('d', "light_purple"),
    YELLOW('e', "yellow"),
    WHITE('f', "white"),
    RANDOM('k', "obfuscated"),
    BOLD('l', "bold"),
    STRIKETHROUGH('m', "strikethrough"),
    UNDERLINE('n', "underline"),
    ITALIC('o', "italic");

    public static final HashMap<String, AnsiCodes> codes = new HashMap<>();

    static {
        for (AnsiCodes v: AnsiCodes.values()) {
            codes.put(v.name, v);
        }
    }

    public final char c;
    public final String name;

    AnsiCodes(char c, String name) {
        this.c = c;
        this.name = name;
    }
}