package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;
import youtube.server.files.FileManager;
import youtube.server.models.HistoryModel;
import youtube.server.models.PlaylistModel;
import youtube.server.models.Video;
import youtube.server.models.VideoModel;

public class VideoController {

    public String GET_VIDEO_BY_ID(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"video_id", "current_user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        if (body.has("current_user_id")) {
            int currentUserId = Helper.validatePositiveInt(body, "current_user_id");
            return new Gson().toJson(VideoModel.authGetVideoById(videoId, currentUserId));
        }

        return new Gson().toJson(VideoModel.getVideoById(videoId));
    }

    public String GET_USER_VIDEOS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "perpage", "page", "sort_by"}, JSONObject.getNames(body));
        int order = Helper.validateInt(body, "sort_by");
        int userId = Helper.validatePositiveInt(body, "user_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getUserVideos(userId, order, page, perpage));
    }

    public String GET_NEW_VIDEOS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"perpage", "page"}, JSONObject.getNames(body));
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getNewVideos(page, perpage));
    }

    public String GET_TRENDING_VIDEOS_OF_SUBSCRIPTIONS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id","perpage", "page"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getPopularVideosOfSubscriptionsInWeek(userId,page, perpage));
    }

    public String GET_TRENDING_VIDEOS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"perpage", "page"}, JSONObject.getNames(body));
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getPopularVideosIn24H(page, perpage));
    }

    public String GET_RELATED_VIDEOS(JSONObject body) throws Exception {
        // TODO
        return "";
    }

    public String GET_VIDEOS_OF_TAG(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"tag_id", "perpage", "page"}, JSONObject.getNames(body));
        int tagId = Helper.validatePositiveInt(body, "tag_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getVideosOfTag(tagId, page, perpage));
    }

    public String CREATE_VIDEO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"title", "description", "isPrivate", "video", "user_id"}, JSONObject.getNames(body));
        String title = Helper.validateRequiredString(body, "title");
        String description = Helper.validateRequiredString(body, "description");
        String video = Helper.validateRequiredString(body, "video");
        boolean isPrivate = Helper.validateBoolean(body, "isPrivate");
        int userId = Helper.validatePositiveInt(body, "user_id");
        String videoPath = FileManager.storeVideo(video);
        int createdId = VideoModel.createVideo(title, description, videoPath, isPrivate, userId);

        return Helper.messageAndIdToJson("video created successfully", createdId);
    }

    public String SET_VIDEO_VISIBILITY(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"isPrivate", "video_id", "user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        boolean isPrivate = Helper.validateBoolean(body, "isPrivate");
        VideoModel.setVideoPrivacy(isPrivate, videoId, userId);
        return Helper.messageToJson("video edited successfully");
    }

    public String SET_VIDEO_THUMBNAIL(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"thumbnail", "video_id", "user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        String thumbnail = Helper.validateRequiredString(body, "thumbnail");
        String thumbnailPath = FileManager.storePhoto(thumbnail);
        VideoModel.setVideoThumbnail(thumbnailPath, videoId, userId);
        return Helper.messageToJson("video edited successfully");
    }

    public String EDIT_VIDEO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"newTitle", "isPrivate", "newDescription", "video_id", "user_id"}, JSONObject.getNames(body));
        String newTitle = Helper.validateRequiredString(body, "newTitle");
        String newDescription = Helper.validateRequiredString(body, "newDescription");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        boolean isPrivate = Helper.validateBoolean(body, "isPrivate");
        VideoModel.editVideo(newTitle, newDescription, isPrivate, videoId, userId);

        return Helper.messageToJson("video edited successfully");
    }

    public String DELETE_VIDEO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"video_id", "user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        VideoModel.deleteVideo(videoId, userId);
        var paths = VideoModel.getPathsById(videoId);
        if (paths!=null){
            FileManager.deletePhoto(paths[0]);
            FileManager.deleteVideo(paths[1]);
        }

        return Helper.messageToJson("video deleted successfully");
    }

    public String PUT_VIDEO_LIKE(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"isLike", "video_id", "user_id"}, JSONObject.getNames(body));
        boolean isLike = Helper.validateBoolean(body, "isLike");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        VideoModel.setVideoLike(isLike, videoId, userId);

        return Helper.messageToJson("video liked/disliked successfully");
    }

    public String DELETE_VIDEO_LIKE(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"video_id", "user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        VideoModel.removeVideoLike(videoId, userId);

        return Helper.messageToJson("video like/dislike removed successfully");
    }

    public String PUT_VIDEO_TAG(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"tagName", "video_id", "user_id"}, JSONObject.getNames(body));
        String tagName = Helper.validateRequiredString(body, "tagName");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        VideoModel.addTagToVideo(videoId, tagName, userId);

        return Helper.messageToJson("video tag added successfully");
    }

    public String DELETE_VIDEO_TAG(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"tagName", "video_id", "user_id"}, JSONObject.getNames(body));
        String tagName = Helper.validateRequiredString(body, "tagName");
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        VideoModel.removeTagFromVideo(tagName, videoId, userId);

        return Helper.messageToJson("video tag removed successfully");
    }

}
