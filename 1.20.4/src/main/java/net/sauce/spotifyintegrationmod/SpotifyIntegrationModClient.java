package net.sauce.spotifyintegrationmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.sauce.spotifyintegrationmod.client.SongHudOverlay;
import net.sauce.spotifyintegrationmod.event.KeyInputHandler;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;
import net.sauce.spotifyintegrationmod.spotify.threads.AuthThread;
import net.sauce.spotifyintegrationmod.spotify.threads.DataThread;

import java.io.IOException;

public class SpotifyIntegrationModClient implements ClientModInitializer {
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
