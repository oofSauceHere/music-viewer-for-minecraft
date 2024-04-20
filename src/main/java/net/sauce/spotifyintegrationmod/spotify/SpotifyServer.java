package net.sauce.spotifyintegrationmod.spotify;

import com.sun.net.httpserver.HttpServer;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.sauce.spotifyintegrationmod.SpotifyIntegrationMod;
import net.sauce.spotifyintegrationmod.spotify.http.LoginHandler;
import net.sauce.spotifyintegrationmod.spotify.http.CallbackHandler;
import net.sauce.spotifyintegrationmod.spotify.http.RefreshTokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import javax.imageio.ImageIO;

public class SpotifyServer {
    // should these be public??
    public static Properties props;
    public static Map<String, String> songData;
    public static Map<Identifier, NativeImage> playlistCovers;
    public static Identifier currentId;
    public static HttpServer server;

    // half of these shouldnt be here
    public static int showSong = -1;
    public static boolean authorized = false;

    // It's in the name
    public static void runServer() throws IOException {
        // perhaps these shouldnt be in a function
        songData = new HashMap<>();
        playlistCovers = new HashMap<>();
        currentId = null;

        // Creates an HTTP server and handles routing (well, technically doles out the handling roles to 3 other functions)
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new LoginHandler());
        server.createContext("/callback", new CallbackHandler());
        server.createContext("/refresh_token", new RefreshTokenHandler());

        // Initializes properties object to store app properties for easy access in other classes
        props = new Properties();
        String path = new File("C:/Users/dwman/Desktop/Coding/Spotify Integration Mod 1.20.4/src/main/resources/app.properties").getAbsolutePath();
        props.load(new FileInputStream(path));

        System.out.println("Server starting...");
        server.start();
    }

    // I think this one's pretty clear too
    public static void closeServer() {
        server.stop(0);
    }

    // Queries the Spotify API for currently playing track and stores the data for use by other classes/functions
    public static void getData() throws MalformedURLException, ProtocolException, IOException {
        // Creates connection to API endpoint
        String endpoint = "https://api.spotify.com/v1/me/player/currently-playing";
        HttpURLConnection getConn = (HttpURLConnection) new URL(endpoint).openConnection();
        getConn.setRequestMethod("GET");
        getConn.setRequestProperty("Authorization", "Bearer " + SpotifyServer.props.getProperty("ACCESS_TOKEN"));
        getConn.setDoOutput(true);

        // If the response is good, we've recieved valuable data
        int getResponseCode = getConn.getResponseCode();
        if (getResponseCode == HttpURLConnection.HTTP_OK) {
            // Responsible for reading in the response data
            BufferedReader in = new BufferedReader(new InputStreamReader(getConn.getInputStream()));
            String line;
            StringBuffer responseBody = new StringBuffer();
            while ((line = in.readLine()) != null) {
                responseBody.append(line);
            }
            in.close();

            // Uses jackson-databind to parse through the json response. I would have done this myself (as a learning thing, I'm trying not to depend on,
            // well, dependencies so I can get a strong foundation) but parsing is a Demonic Task so here's this instead.
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody.toString());
            Iterator<JsonNode> elements = root.path("item").path("album").path("images").elements();

            // This project only cares about the song's image, title, and artist name. The id is included for reference later.
            String imageUrl = elements.next().get("url").toString().replace("\"", "");
            String artistName = root.path("item").path("artists").elements().next().get("name").toString().replace("\"", "");
            String songName = root.path("item").get("name").toString().replace("\"", "");
            String songId = root.path("item").get("id").toString().replace("\"", "");

            // We're done with the HTTP connection now
            getConn.disconnect();

            // Stores the data in a map for easy access elsewhere
            songData.put("imageUrl", imageUrl);
            songData.put("artistName", artistName);
            songData.put("songName", songName);
            songData.put("songId", songId);

            // If the current song is the same as the previous song (likely happens often if we're polling every second), don't bother updating things
            Identifier id = new Identifier(SpotifyIntegrationMod.MOD_ID, songId.toLowerCase());
            if(id.equals(currentId)) return;

            currentId = id;
            if(!playlistCovers.containsKey(id)) {
                // Annoyingly, spotify stores album covers as jpgs, but we can work around that
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                BufferedImage jpegImage = ImageIO.read(new URL(imageUrl).openStream());
                ImageIO.write(jpegImage, "png", os);

                // Read in the png image and register it with a corresponding ID so it can be drawn via ID reference in the HUD.
                NativeImage art = NativeImage.read(new ByteArrayInputStream(os.toByteArray()));
                playlistCovers.put(id, art);
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(art));
            }
        } else {
            // If we don't do this, we won't know when a song ISN'T playing.
            songData.put("imageUrl", null);
            songData.put("artistName", null);
            songData.put("songName", null);
            currentId = null;
        }
    }

    // This isn't done. Or at least it's never used. But it should be used.
    public static void getRefreshToken() throws MalformedURLException, ProtocolException, IOException{
        String endpoint = "http://localhost:8080/refresh_token";
        HttpURLConnection getConn = (HttpURLConnection) new URL(endpoint).openConnection();
        getConn.setRequestMethod("GET");
        getConn.setDoOutput(true);

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("refresh_token", props.getProperty("REFRESH_TOKEN"));

        OutputStream getConnOs = getConn.getOutputStream();
        getConnOs.write(HttpUtils.mapToQuery(queryMap).getBytes());

        int getResponseCode = getConn.getResponseCode();
        System.out.println(getResponseCode);
        getConn.disconnect();
    }
}
