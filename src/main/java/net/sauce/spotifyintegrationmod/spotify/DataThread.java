package net.sauce.spotifyintegrationmod.spotify;

import net.minecraft.client.MinecraftClient;

// This thread queries the Spotify API every second to see if the song has changed.
public class DataThread implements Runnable {
    @Override
    public void run() {
        while(true) {
            try {
                // The client must be open and an access token must have been acquired before attempting to get data
                if(SpotifyServer.authorized && MinecraftClient.getInstance() != null && MinecraftClient.getInstance().getTextureManager() != null) {
                    SpotifyServer.getData();
                }
                Thread.sleep(1000);
            } catch(Exception e) {
                // This catches any exception, which is probably not good practice. This time, however, I CARE about good practice,
                // so it'll be changed later.
                e.printStackTrace();
            }
        }
    }
}
