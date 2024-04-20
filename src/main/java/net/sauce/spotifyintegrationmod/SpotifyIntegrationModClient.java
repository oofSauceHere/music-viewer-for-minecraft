package net.sauce.spotifyintegrationmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.sauce.spotifyintegrationmod.client.SongHudOverlay;
import net.sauce.spotifyintegrationmod.event.KeyInputHandler;
import net.sauce.spotifyintegrationmod.spotify.DataThread;

public class SpotifyIntegrationModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Start fetching data from Spotify's API
        new Thread(new DataThread()).start();

        // Register keybinds and begin rendering HUD.
        KeyInputHandler.register();
        HudRenderCallback.EVENT.register(new SongHudOverlay());
    }
}
