package youtube.server;

import org.json.JSONObject;

import youtube.RequestTypes;
import youtube.server.controllers.CommentController;
import youtube.server.controllers.UserController;
import youtube.server.controllers.VideoController;

public class Router {
    private static final RequestTypes[] types;
    static {
        types = RequestTypes.values();
    }

    public String handleRequest(String request) {
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
            case EDIT_USER -> new UserController().EDIT_USER(body);
            case CREATE_USER -> new UserController().CREATE_USER(body);
            case DELETE_USER -> new UserController().DELETE_USER(body);

            /// videos
            case GET_AUTH_VIDEO_BY_ID -> new VideoController().GET_AUTH_VIDEO_BY_ID(body);
            case GET_VIDEO_BY_ID -> new VideoController().GET_VIDEO_BY_ID(body);
            case GET_USER_VIDEOS -> new VideoController().GET_USER_VIDEOS(body);
            case GET_HOME_VIDEOS -> new VideoController().GET_HOME_VIDEOS(body);
            case GET_TAG_VIDEOS -> new VideoController().GET_TAG_VIDEOS(body);
            case CREATE_VIDEO -> new VideoController().CREATE_VIDEO(body);
            case EDIT_VIDEO -> new VideoController().EDIT_VIDEO(body);
            case DELETE_VIDEO -> new VideoController().DELETE_VIDEO(body);
            case PUT_VIDEO_LIKE -> new VideoController().PUT_VIDEO_LIKE(body);
            case DELETE_VIDEO_LIKE -> new VideoController().DELETE_VIDEO_LIKE(body);
            case PUT_VIDEO_TAG -> new VideoController().PUT_VIDEO_TAG(body);
            case DELETE_VIDEO_TAG -> new VideoController().DELETE_VIDEO_TAG(body);

            ///comments
            case GET_AUTH_VIDEO_COMMENTS -> new CommentController().GET_AUTH_VIDEO_COMMENTS(body);
            case GET_VIDEO_COMMENTS -> new CommentController().GET_VIDEO_COMMENTS(body);
            case GET_AUTH_REPLIES -> new CommentController().GET_AUTH_REPLIES(body);
            case GET_REPLIES -> new CommentController().GET_REPLIES(body);
            case CREATE_COMMENT -> new CommentController().CREATE_COMMENT(body);
            case CREATE_REPLY -> new CommentController().CREATE_REPLY(body);
            case EDIT_COMMENT -> new CommentController().EDIT_COMMENT(body);
            case DELETE_COMMENT -> new CommentController().DELETE_COMMENT(body);
            case PUT_COMMENT_LIKE -> new CommentController().PUT_COMMENT_LIKE(body);
            case DELETE_COMMENT_LIKE -> new CommentController().DELETE_COMMENT_LIKE(body);
            default -> throw new RuntimeException("Request type not found");
        };
    }

}
