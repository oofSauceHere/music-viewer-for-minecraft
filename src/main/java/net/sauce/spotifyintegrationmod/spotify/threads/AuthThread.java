package net.sauce.spotifyintegrationmod.spotify.threads;

import net.sauce.spotifyintegrationmod.spotify.AuthServer;

public class AuthThread implements Runnable {
    public static int currentTime = 0;
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
            int expiryTime = AuthServer.props.getProperty("EXPIRES_IN") == null ?
                    Integer.parseInt(AuthServer.props.getProperty("EXPIRES_IN")) : -1;
            while(true) { // this method may not be foolproof because threads are imprecise
                if(expiryTime != -1 && currentTime >= expiryTime-5) {
                    currentTime = 0;
                    AuthServer.getRefreshToken();
                    expiryTime = Integer.parseInt(AuthServer.props.getProperty("EXPIRES_IN"));
                }

                Thread.sleep(1000);
                currentTime++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
