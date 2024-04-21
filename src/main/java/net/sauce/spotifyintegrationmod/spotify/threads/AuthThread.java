package net.sauce.spotifyintegrationmod.spotify.threads;

import net.sauce.spotifyintegrationmod.spotify.AuthServer;

public class AuthThread implements Runnable {
    @Override
    public void run() {
        try {
            // Checks every second for an access token
            while (AuthServer.props.getProperty("ACCESS_TOKEN", null) == null) {
                Thread.sleep(1000);
            }

            // Once we finally have one, we can report that it exists and close the server.
            AuthServer.authorized = true;
            AuthServer.closeServer();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
