package net.sauce.spotifyintegrationmod.spotify;

import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Properties;

import net.sauce.spotifyintegrationmod.spotify.server.LoginHandler;
import net.sauce.spotifyintegrationmod.spotify.server.CallbackHandler;
import net.sauce.spotifyintegrationmod.spotify.server.RefreshTokenHandler;

public class AuthServer {
    // Should these be public??
    public static Properties props;
    public static HttpServer server;
    public static boolean authorized = false;
    public static boolean serverStarted = false;

    // It's in the name
    public static void runServer() throws IOException {
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
        serverStarted = true;
    }

    public static void getRefreshToken() throws MalformedURLException, ProtocolException, IOException {
        // Queries for get requests are embedded in the url, while they're sent in the body for post requests.
        String endpoint = "http://localhost:8080/refresh_token";
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("refresh_token", props.getProperty("REFRESH_TOKEN"));

        HttpURLConnection getConn = (HttpURLConnection) new URL(endpoint + "?" + ServerUtils.mapToQuery(queryMap)).openConnection();
        getConn.setRequestMethod("GET");
        getConn.setDoOutput(true);

        int getResponseCode = getConn.getResponseCode();
        // Should throw an error, probably.
        if(getResponseCode != HttpURLConnection.HTTP_OK) System.err.println("Unable to get refresh token");
        getConn.disconnect();
    }
}
