package net.ravinvt.nameless;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.ravinvt.nameless.commands.NamelessCommand;
import net.ravinvt.nameless.listeners.PlayerEvents;
import net.ravinvt.nameless.listeners.SignEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Nameless extends JavaPlugin implements Listener {
    public static JavaPlugin plugin;
    public static Logger logger;
    public static YamlConfiguration players = new YamlConfiguration();
    private static File playersFile;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .checkForUpdates(false)
                .bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        PacketEvents.getAPI().init();
        getServer().getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        playersFile = new File(getDataFolder(), "players.yml");

        if (!playersFile.exists()) { try { playersFile.createNewFile(); } catch (IOException e) { logger.severe("Create Players: " + e.getMessage()); } }

        try {
            players.load(playersFile);
            logger.info("Load Players: Loaded players file.");
        } catch (Exception e) {
            logger.severe("Load Players: " + e.getMessage());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScheduler().runAtFixedRate(
                    plugin,
                    task -> {
                        Random random = new Random();
                        int value = random.nextInt(players.getKeys(false).size() + 1);
                        player.customName(Component.text(String.valueOf(value)));
                        player.displayName(Component.text(String.valueOf(value)));
                    },
                    null,
                    20L * 60L * 20L,
                    20L * 60L * 20L
            );
        }

        new PlayerEvents();
        new SignEvents();

        Objects.requireNonNull(getCommand("nameless")).setExecutor(new NamelessCommand());
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        try {
            players.save(playersFile);
            logger.info("Save Players: Saved players file.");
        } catch (IOException e) {
            logger.severe("Save Players: " + e.getMessage());
        }
    }


    //
    // Command based stuff that I don't want to relocate
    // This stuff is complete and shouldn't be touched
    //     -- Ravin (16/07/26)
    //
    @EventHandler
    private void removePrivateCommands(PlayerCommandSendEvent event) {
        Collection<String> commands = event.getCommands();
        commands.remove("tell");
        commands.remove("w");
        commands.remove("msg");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (
                label.equalsIgnoreCase("w") ||
                        label.equalsIgnoreCase("tell") ||
                        label.equalsIgnoreCase("msg") ||
                        label.equalsIgnoreCase("me") ||
                        label.equalsIgnoreCase("say")
        ) {
            return false;
        }
        return super.onCommand(sender, command, label, args);
    }
}