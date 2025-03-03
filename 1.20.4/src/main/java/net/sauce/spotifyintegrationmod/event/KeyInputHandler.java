package net.sauce.spotifyintegrationmod.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;
import net.sauce.spotifyintegrationmod.client.SongHudOverlay;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;
import net.sauce.spotifyintegrationmod.spotify.threads.AuthThread;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_DISPLAY_SONG = "key.spotifyintegrationmod.display_song";
    public static KeyBinding displaySongKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (displaySongKey.wasPressed()) {
                    // We need to confirm that we have an access token before proceeding
                    if(!AuthServer.authorized && AuthServer.props.getProperty("REFRESH_TOKEN", null) == null) {
                        // If we have no access token, we open a browser and run the auth server for the user to connect to Spotify
                        String url = "http://localhost:36477/login";
                        Util.getOperatingSystem().open(url);
                    } else {
                        SongHudOverlay.showSong = !SongHudOverlay.showSong;
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void register() {
        // Ties backtick (`) to the above function
        displaySongKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spotifyintegrationmod.display_song",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "key.category.spotifyintegrationmod.main"
        ));

        // Allow for skipping songs? (premium function)

        registerKeyInputs();
    }
}
