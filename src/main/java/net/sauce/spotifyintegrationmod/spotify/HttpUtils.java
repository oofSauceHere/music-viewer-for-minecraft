package net.sauce.spotifyintegrationmod.spotify;

import java.util.ArrayList;
import java.util.HashMap;

public class HttpUtils {
    // allows for cleaner code when stringify-ing query params
    public static String mapToQuery(HashMap<String, String> queryMap) {
        if(queryMap == null) return null;

        ArrayList<String> queries = new ArrayList<>();
        for(String key : queryMap.keySet()) {
            String value = queryMap.get(key);
            value = String.join("+", value.split(" "));
            queries.add(key + "=" + value);
        }

        return String.join("&", queries);
    }

    // allows for cleaner code when parsing queries from URI
    public static HashMap<String, String> queryToMap(String query) {
        if(query == null) return null;

        HashMap<String, String> queryMap = new HashMap<>();
        for(String param : query.split("&")) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];

            queryMap.put(name, value);
        }

        return queryMap;
    }

    // cryptographically random string for session purposes
    public static String randomString(int len) {
        return "hello chat";
    }
}
