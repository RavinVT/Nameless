package net.ravinvt.nameless.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

import static net.ravinvt.nameless.code.CommonAPI.getPlayerNum;
import static net.ravinvt.nameless.Nameless.*;

public class PlayerEvents implements Listener {
    private static final String TEXTURE_VALUE =
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NDEzMDI2OTEzMiwKICAicHJvZmlsZUlkIiA6ICI4NjY3YmE3MWI4NWE0MDA0YWY1NDQ1N2E5NzM0ZWVkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGV2ZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGE1YmQwMTZiM2M5YTFiOTI3MmU0OTI5ZTMwODI3YTY3YmU0ZWJiMjE5MDE3YWRiYmM0YTRkMjJlYmQ1YjEiCiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk1M2NhYzhiNzc5ZmU0MTM4M2U2NzVlZTJiODYwNzFhNzE2NThmMjE4MGY1NmZiY2U4YWEzMTVlYTcwZTJlZDYiCiAgICB9CiAgfQp9";
    private static final String TEXTURE_SIGNATURE =
            "OZwYGJJERQZNzJht2UEQ7PG7qWkTDdcqVp5CgCsidu1784HjaXdiGkEG1hrC6ZflrVylMX+K+g2o9103jYeQhCFFs5vx0tX7xJAP5QJZ/xdeI6WRSFBtr83hEpHyqMW/WzUoewbgXxu9aMf6a4Q2OMOsGp9ixZUs2qdlpaBq77xxzKATI6Cuq5R1qKqkPFAcDTyiequFgykA6w3+w0vQmb4Nx6qHUeM59j33y1j/81J9dhPRGVmipSop0ofkFB9v4SPT4Q3ImczYEo99R2xatp1J7pTy0mC+Dw5L66OAGY0bIyP7/jSQ+GgSjAbISHDYTUerh3NuqS+2lMVNjHDhgLP/3XYpv+wKzimtZQo9nNC6TRSi/Yfyr5HumWyK352fnaiZxBT/VovdIf5tXK8z08IS+veY4dZKrLOFg8xAiQswFSWanKAnN1CllKrAB0gVHfcXLN2eEMK79wakvHY589WfvlwBfXS09KqC4NFCjosabtrKYyTb+f6KZp0PxueyLMFkai8jGGAK8CQetBy+7OBDzs1fRfeZvGeDZzbM/Hu8amXZSR35ThEOcsKAynoShZejaPPtKD+0tmZXdSpRKra0EpxtUX7/xI+lhZGdTd3w7GLUOlNzyFwUWZYUExqY54zgdDU+r1H/ipPgop60AOcS6OFL0FTkyp0O7M+gNHg=";

    public PlayerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void changePlayer(AsyncPlayerPreLoginEvent event) {
        try {
            PlayerProfile profile = event.getPlayerProfile();
            UUID uuid = event.getUniqueId();
            profile.setName(String.valueOf(getPlayerNum(uuid)));
            profile.setProperty(new ProfileProperty("textures", TEXTURE_VALUE, TEXTURE_SIGNATURE));
            logger.info("Player was authed correctly!");
        } catch (Exception e) {
            Component message = Component.text("There was an issue connecting you to the server!")
                    .color(TextColor.fromHexString("#ff0000"));
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, message);
            logger.severe("Login Exception: " + e.getMessage());
        }
    }

    @EventHandler
    private void sendModifiedJoinMessage(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Component message = Component.text("A player joined the game!").color(TextColor.fromHexString("#ffff00"));
        event.joinMessage(message);

        WrapperPlayServerTeams packet = new WrapperPlayServerTeams(
                "hiddenTeam",
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                Optional.of(new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                        Component.text("hiddenTeam"),
                        null, null,
                        WrapperPlayServerTeams.NameTagVisibility.NEVER,
                        WrapperPlayServerTeams.CollisionRule.NEVER,
                        net.kyori.adventure.text.format.NamedTextColor.WHITE,
                        WrapperPlayServerTeams.OptionData.NONE
                )),
                player.getName()
        );

        for (Player online : Bukkit.getOnlinePlayers()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(online, packet);
        }

        logger.info("Player with the id of " + player.getName() + " logged in!");
    }

    @EventHandler
    private void sendModifiedQuitMessage(PlayerQuitEvent event) {
        Component message = Component.text("A player left the game!").color(TextColor.fromHexString("#ffff00"));
        event.quitMessage(message);
    }

    @EventHandler
    private void preventChatMessages(AsyncChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void sendModifiedDeathMessage(PlayerDeathEvent event) {
        Component message = Component.text("A player died!").color(TextColor.fromHexString("#cc0000"));
        event.deathMessage(message);
    }

    @EventHandler
    private void sendModifiedAdvancementMessage(PlayerAdvancementDoneEvent event) {
        Component message = Component.text("A player has achieved").appendSpace().append(event.getAdvancement().displayName()).color(TextColor.fromHexString("#aaff22"));
        event.message(message);
    }
}
