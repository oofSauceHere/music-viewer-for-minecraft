package net.sauce.spotifyintegrationmod.spotify.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import net.sauce.spotifyintegrationmod.spotify.AuthServer;
import net.sauce.spotifyintegrationmod.spotify.ServerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;

public class RefreshTokenHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String queryString = exchange.getRequestURI().getQuery();
        HashMap<String, String> queryMap = ServerUtils.queryToMap(queryString);

        String refreshToken = queryMap.get("refresh_token");

        HashMap<String, String> authQueryMap = new HashMap<>();
        authQueryMap.put("grant_type", "refresh_token");
        authQueryMap.put("refresh_token", refreshToken);

        String url = "https://accounts.spotify.com/api/token";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String authProps = AuthServer.props.getProperty("CLIENT_ID") + ":" + AuthServer.props.getProperty("CLIENT_SECRET");
        conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authProps.getBytes()));
        conn.setDoOutput(true);

        OutputStream connOs = conn.getOutputStream();
        connOs.write(ServerUtils.mapToQuery(authQueryMap).getBytes());
        connOs.flush();
        connOs.close();

        int responseCode = conn.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer responseBody = new StringBuffer();
            while ((line = in.readLine()) != null) {
                responseBody.append(line);
                responseBody.append("\n");
            }
            in.close();

            String[] pairs = responseBody.toString().replace("{", "").replace("}", "").replace("\"", "").split(",");
            for(String i : pairs) {
                String[] pair = i.split(":");
                AuthServer.props.setProperty(pair[0].toUpperCase(), pair[1]);
            }

            conn.disconnect();
        }

        exchange.sendResponseHeaders(200, 0);
    }
}
