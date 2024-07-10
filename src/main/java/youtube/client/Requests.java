package youtube.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import org.json.JSONObject;
import youtube.RequestTypes;
import youtube.server.models.Video;

import java.util.ArrayList;
import java.util.Map;

public class Requests {

    public static String createRequestContent(RequestTypes rt, JSONObject body) {
        String request = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", rt.ordinal());

        jsonObject.put("body", body);
        request = jsonObject.toString();
        return request;
    }

    public static Task<Response> GET_USERINFO(int userId, int currentUserId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId)

                ));
                if (currentUserId != 0) {
                    body.put("current_user_id", currentUserId);
                }
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_USERINFO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SIGN_IN(String username, String password) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("username", username),
                        Map.entry("password", password)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SIGN_IN, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> FOLLOW(int userId, int followingId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("following_user_id", followingId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.FOLLOW, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> UNFOLLOW(int userId, int followingId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("following_user_id", followingId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.UNFOLLOW, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SET_USER_PROFILE_PHOTO(int userId, String channelPhoto) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("channelPhoto", channelPhoto),
                        Map.entry("user_id", userId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_USER_PROFILE_PHOTO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SET_USER_CHANNEL_PHOTO(String profilePhoto, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("profilePhoto", profilePhoto),
                        Map.entry("user_id", userId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_USER_CHANNEL_PHOTO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> EDIT_USER(String newUsername, String newPassword, String newAboutMe, int userId, String oldPassword) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("newUsername", newUsername),
                        Map.entry("newPassword", newPassword),
                        Map.entry("newAboutMe", newAboutMe),
                        Map.entry("user_id", userId),
                        Map.entry("oldPassword", oldPassword)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.EDIT_USER, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> CREATE_USER(String email, String username, String password) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("email", email),
                        Map.entry("username", username),
                        Map.entry("password", password)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.CREATE_USER, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_USER(int userId, String password) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("password", password)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_USER, body));
                return new Response(response);
            }
        };
    }


    public static Task<Response> GET_VIDEO_BY_ID(int videoId, int currentUserId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("video_id", videoId)
                ));
                if (currentUserId != 0) {
                    body.put("current_user_id", currentUserId);
                }
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_VIDEO_BY_ID, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_USER_VIDEOS(int sortType, int userId, int perpage, int page) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("sort_by", sortType),
                        Map.entry("user_id", userId),
                        Map.entry("perpage", perpage),
                        Map.entry("page", page)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_USER_VIDEOS, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_NEW_VIDEOS(int page, int perpage) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {

                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("page", page),
                        Map.entry("perpage", perpage)
                ));

                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_NEW_VIDEOS, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS(int userId, int page, int perpage) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {

                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("page", page),
                        Map.entry("perpage", perpage)
                ));

                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_TRENDING_VIDEOS(int page, int perpage) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("page", page),
                        Map.entry("perpage", perpage)
                ));

                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_TRENDING_VIDEOS, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_RELATED_VIDEOS(int videoId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                // To be created
                return null;
            }
        };
    }

    public static Task<Response> GET_VIDEOS_OF_TAG(int page, int perpage, int tagId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("tag_id", tagId),
                        Map.entry("page", page),
                        Map.entry("perpage", perpage)
                ));

                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_VIDEOS_OF_TAG, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> CREATE_VIDEO(String title, String description, String video, boolean isPrivate, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("title", title),
                        Map.entry("description", description),
                        Map.entry("video", video),
                        Map.entry("isPrivate", isPrivate),
                        Map.entry("userId", userId)
                ));

                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_VIDEOS_OF_TAG, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SET_VIDEO_VISIBILITY(int videoId, int userId, boolean isPrivate) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("videoId", videoId),
                        Map.entry("userId", userId),
                        Map.entry("isPrivate", isPrivate)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_VIDEO_VISIBILITY, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SET_VIDEO_THUMBNAIL(int videoId, int userId, String thumbnail) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("videoId", videoId),
                        Map.entry("userId", userId),
                        Map.entry("thumbnail", thumbnail)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_VIDEO_THUMBNAIL, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> EDIT_VIDEO(String newTitle, String newDescription, int videoId, int userId, boolean isPrivate) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("newTitle", newTitle),
                        Map.entry("newDescription", newDescription),
                        Map.entry("video_id", videoId),
                        Map.entry("user_id", userId),
                        Map.entry("isPrivate", isPrivate)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.EDIT_VIDEO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_VIDEO(int videoId, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("video_id", videoId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_VIDEO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> PUT_VIDEO_LIKE(int videoId, int userId, boolean isLike) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("isLike", isLike),
                        Map.entry("user_id", userId),
                        Map.entry("video_id", videoId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.PUT_VIDEO_LIKE, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_VIDEO_LIKE(int videoId, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", userId),
                        Map.entry("video_id", videoId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_VIDEO_LIKE, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> PUT_VIDEO_TAG(int videoId, int userId, String tagName) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("tagName", tagName),
                        Map.entry("user_id", userId),
                        Map.entry("video_id", videoId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.PUT_VIDEO_TAG, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_VIDEO_TAG(int videoId, int userId, String tagName) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("tagName", tagName),
                        Map.entry("user_id", userId),
                        Map.entry("video_id", videoId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_VIDEO_TAG, body));
                return new Response(response);
            }
        };
    }

    ///////////// playlists

    public static Task<Response> GET_PLAYLIST_ID(String playlistName, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_name", playlistName),
                        Map.entry("user_id", userId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_PLAYLIST_ID, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_PLAYLIST(int playlistId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> CREATE_PLAYLIST(String name, String text, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("name", name),
                        Map.entry("text", text),
                        Map.entry("user_id", userId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.CREATE_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> SET_PLAYLIST_THUMBNAIL(String thumbnail, int playlistId, int userId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("thumbnail", thumbnail),
                        Map.entry("user_id", userId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_PLAYLIST_THUMBNAIL, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> EDIT_PLAYLIST(String newName, String newText, int user_id, int playlistId) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("newName", newName),
                        Map.entry("newText", newText),
                        Map.entry("user_id", user_id),
                        Map.entry("playlist_id", playlistId)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.EDIT_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_PLAYLIST(int playlistId, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> PUT_ADMIN_PLAYLIST(int playlistId, int user_id, int collaborator_user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("user_id", user_id),
                        Map.entry("collaborator_user_id", collaborator_user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.PUT_ADMIN_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_ADMIN_PLAYLIST(int playlistId, int user_id, int collaborator_user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("user_id", user_id),
                        Map.entry("collaborator_user_id", collaborator_user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_ADMIN_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> PUT_VIDEO_PLAYLIST(int playlistId, int custom_order, int video_id, int collaborator_user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("custom_order", custom_order),
                        Map.entry("video_id", video_id),
                        Map.entry("collaborator_user_id", collaborator_user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.PUT_VIDEO_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> EDIT_VIDEO_PLAYLIST(int playlistId, int video_id, int collaborator_user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("video_id", video_id),
                        Map.entry("collaborator_user_id", collaborator_user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.EDIT_VIDEO_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_VIDEO_PLAYLIST(int playlistId, int video_id, int collaborator_user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("video_id", video_id),
                        Map.entry("collaborator_user_id", collaborator_user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_VIDEO_PLAYLIST, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_PLAYLIST_VIDEOS(int playlistId, int perpage, int page) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("playlist_id", playlistId),
                        Map.entry("perpage", perpage),
                        Map.entry("page", page)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_PLAYLIST_VIDEOS, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_USER_PLAYLIST(int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_USER_PLAYLIST, body));
                return new Response(response);
            }
        };
    }


    //////////////////////// comments

    public static Task<Response> GET_COMMENTS_OF_VIDEO(int video_id, int current_user_id, int perpage, int page, int sort_by) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("video_id", video_id),
                        Map.entry("perpage", perpage),
                        Map.entry("page", page),
                        Map.entry("sort_by", sort_by)
                ));
                if (current_user_id != 0) {
                    body.put("current_user_id", current_user_id);
                }
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_COMMENTS_OF_VIDEO, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> GET_REPLIES_OF_COMMENT(int comment_id, int current_user_id, int perpage, int page, int sort_by) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("comment_id", comment_id),
                        Map.entry("perpage", perpage),
                        Map.entry("page", page),
                        Map.entry("sort_by", sort_by)
                ));
                if (current_user_id != 0) {
                    body.put("current_user_id", current_user_id);
                }
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_REPLIES_OF_COMMENT, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> CREATE_COMMENT(String text, int reply_id, int video_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("text", text),
                        Map.entry("video_id", video_id),
                        Map.entry("user_id", user_id)
                ));
                if (reply_id != 0) {
                    body.put("reply_id", reply_id);
                }
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.CREATE_COMMENT, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> EDIT_COMMENT(String text, int comment_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("text", text),
                        Map.entry("comment_id", comment_id),
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.EDIT_COMMENT, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_COMMENT(int comment_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("comment_id", comment_id),
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_COMMENT, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> PUT_COMMENT_LIKE(boolean isLike, int comment_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("isLike", isLike),
                        Map.entry("comment_id", comment_id),
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.PUT_COMMENT_LIKE, body));
                return new Response(response);
            }
        };
    }

    public static Task<Response> DELETE_COMMENT_LIKE(int comment_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("comment_id", comment_id),
                        Map.entry("user_id", user_id)
                ));
                String response = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_COMMENT_LIKE, body));
                return new Response(response);
            }
        };
    }

    ///////// hitory


    public static Task<Response> GET_HISTORIES(int user_id, int perpage, int page) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", user_id),
                        Map.entry("perpage", perpage),
                        Map.entry("page", page)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_HISTORIES, body));
                return new Response(r);
            }
        };
    }

    public static Task<Response> DELETE_HISTORY(int history_id, int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("history_id", history_id),
                        Map.entry("user_id", user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_HISTORY, body));
                return new Response(r);
            }
        };
    }

    public static Task<Response> DELETE_ALL_USER_HISTORY(int user_id) {
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id", user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_ALL_USER_HISTORY, body));
                return new Response(r);
            }
        };
    }

    ///////// notifications


    public static Task<Response> GET_UNSEEN_NOTIFICATION_COUNT(int user_id){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id",user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_UNSEEN_NOTIFICATION_COUNT,body));
                return new Response(r);
            }
        };
    }
    public static Task<Response> GET_UNSEEN_NOTIFICATIONS(int user_id,int perpage,int page){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id",user_id),
                        Map.entry("page",page),
                        Map.entry("perpage",perpage)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.GET_UNSEEN_NOTIFICATIONS,body));
                return new Response(r);
            }
        };
    }
    public static Task<Response> SET_NOTIFICATION_SEEN(int notification_id,int user_id){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("notification_id",notification_id),
                        Map.entry("user_id",user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_NOTIFICATION_SEEN,body));
                return new Response(r);
            }
        };
    }
    public static Task<Response> SET_ALL_NOTIFICATION_SEEN(int user_id){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id",user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.SET_ALL_NOTIFICATION_SEEN,body));
                return new Response(r);
            }
        };
    }
    public static Task<Response> DELETE_NOTIFICATION(int notification_id,int user_id){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("notification_id",notification_id),
                        Map.entry("user_id",user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_NOTIFICATION,body));
                return new Response(r);
            }
        };
    }
    public static Task<Response> DELETE_ALL_NOTIFICATION(int user_id){
        return new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                JSONObject body = new JSONObject(Map.ofEntries(
                        Map.entry("user_id",user_id)
                ));
                String r = RequestHandler.sendRequest(createRequestContent(RequestTypes.DELETE_ALL_NOTIFICATION,body));
                return new Response(r);
            }
        };
    }
}
