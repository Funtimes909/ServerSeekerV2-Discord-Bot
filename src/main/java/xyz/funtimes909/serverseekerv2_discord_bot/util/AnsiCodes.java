package xyz.funtimes909.serverseekerv2_discord_bot.util;

import java.util.HashMap;

public enum AnsiCodes {
    BLACK('0', "black", 30),
    DARK_BLUE('1', "dark_blue", 34),
    DARK_GREEN('2', "dark_green", 32),
    DARK_AQUA('3', "dark_aqua", 36),
    DARK_RED('4', "dark_red", 31),
    PURPLE('5', "dark_purple", 35),
    GOLD('6', "gold", 33),
    GRAY('7', "gray", 37),
    DARK_GRAY('8', "dark_gray", 30),
    BLUE('9', "blue", 34),
    GREEN('a', "green", 32),
    AQUA('b', "aqua", 36),
    RED('c', "red", 31),
    PINK('d', "light_purple", 35),
    YELLOW('e', "yellow", 33),
    WHITE('f', "white", 37),
    BOLD('l', "bold", 1),
    UNDERLINE('n', "underline", 4);

    public static final HashMap<String, AnsiCodes> codes = new HashMap<>();
    public static final HashMap<Character, AnsiCodes> colors = new HashMap<>();

    static {
        for (AnsiCodes v: AnsiCodes.values()) {
            codes.put(v.name, v);
        }

        for (AnsiCodes v: AnsiCodes.values()) {
            colors.put(v.c, v);
        }
    }

    public final char c;
    public final String name;
    public final int ansi;

    AnsiCodes(char c, String name, Integer ansi) {
        this.c = c;
        this.name = name;
        this.ansi = ansi;
    }
}
