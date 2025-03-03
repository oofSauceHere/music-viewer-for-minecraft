package net.oofsauce.spotifymc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.oofsauce.spotifymc.client.SongHudOverlay;
import net.oofsauce.spotifymc.event.KeyInputHandler;
import net.oofsauce.spotifymc.spotify.AuthServer;
import net.oofsauce.spotifymc.spotify.threads.AuthThread;
import net.oofsauce.spotifymc.spotify.threads.DataThread;

import java.io.IOException;

public class SpotifyMCClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Start fetching data from Spotify's API
        new Thread(new DataThread()).start();

        // Begin authentication process
        try {
            AuthServer.runServer();
            // A new thread will handle authentication because otherwise the game client will be stopped.
            new Thread(new AuthThread()).start();
        } catch(IOException e) {
            e.printStackTrace();;
        }

        // Register keybinds and begin rendering HUD.
        KeyInputHandler.register();
        HudRenderCallback.EVENT.register(new SongHudOverlay());
    }
}
