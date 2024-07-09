package youtube.server;

import org.json.JSONObject;
import youtube.SortTypes;
import youtube.RequestTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class Test {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 1234;
    private static Socket clientsSocket;
    private static PrintWriter outStream; // Output stream to send data to the server
    private static BufferedReader inpStream; // Input stream to receive data from the server

    public static void main(String[] args) {
        // Video v = new Video();
        // v.setId(2);
        // v.setTitle("Hello");
        // v.setDescription("TESING");
        // v.setCommentsCount(1242);
        //
        // System.out.println(new Gson().toJson(v));

        try {
            // Connect to server
            clientsSocket = new Socket(SERVER_IP, SERVER_PORT);

            outStream = new PrintWriter(clientsSocket.getOutputStream(), true);
            inpStream = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));
            System.out.println("[CLIENT] connected to server.");


            JSONObject requestJson = new JSONObject();

            // GET_USERINFO
            // requestJson.put("type", RequestTypes.GET_USERINFO.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id","6"),
            //         Map.entry("current_user_id","6")
            // )));

            // SIGN_IN
            // requestJson.put("type", RequestTypes.SIGN_IN.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("username","asdgfasd"),
            //         Map.entry("password","12345")
            // )));

            // FOLLOW
            // requestJson.put("type", RequestTypes.FOLLOW.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",2),
            //         Map.entry("following_user_id","1")
            // )));

            // UNFOLLOW
            // requestJson.put("type", RequestTypes.UNFOLLOW.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",1),
            //         Map.entry("following_user_id","2")
            // )));

            // CREATE_USER
            // requestJson.put("type", RequestTypes.CREATE_USER.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("email","asdfasdf"),
            //         Map.entry("username","asdfawef"),
            //         Map.entry("password","12345")
            // )));

            // DELETE_USER
            // requestJson.put("type", RequestTypes.DELETE_USER.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",6),
            //         Map.entry("password","12345")
            // )));

            ///////////// VIDEOS

            // CREATE_VIDEO
            // requestJson.put("type", RequestTypes.CREATE_VIDEO.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("title","32fawd"),
            //         Map.entry("description","3fasdf"),
            //         Map.entry("isPrivate","false"),
            //         Map.entry("video","adsd"),
            //         Map.entry("user_id",1)
            // )));

            // EDIT_VIDEO
            // requestJson.put("type", RequestTypes.EDIT_VIDEO.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("newTitle","asdf"),
            //         Map.entry("newDescription","asdf"),
            //         Map.entry("video_id","1"),
            //         Map.entry("user_id",4)
            // )));

            // DELETE_VIDEO
            // requestJson.put("type", RequestTypes.DELETE_VIDEO.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("video_id",8),
            //         Map.entry("user_id",4)
            // )));

            // GET_VIDEO_BY_ID
            // requestJson.put("type", RequestTypes.GET_VIDEO_BY_ID.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("video_id",8),
            //         Map.entry("current_user_id",4)
            // )));

            // GET_NEW_VIDEOS
            // requestJson.put("type", RequestTypes.GET_NEW_VIDEOS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // GET_TRENDING_VIDEOS
            // requestJson.put("type", RequestTypes.GET_TRENDING_VIDEOS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // GET_TRENDING_VIDEOS
            // requestJson.put("type", RequestTypes.GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",1),
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // GET_USER_VIDEOS
            // requestJson.put("type", RequestTypes.GET_USER_VIDEOS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("sort_by", SortTypes.LEAST_POPULAR.ordinal()),
            //         Map.entry("user_id",1),
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // GET_TRENDING_VIDEOS
            // requestJson.put("type", RequestTypes.GET_TRENDING_VIDEOS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // PUT_VIDEO_LIKE
            // requestJson.put("type", RequestTypes.PUT_VIDEO_LIKE.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("isLike",true),
            //         Map.entry("video_id",1),
            //         Map.entry("user_id",3)
            // )));

            // DELETE_VIDEO_LIKE
            // requestJson.put("type", RequestTypes.DELETE_VIDEO_LIKE.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("video_id",5),
            //         Map.entry("user_id",3)
            // )));

            // PUT_VIDEO_TAG
            requestJson.put("type", RequestTypes.PUT_VIDEO_TAG.ordinal());
            requestJson.put("body", new JSONObject(Map.ofEntries(
                    Map.entry("tagName","asdfasdfdsfa"),
                    Map.entry("video_id",1),
                    Map.entry("user_id",1)
            )));

            // DELETE_VIDEO_TAG
            // requestJson.put("type", RequestTypes.DELETE_VIDEO_TAG.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("tagName","dasdf"),
            //         Map.entry("video_id",5),
            //         Map.entry("user_id",1)
            // )));

            /////////// COMMENTS

            // GET_COMMENTS_OF_VIDEO
            // requestJson.put("type", RequestTypes.GET_COMMENTS_OF_VIDEO.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("video_id",9),
            //         Map.entry("current_user_id",1),
            //         Map.entry("perpage",10),
            //         Map.entry("page",1),
            //         Map.entry("sort_by",SortTypes.MOST_LIKED.ordinal())
            // )));

            // GET_REPLIES_OF_COMMENT
            // requestJson.put("type", RequestTypes.GET_REPLIES_OF_COMMENT.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("comment_id",2),
            //         Map.entry("current_user_id",1),
            //         Map.entry("perpage",10),
            //         Map.entry("page",1)
            // )));

            // CREATE_COMMENT or reply
            // requestJson.put("type", RequestTypes.CREATE_COMMENT.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("text","asdf"),
            //         Map.entry("video_id",3),
            //         Map.entry("user_id",2),
            //         Map.entry("reply_id",1)
            // )));

            // EDIT_COMMENT or reply
            // requestJson.put("type", RequestTypes.EDIT_COMMENT.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("text","asdf"),
            //         Map.entry("user_id",10),
            //         Map.entry("comment_id",321)
            // )));

            // DELETE_COMMENT
            // requestJson.put("type", RequestTypes.DELETE_COMMENT.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",10),
            //         Map.entry("comment_id",2)
            // )));

            // PUT_COMMENT_LIKE
            // requestJson.put("type", RequestTypes.PUT_COMMENT_LIKE.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("isLike",true),
            //         Map.entry("user_id",3),
            //         Map.entry("comment_id",1)
            // )));

            // DELETE_COMMENT_LIKE
            // requestJson.put("type", RequestTypes.DELETE_COMMENT_LIKE.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",10),
            //         Map.entry("comment_id",3)
            // )));

            /////////// PLAYLISTS

            // GET_PLAYLIST
            // requestJson.put("type", RequestTypes.GET_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id",1)
            // )));

            // GET_USER_PLAYLIST
            // requestJson.put("type", RequestTypes.GET_USER_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id",3)
            // )));

            // CREATE_PLAYLIST
            // requestJson.put("type", RequestTypes.CREATE_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("name","asd"),
            //         Map.entry("text","asdasd"),
            //         Map.entry("user_id",2)
            // )));

            // EDIT_PLAYLIST
            // requestJson.put("type", RequestTypes.EDIT_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("newName","jkgfsgfd"),
            //         Map.entry("newText","12321442213"),
            //         Map.entry("thumbnail","asd"),
            //         Map.entry("playlist_id",7),
            //         Map.entry("user_id",2)
            // )));

            // DELETE_PLAYLIST
            // requestJson.put("type", RequestTypes.DELETE_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","8"),
            //         Map.entry("user_id",2)
            // )));

            // PUT_ADMIN_PLAYLIST
            // requestJson.put("type", RequestTypes.PUT_ADMIN_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","2"),
            //         Map.entry("user_id",2),
            //         Map.entry("collaborator_user_id","1")
            // )));

            // DELETE_ADMIN_PLAYLIST
            // requestJson.put("type", RequestTypes.DELETE_ADMIN_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","2"),
            //         Map.entry("user_id",2),
            //         Map.entry("collaborator_user_id","1")
            // )));

            // PUT_VIDEO_PLAYLIST
            // requestJson.put("type", RequestTypes.PUT_VIDEO_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","2"),
            //         Map.entry("custom_order","5123"),
            //         Map.entry("video_id",6),
            //         Map.entry("collaborator_user_id",4)
            // )));

            // EDIT_VIDEO_PLAYLIST
            // requestJson.put("type", RequestTypes.EDIT_VIDEO_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","7"),
            //         Map.entry("new_custom_order","3"),
            //         Map.entry("video_id",4),
            //         Map.entry("collaborator_user_id",1)
            // )));

            // DELETE_VIDEO_PLAYLIST
            // requestJson.put("type", RequestTypes.DELETE_VIDEO_PLAYLIST.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("playlist_id","2"),
            //         Map.entry("video_id",6),
            //         Map.entry("collaborator_user_id",2)
            // )));

            // GET_PLAYLIST_VIDEOS
            // requestJson.put("type", RequestTypes.GET_PLAYLIST_VIDEOS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("page","1"),
            //         Map.entry("perpage","10"),
            //         Map.entry("playlist_id","7")
            // )));

            /// history

            /// GET_HISTORIES
            // requestJson.put("type", RequestTypes.GET_HISTORIES.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("page","1"),
            //         Map.entry("perpage","5"),
            //         Map.entry("user_id","4")
            // )));

            /// DELETE_HISTORY
            // requestJson.put("type", RequestTypes.DELETE_HISTORY.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("history_id","3"),
            //         Map.entry("user_id","4")
            // )));

            /// DELETE_ALL_USER_HISTORY
            // requestJson.put("type", RequestTypes.DELETE_ALL_USER_HISTORY.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id","4")
            // )));

            ////// notification

            // GET_UNSEEN_NOTIFICATION_COUNT
            // requestJson.put("type", RequestTypes.GET_UNSEEN_NOTIFICATION_COUNT.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id","4")
            // )));

            /// GET_UNSEEN_NOTIFICATIONS
            // requestJson.put("type", RequestTypes.GET_UNSEEN_NOTIFICATIONS.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("perpage", "5"),
            //         Map.entry("page", "1"),
            //         Map.entry("user_id", "2")
            // )));

            /// SET_NOTIFICATION_SEEN
            // requestJson.put("type", RequestTypes.SET_NOTIFICATION_SEEN.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("notification_id","12"),
            //         Map.entry("user_id","4")
            // )));

            /// SET_ALL_NOTIFICATION_SEEN
            // requestJson.put("type", RequestTypes.SET_ALL_NOTIFICATION_SEEN.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id","4")
            // )));

            /// DELETE_NOTIFICATION
            // requestJson.put("type", RequestTypes.DELETE_NOTIFICATION.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("notification_id","4"),
            //         Map.entry("user_id","2")
            // )));

            /// DELETE_ALL_NOTIFICATION
            // requestJson.put("type", RequestTypes.DELETE_ALL_NOTIFICATION.ordinal());
            // requestJson.put("body", new JSONObject(Map.ofEntries(
            //         Map.entry("user_id","4")
            // )));


            outStream.println(requestJson);
            System.out.println("[CLIENT] Request Sent.");

            System.out.println("response: " + inpStream.readLine());
            // System.out.println("response: " + new Gson().fromJson(inpStream.readLine(),Video.class));

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
