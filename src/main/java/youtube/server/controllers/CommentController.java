package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;
import youtube.server.files.FileManager;
import youtube.server.models.CommentModel;
import youtube.server.models.VideoModel;

public class CommentController {

    public String GET_COMMENTS_OF_VIDEO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"video_id", "current_user_id", "perpage", "page", "sort_by"}, JSONObject.getNames(body));
        int order = Helper.validateInt(body, "sort_by");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");
        if (body.has("current_user_id")) {
            int currentUserId = Helper.validatePositiveInt(body, "current_user_id");
            return new Gson().toJson(CommentModel.authGetCommentsOfVideo(videoId, order, page, perpage, currentUserId));
        }
        return new Gson().toJson(CommentModel.getCommentsOfVideo(videoId, order, page, perpage));
    }

    public String GET_REPLIES_OF_COMMENT(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"comment_id", "current_user_id", "perpage", "page"}, JSONObject.getNames(body));
        int commentId = Helper.validatePositiveInt(body, "comment_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");
        if (body.has("current_user_id")) {
            int currentUserId = Helper.validatePositiveInt(body, "current_user_id");
            return new Gson().toJson(CommentModel.authGetRepliesOfComment(commentId, page, perpage, currentUserId));
        }
        return new Gson().toJson(CommentModel.getRepliesOfComment(commentId, page, perpage));
    }

    public String CREATE_COMMENT(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"text", "reply_id", "video_id", "user_id"}, JSONObject.getNames(body));
        String text = Helper.validateRequiredString(body, "text");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        if (body.has("reply_id")) {
            int commentId = Helper.validatePositiveInt(body, "reply_id");
            return Helper.messageAndIdToJson("reply created successfully", CommentModel.createReply(text, videoId, userId, commentId));
        }
        return Helper.messageAndIdToJson("comment created successfully", CommentModel.createComment(text, videoId, userId));
    }

    public String EDIT_COMMENT(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"text", "comment_id", "user_id"}, JSONObject.getNames(body));
        String text = Helper.validateRequiredString(body, "text");
        int commentId = Helper.validatePositiveInt(body, "comment_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        CommentModel.editComment(text, commentId, userId);
        return Helper.messageToJson("comment edited successfully");
    }

    public String DELETE_COMMENT(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"comment_id", "user_id"}, JSONObject.getNames(body));
        int commentId = Helper.validatePositiveInt(body, "comment_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        CommentModel.deleteComment(commentId, userId);
        return Helper.messageToJson("comment and its replies deleted successfully");
    }

    public String PUT_COMMENT_LIKE(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"isLike", "comment_id", "user_id"}, JSONObject.getNames(body));
        boolean isLike = Helper.validateBoolean(body, "isLike");
        int commentId = Helper.validatePositiveInt(body, "comment_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        CommentModel.setCommentLike(isLike, commentId, userId);
        return Helper.messageToJson("comment liked/disliked successfully");
    }

    public String DELETE_COMMENT_LIKE(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"comment_id", "user_id"}, JSONObject.getNames(body));
        int commentId = Helper.validatePositiveInt(body, "comment_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        CommentModel.removeCommentLike(commentId, userId);
        return Helper.messageToJson("comment like/dislike removed successfully");
    }

}
