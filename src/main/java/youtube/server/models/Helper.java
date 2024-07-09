package youtube.server.models;

import youtube.SortTypes;

public class Helper {
    private static final SortTypes[] oTypes;

    static {
        oTypes = SortTypes.values();
    }

    public static SortTypes getOrderTypeFromInt(int n) {
        return oTypes[n];
    }

    public static String getVideoOrderFromInt(int n) throws Exception {
        switch (oTypes[n]) {
            case NEWEST -> {
                return " V.created_at DESC ";
            }
            case OLDEST -> {
                return " V.created_at ASC ";
            }
            case MOST_POPULAR -> {
                return " V.view_count DESC ";
            }
            case LEAST_POPULAR -> {
                return " V.view_count ASC ";
            }
        }
        throw new ModelError("order type:"+n+" is not allowed");
    }

    public static String getCommentOrderFromInt(int n) throws Exception {
        switch (oTypes[n]) {
            case NEWEST -> {
                return " C.created_at DESC ";
            }
            case OLDEST -> {
                return " C.created_at ASC ";
            }
            case MOST_LIKED -> {
                return " likes_count DESC ";
            }
            case LEAST_DISLIKED -> {
                return " dislikes_count ASC ";
            }
        }
        throw new ModelError("order type:"+n+" is not allowed");
    }

}
