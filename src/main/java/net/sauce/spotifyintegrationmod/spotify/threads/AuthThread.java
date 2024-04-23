package net.sauce.spotifyintegrationmod.spotify.threads;

import net.minecraft.client.MinecraftClient;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;

public class AuthThread implements Runnable {
    @Override
    public void run() {
        try {
            // Checks every second for an access token.
            while (AuthServer.props.getProperty("ACCESS_TOKEN", null) == null) {
                Thread.sleep(1000);
            }

            // Once we finally have one, we can report that it exists.
            AuthServer.authorized = true;

            // We still have to monitor the time so we can refresh the access token as needed.
            while(true) { // this method may not be foolproof because threads are imprecise
                if(System.currentTimeMillis() > AuthServer.expiryTime-10000) {
                    AuthServer.getRefreshToken();
                    System.out.println("Token refreshed!");
                }

                Thread.sleep(1000);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
