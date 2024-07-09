package youtube.server;

import org.json.JSONObject;

import youtube.RequestTypes;
import youtube.server.controllers.*;
import youtube.server.files.FileManager;

public class Router {
    private static final RequestTypes[] types;

    static {
        types = RequestTypes.values();
    }

    public String handleRequest(String request) throws Exception {
        System.out.println("Request received: " + request);

        JSONObject json = new JSONObject(request);
        RequestTypes type = types[json.getInt("type")];
        JSONObject body = json.getJSONObject("body");

        return switch (type) {
            /// users
            case GET_USERINFO -> new UserController().GET_USERINFO(body);
            case SIGN_IN -> new UserController().SIGN_IN(body);
            case FOLLOW -> new UserController().FOLLOW(body);
            case UNFOLLOW -> new UserController().UNFOLLOW(body);
            case SET_USER_PROFILE_PHOTO -> new UserController().SET_USER_PROFILE_PHOTO(body);
            case SET_USER_CHANNEL_PHOTO -> new UserController().SET_USER_CHANNEL_PHOTO(body);
            case EDIT_USER -> new UserController().EDIT_USER(body);
            case CREATE_USER -> new UserController().CREATE_USER(body);
            case DELETE_USER -> new UserController().DELETE_USER(body);

            /// videos
            case GET_VIDEO_BY_ID -> new VideoController().GET_VIDEO_BY_ID(body);
            case GET_USER_VIDEOS -> new VideoController().GET_USER_VIDEOS(body);
            case GET_NEW_VIDEOS -> new VideoController().GET_NEW_VIDEOS(body);
            case GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS -> new VideoController().GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS(body);
            case GET_TRENDING_VIDEOS -> new VideoController().GET_TRENDING_VIDEOS(body);
            case GET_RELATED_VIDEOS -> new VideoController().GET_RELATED_VIDEOS(body);
            case GET_VIDEOS_OF_TAG -> new VideoController().GET_VIDEOS_OF_TAG(body);
            case CREATE_VIDEO -> new VideoController().CREATE_VIDEO(body);
            case SET_VIDEO_VISIBILITY -> new VideoController().SET_VIDEO_VISIBILITY(body);
            case SET_VIDEO_THUMBNAIL -> new VideoController().SET_VIDEO_THUMBNAIL(body);
            case EDIT_VIDEO -> new VideoController().EDIT_VIDEO(body);
            case DELETE_VIDEO -> new VideoController().DELETE_VIDEO(body);
            case PUT_VIDEO_LIKE -> new VideoController().PUT_VIDEO_LIKE(body);
            case DELETE_VIDEO_LIKE -> new VideoController().DELETE_VIDEO_LIKE(body);
            case PUT_VIDEO_TAG -> new VideoController().PUT_VIDEO_TAG(body);
            case DELETE_VIDEO_TAG -> new VideoController().DELETE_VIDEO_TAG(body);

            ///comments
            case GET_COMMENTS_OF_VIDEO -> new CommentController().GET_COMMENTS_OF_VIDEO(body);
            case GET_REPLIES_OF_COMMENT -> new CommentController().GET_REPLIES_OF_COMMENT(body);
            case CREATE_COMMENT -> new CommentController().CREATE_COMMENT(body);
            case EDIT_COMMENT -> new CommentController().EDIT_COMMENT(body);
            case DELETE_COMMENT -> new CommentController().DELETE_COMMENT(body);
            case PUT_COMMENT_LIKE -> new CommentController().PUT_COMMENT_LIKE(body);
            case DELETE_COMMENT_LIKE -> new CommentController().DELETE_COMMENT_LIKE(body);

            ///playlists
            case GET_PLAYLIST_ID -> new PlaylistController().GET_PLAYLIST_ID(body);
            case GET_PLAYLIST -> new PlaylistController().GET_PLAYLIST(body);
            case CREATE_PLAYLIST -> new PlaylistController().CREATE_PLAYLIST(body);
            case SET_PLAYLIST_THUMBNAIL -> new PlaylistController().SET_PLAYLIST_THUMBNAIL(body);
            case EDIT_PLAYLIST -> new PlaylistController().EDIT_PLAYLIST(body);
            case DELETE_PLAYLIST -> new PlaylistController().DELETE_PLAYLIST(body);
            case PUT_ADMIN_PLAYLIST -> new PlaylistController().PUT_ADMIN_PLAYLIST(body);
            case DELETE_ADMIN_PLAYLIST -> new PlaylistController().DELETE_ADMIN_PLAYLIST(body);
            case PUT_VIDEO_PLAYLIST -> new PlaylistController().PUT_VIDEO_PLAYLIST(body);
            case EDIT_VIDEO_PLAYLIST -> new PlaylistController().EDIT_VIDEO_PLAYLIST(body);
            case DELETE_VIDEO_PLAYLIST -> new PlaylistController().DELETE_VIDEO_PLAYLIST(body);
            case GET_PLAYLIST_VIDEOS -> new PlaylistController().GET_PLAYLIST_VIDEOS(body);
            case GET_USER_PLAYLIST -> new PlaylistController().GET_USER_PLAYLIST(body);

            /// histories
            case GET_HISTORIES -> new HistoryController().GET_HISTORIES(body);
            case DELETE_HISTORY -> new HistoryController().DELETE_HISTORY(body);
            case DELETE_ALL_USER_HISTORY -> new HistoryController().DELETE_ALL_USER_HISTORY(body);

            // notification
            case GET_UNSEEN_NOTIFICATION_COUNT -> new NotificationController().GET_UNSEEN_NOTIFICATION_COUNT(body);
            case GET_UNSEEN_NOTIFICATIONS -> new NotificationController().GET_UNSEEN_NOTIFICATIONS(body);
            case SET_NOTIFICATION_SEEN -> new NotificationController().SET_NOTIFICATION_SEEN(body);
            case SET_ALL_NOTIFICATION_SEEN -> new NotificationController().SET_ALL_NOTIFICATION_SEEN(body);
            case DELETE_NOTIFICATION -> new NotificationController().DELETE_NOTIFICATION(body);
            case DELETE_ALL_NOTIFICATION -> new NotificationController().DELETE_ALL_NOTIFICATION(body);
            /// photo,video
            case GET_PHOTO -> new FileManager().GET_PHOTO(body);
            case GET_VIDEO -> new FileManager().GET_VIDEO(body);
            // default -> throw new RuntimeException("Request type not found");
            default -> "{\"error\":\"Request type not found\"}";
        };
    }

}
