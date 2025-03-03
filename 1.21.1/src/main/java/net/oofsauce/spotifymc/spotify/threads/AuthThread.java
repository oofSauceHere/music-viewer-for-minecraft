package net.oofsauce.spotifymc.spotify.threads;

import net.minecraft.client.MinecraftClient;
import net.oofsauce.spotifymc.spotify.AuthServer;

// People need to be able to remove their credentials from the game (use props.remove)
public class AuthThread implements Runnable {
    @Override
    public void run() {
        try {
            // Checks every second for an access token.
            String oldToken = AuthServer.props.getProperty("ACCESS_TOKEN", null);
            if(AuthServer.props.getProperty("REFRESH_TOKEN", null) != null) {
                AuthServer.refreshToken();
            }

            while (AuthServer.props.getProperty("ACCESS_TOKEN", null) == null ||
                    AuthServer.props.getProperty("ACCESS_TOKEN", null).equals(oldToken)) {
                Thread.sleep(1000);
            }

            // Once we finally have one, we can report that it exists.
            AuthServer.authorized = true;

            // We still have to monitor the time so we can refresh the access token as needed.
            while(true) {
                if(System.currentTimeMillis() > AuthServer.expiryTime-15000) {
                    AuthServer.refreshToken();
                    System.out.println("Token refreshed!");
                }

                Thread.sleep(1000);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
