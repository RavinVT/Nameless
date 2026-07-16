package net.ravinvt.nameless.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Random;

import static net.ravinvt.nameless.Nameless.plugin;

public class SignEvents implements Listener {
    public SignEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onSignChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        Side side = event.getSide();

        String glitchChars = "!@#$%^&*?/\\|~";
        Random random = new Random();

        String[] originals = event.lines().stream()
                .map(c -> PlainTextComponentSerializer.plainText().serialize(c))
                .toArray(String[]::new);

        Bukkit.getRegionScheduler().runAtFixedRate(plugin, sign.getLocation(), task -> {
            int lineIndex = random.nextInt(originals.length);
            String base = originals[lineIndex];

            if (base.isEmpty()) return;

            char[] chars = base.toCharArray();
            int charIndex = random.nextInt(chars.length);
            chars[charIndex] = glitchChars.charAt(random.nextInt(glitchChars.length()));

            Sign current = (Sign) sign.getBlock().getState();
            current.getSide(side).line(lineIndex, Component.text(new String(chars)));
            current.update();
        }, 1L, 20L);
    }
}
