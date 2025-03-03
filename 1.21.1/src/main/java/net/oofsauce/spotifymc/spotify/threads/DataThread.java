package net.oofsauce.spotifymc.spotify.threads;

import net.minecraft.client.MinecraftClient;
import net.oofsauce.spotifymc.client.SongHudOverlay;
import net.oofsauce.spotifymc.spotify.AuthServer;
import net.oofsauce.spotifymc.spotify.SpotifyAPI;

// This thread queries the Spotify API every second to see if the song has changed.
public class DataThread implements Runnable {
    @Override
    public void run() {
        while(true) {
            try {
                // The client must be open and an access token must have been acquired before attempting to get data
                if(AuthServer.authorized && MinecraftClient.getInstance() != null &&
                        MinecraftClient.getInstance().getTextureManager() != null && SongHudOverlay.showSong) {
                    SpotifyAPI.getData(); // This is not really conducive to synchronous programming
                }
                Thread.sleep(2000);
            } catch(Exception e) {
                // This catches any exception, which is probably not good practice. This time, however, I CARE about good practice,
                // so it'll be changed later.
                e.printStackTrace();
            }
        }
    }
}
