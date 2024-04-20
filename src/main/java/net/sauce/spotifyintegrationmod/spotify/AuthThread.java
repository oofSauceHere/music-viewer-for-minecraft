package net.sauce.spotifyintegrationmod.spotify;

public class AuthThread implements Runnable {
    @Override
    public void run() {
        try {
            // Checks every second for an access token
            while (SpotifyServer.props.getProperty("ACCESS_TOKEN", null) == null) {
                Thread.sleep(1000);
            }

            // Once we finally have one, we can report that it exists and close the server.
            SpotifyServer.authorized = true;
            SpotifyServer.closeServer();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
