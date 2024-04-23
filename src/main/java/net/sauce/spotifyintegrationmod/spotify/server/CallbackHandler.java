package net.sauce.spotifyintegrationmod.spotify.server;

import com.sun.net.httpserver.Headers;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class CallbackHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // consider how to properly handle exceptions and such
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
        responseHeaders.add("Access-Control-Allow-Credentials", "true");
        responseHeaders.add("Access-Control-Allow-Methods", "GET");

        String queryString = exchange.getRequestURI().getQuery();
        HashMap<String, String> queryMap = ServerUtils.queryToMap(queryString);

        String code = queryMap.get("code");
        String state = String.join(" ", queryMap.get("state").split("\\+"));

        Headers requestHeaders = exchange.getRequestHeaders();
        List<String> cookies = requestHeaders.get("Cookie");
        String storedState = null;
        for(String cookie : cookies) {
            String[] pair = cookie.split("=");
            if(pair[0].equals("spotify_auth_state") && pair.length == 2) {
                storedState = cookie.split("=")[1];
                break;
            }
        }

        if(storedState == null || !storedState.equals(state)) {
            String location = "/#";
            location += "error=state_mismatch";
            responseHeaders.set("Location", location);

            String response = "<html><body><h1>State mismatch</h1></body></html>";
            exchange.sendResponseHeaders(302, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        } else {
            HashMap<String, String> authQueryMap = new HashMap<>();
            authQueryMap.put("code", code);
            authQueryMap.put("redirect_uri", AuthServer.props.getProperty("REDIRECT_URI"));
            authQueryMap.put("grant_type", "authorization_code");

            String endpoint = "https://accounts.spotify.com/api/token";
            HttpURLConnection postConn = (HttpURLConnection) new URL(endpoint).openConnection();
            postConn.setRequestMethod("POST");
            postConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String authProps = AuthServer.props.getProperty("CLIENT_ID") + ":" + AuthServer.props.getProperty("CLIENT_SECRET");
            postConn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(authProps.getBytes()));
            postConn.setDoOutput(true);

            OutputStream postConnOs = postConn.getOutputStream();
            postConnOs.write(ServerUtils.mapToQuery(authQueryMap).getBytes());
            postConnOs.flush();
            postConnOs.close();

            int postResponseCode = postConn.getResponseCode();
            if(postResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(postConn.getInputStream()));
                String line;
                StringBuffer responseBody = new StringBuffer();
                while((line = in.readLine()) != null) {
                    responseBody.append(line);
                }
                in.close();

                // we want to parse the json here
                String[] pairs = responseBody.toString().replace("{", "").replace("}", "").replace("\"", "").split(",");
                for(String i : pairs) {
                    String[] pair = i.split(":");
                    AuthServer.props.setProperty(pair[0].toUpperCase(), pair[1]);
                    if(pair[0].toUpperCase().equals("EXPIRES_IN")) {
                        AuthServer.expiryTime = System.currentTimeMillis() + Long.parseLong(pair[1])*1000;
                    }
                }

                postConn.disconnect();
            }

            String response = "<html><body><h1>go away</h1></body></html>";
            exchange.sendResponseHeaders(200, response.length());
            responseHeaders.set("Set-Cookie", "spotify_auth_state=" + state + ";expires=" + Instant.now().minus(1, ChronoUnit.DAYS).toString());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.flush();
            os.close();
        }
    }
}
