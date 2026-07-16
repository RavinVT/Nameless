package net.ravinvt.nameless;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.UUID;

import static net.ravinvt.nameless.Nameless.players;

public class CommonAPI {
    public static int getPlayerNum(UUID uuid) {
        int play = players.getInt(uuid.toString(), -1);
        if (play == -1) {
            int new_num = players.getKeys(false).size() + 1;
            players.set(uuid.toString(), new_num);
            return new_num;
        }
        return play;
    }

    public static Component getPrefix() {
        return MiniMessage.
                miniMessage().
                deserialize("<bold><gradient:white:dark_gray:white>Nameless <dark_gray>»<reset>")
                .appendSpace();
    }
}
