package net.sauce.spotifyintegrationmod.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.sauce.spotifyintegrationmod.spotify.AuthThread;
import net.sauce.spotifyintegrationmod.spotify.SpotifyServer;
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
                    // We need to confirm that we have an access token and that the user is not
                    // currently going through authentication
                    if(!SpotifyServer.authorized) {
                        if(!inAuthProcess) {
                            // If we have no access token, we open a browser and run the auth server for the user to connect to Spotify
                            SpotifyServer.runServer();
                            Runtime rt = Runtime.getRuntime();
                            String url = "http://localhost:8080/login";
                            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                            inAuthProcess = true;

                            // A new thread will handle authentication because otherwise the game client will be stopped.
                            new Thread(new AuthThread()).start();
                        }
                    } else {
                        // Alternates showSong between 1 and -1 so that the song HUD element is toggleable.
                        SpotifyServer.showSong *= -1;
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
