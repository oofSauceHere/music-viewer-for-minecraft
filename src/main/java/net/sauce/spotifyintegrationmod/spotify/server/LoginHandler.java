package net.sauce.spotifyintegrationmod.spotify.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;
import net.sauce.spotifyintegrationmod.spotify.ServerUtils;

import java.io.IOException;
import java.util.HashMap;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        String state = ServerUtils.randomString(16); // should be different!

        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("response_type", "code");
        queryMap.put("client_id", AuthServer.props.getProperty("CLIENT_ID"));
        queryMap.put("scope", "user-read-playback-state user-modify-playback-state user-read-currently-playing");
        queryMap.put("redirect_uri", AuthServer.props.getProperty("REDIRECT_URI"));
        queryMap.put("state", state);
        queryMap.put("show_dialog", "true");

        String location = "https://accounts.spotify.com/authorize?";
        location += ServerUtils.mapToQuery(queryMap);

        responseHeaders.set("Set-Cookie", "spotify_auth_state=" + state);
        responseHeaders.set("Location", location);

        exchange.sendResponseHeaders(302, 0);
    }
}
