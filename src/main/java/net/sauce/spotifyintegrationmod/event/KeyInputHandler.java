package net.sauce.spotifyintegrationmod.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.sauce.spotifyintegrationmod.client.SongHudOverlay;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;
import net.sauce.spotifyintegrationmod.spotify.threads.AuthThread;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_DISPLAY_SONG = "key.spotifyintegrationmod.display_song";
    // This is necessary because it prevents multiple AuthThreads from being created.
    public static boolean inAuthProcess = false;
    public static KeyBinding displaySongKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                if (displaySongKey.wasPressed()) {
                    // We need to confirm that we have an access token before proceeding
                    if(!AuthServer.authorized) {
                            // If we have no access token, we open a browser and run the auth server for the user to connect to Spotify
                        if(!inAuthProcess) {
                            // A new thread will handle authentication because otherwise the game client will be stopped.
                            AuthServer.runServer();
                            new Thread(new AuthThread()).start();
                            inAuthProcess = true;
                        }

                        // only works on windows!!
                        String os = System.getProperty("os.name").toLowerCase();
                        Runtime rt = Runtime.getRuntime();
                        String url = "http://localhost:8080/login";
                        if(os.indexOf("win") >= 0) {
                            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                        } else if(os.indexOf("mac") >= 0) {
                            rt.exec("open " + url);
                        }
                    } else {
                        // Alternates showSong between 1 and -1 so that the song HUD element is toggleable.
                        SongHudOverlay.showSong *= -1;
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

        registerKeyInputs();
    }
}
