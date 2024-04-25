package net.sauce.spotifyintegrationmod.spotify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.sauce.spotifyintegrationmod.SpotifyIntegrationMod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SpotifyAPI {
    public static Map<String, String> songData = new HashMap<>();
    public static Map<Identifier, NativeImage> playlistCovers = new HashMap<>();
    public static Identifier currentId = null;

    // Queries the Spotify API for currently playing track and stores the data for use by other classes/functions
    public static void getData() throws MalformedURLException, ProtocolException, IOException {
        if(!AuthServer.serverStarted) return;

        // Creates connection to API endpoint
        String endpoint = "https://api.spotify.com/v1/me/player/currently-playing?additional_types=episode";

        HttpURLConnection getConn = (HttpURLConnection) new URL(endpoint).openConnection();
        getConn.setRequestMethod("GET");
        getConn.setRequestProperty("Authorization", "Bearer " + AuthServer.props.getProperty("ACCESS_TOKEN"));
        getConn.setDoOutput(true);

        // If the response is good, we've recieved valuable data
        int getResponseCode = getConn.getResponseCode();
        // System.out.println("Response Code: " + getResponseCode);
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
            String type = root.get("currently_playing_type").toString().replace("\"", "");

            // This project only cares about the song's image, title, and artist name. The id is included for reference later.
            Iterator<JsonNode> elements = type.equals("track") ?
                    root.path("item").path("album").path("images").elements() :
                    root.path("item").path("show").path("images").elements();
            String imageUrl = elements.hasNext() ? elements.next().get("url").toString().replace("\"", "") : null;
            String artistName = type.equals("track") ?
                    root.path("item").path("artists").elements().next().get("name").toString().replace("\"", ""):
                    root.path("item").path("show").get("name").toString().replace("\"", "");
            String songName = root.path("item").get("name").toString().replace("\"", "");
            String songId = root.path("item").get("id").toString().replace("\"", "");
            String songDuration = root.path("item").get("duration_ms").toString().replace("\"", "");
            String progress = root.get("progress_ms").toString().replace("\"", "");

            // We're done with the HTTP connection now
            getConn.disconnect();

            // Stores the data in a map for easy access elsewhere
            songData.put("type", type);
            songData.put("imageUrl", imageUrl);
            songData.put("artistName", artistName);
            songData.put("songName", songName);
            songData.put("songId", songId);
            songData.put("songDuration", songDuration);
            songData.put("progress", progress);

            // If the current song is the same as the previous song (likely happens often if we're polling every second), don't bother updating things
            Identifier id = new Identifier(SpotifyIntegrationMod.MOD_ID, songId.toLowerCase());
            if(id.equals(currentId)) return;

            currentId = id;
            if(!playlistCovers.containsKey(id) && imageUrl != null) {
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
            songData.replaceAll((i, v) -> null);
            currentId = null;
        }
    }
}
