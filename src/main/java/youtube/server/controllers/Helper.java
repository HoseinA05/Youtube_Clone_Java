package youtube.server.controllers;

import org.json.JSONObject;
import youtube.server.models.ModelError;
import org.json.JSONException;

import java.util.HashMap;

public class Helper {
    public static String errorToJson(Exception e) {
        JSONObject response = new JSONObject();
        if (e instanceof JSONException) {
            String m = "Invalid json error:" + e.getMessage().replaceAll("\"","`");
            response.put("error",m);
            // return String.format("{\"error\":\"%s\"}", m);
            return response.toString();
        }
        if (e instanceof ModelError) {
            String m = String.format("{\"error\":\"%s\"}", e.getMessage().replaceAll("\"","`"));
            response.put("error",m);
            return response.toString();
        }
        System.out.println("server error:");
        System.out.println(e.getMessage());
        e.printStackTrace();
        return "{\"error\":\"something went wrong in server\"}";
    }

    public static String messageToJson(String m) {
        return String.format("{\"message\":\"%s\"}", m);
    }

    public static String messageAndIdToJson(String m, int id) {
        return String.format("{\"message\":\"%s\",\"id\":%d}", m, id);
    }

    public static void validateAllowedKeys(String[] validKeys, String[] jsonKeys) throws Exception {
        HashMap<String, Boolean> keysMap = new HashMap<>();
        for (String validKey : validKeys) {
            keysMap.put(validKey, true);
        }
        for (String jsonKey : jsonKeys) {
            if (keysMap.get(jsonKey) == null) {
                throw new ModelError("key:`" + jsonKey + "` is not allowed.valid keys:(" + String.join(",", validKeys) + ")");
            }
        }
    }

    public static int validateInt(JSONObject body, String key) throws Exception {
        int n;
        if (!body.has(key)) {
            throw new ModelError("key:`" + key + "` is required");
        }
        try {
            n = body.getInt(key);
        } catch (JSONException e) {
            throw new ModelError("key:`" + key + "` is not a valid int");
        }
        return n;
    }

    public static int validatePositiveInt(JSONObject body, String key) throws Exception {
        int n;
        if (!body.has(key)) {
            throw new ModelError("key:`" + key + "` is required");
        }
        try {
            n = body.getInt(key);
        } catch (JSONException e) {
            throw new ModelError("key:`" + key + "` is not a valid int");
        }

        if (n <= 0) {
            throw new ModelError("key:`" + key + "` should be positive int");
        }
        return n;
    }

    public static boolean validateBoolean(JSONObject body, String key) throws Exception {
        boolean n;
        if (!body.has(key)) {
            throw new ModelError("key:`" + key + "` is required");
        }
        try {
            n = body.getBoolean(key);
        } catch (JSONException e) {
            throw new ModelError("key:`" + key + "` is not a valid boolean");
        }

        return n;
    }

    public static String validateRequiredString(JSONObject body, String key) throws Exception {
        String s;
        if (!body.has(key)) {
            throw new ModelError("key:`" + key + "` is required");
        }
        try {
            s = body.getString(key);
        } catch (JSONException e) {
            throw new ModelError("key:`" + key + "` is not a valid string");
        }

        if (s.isEmpty()) {
            throw new ModelError("key:`" + key + "` is empty");
        }
        return s;
    }

    public static String validateNonRequiredString(JSONObject body, String key) throws Exception {
        String s;
        try {
            s = body.getString(key);
        } catch (JSONException e) {
            throw new ModelError("key:`" + key + "` is not a valid string");
        }
        return s;
    }

    // private static final OrderTypes[] oTypes;
    //
    // static {
    //     oTypes = OrderTypes.values();
    // }
    //
    // public static OrderTypes validateOrderType(JSONObject body, String key) throws Exception {
    //     int n;
    //     if (!body.has(key)) {
    //         throw new ModelError("key:`" + key + "` is required");
    //     }
    //     try {
    //         n = body.getInt(key);
    //     } catch (JSONException e) {
    //         throw new ModelError("key:`" + key + "` is not a valid int");
    //     }
    //     OrderTypes t = oTypes[n];
    //     if (t == null) {
    //         throw new ModelError("key:`" + key + "` is not a valid OrderType");
    //     }
    //     return t;
    // }
}
