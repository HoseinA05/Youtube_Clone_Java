package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;
import youtube.server.files.FileManager;
import youtube.server.models.CommentModel;
import youtube.server.models.ModelError;
import youtube.server.models.PlaylistModel;
import youtube.server.models.VideoModel;

public class PlaylistController {

    public String GET_PLAYLIST_ID(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_name","user_id"}, JSONObject.getNames(body));
        String playlistName = Helper.validateRequiredString(body, "playlist_name");
        int userId =  Helper.validatePositiveInt(body, "user_id");
        return new Gson().toJson(PlaylistModel.getPlaylistIdByName(playlistName,userId));
    }

    public String GET_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id"}, JSONObject.getNames(body));
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");

        return new Gson().toJson(PlaylistModel.getPlaylistById(playlistId));
    }

    public String GET_USER_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");

        return new Gson().toJson(PlaylistModel.getUserPlaylist(userId));
    }

    public String CREATE_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"name", "text", "user_id"}, JSONObject.getNames(body));
        String name = Helper.validateRequiredString(body, "name");
        String text = Helper.validateRequiredString(body, "text");
        int userId = Helper.validatePositiveInt(body, "user_id");

        return Helper.messageAndIdToJson("playlist created successfully", PlaylistModel.createPlaylist(name, text, userId,true));
    }

    public String SET_PLAYLIST_THUMBNAIL(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "playlist_id", "thumbnail"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        String thumbnail = Helper.validateRequiredString(body, "thumbnail");
        String thumbnailPath = FileManager.storePhoto(thumbnail);
        PlaylistModel.setPlaylistThumbnail(thumbnailPath, playlistId, userId);
        return Helper.messageToJson("playlist thumbnail edited successfully");
    }

    public String EDIT_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"newName", "newText", "user_id", "playlist_id"}, JSONObject.getNames(body));
        String name = Helper.validateRequiredString(body, "newName");
        String text = Helper.validateRequiredString(body, "newText");
        int userId = Helper.validatePositiveInt(body, "user_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");

        PlaylistModel.editPlaylist(name, text, playlistId, userId);

        return Helper.messageToJson("playlist edited successfully");
    }

    public String DELETE_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "playlist_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        PlaylistModel.deletePlaylist(playlistId, userId);
        FileManager.deletePhoto(PlaylistModel.getThumbnailById(playlistId));
        return Helper.messageToJson("playlist deleted successfully");
    }

    public String PUT_ADMIN_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "user_id", "collaborator_user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int collaboratorUserId = Helper.validatePositiveInt(body, "collaborator_user_id");
        PlaylistModel.addAdminToPlaylist(playlistId, userId, collaboratorUserId);

        return Helper.messageToJson("playlist collaborator added successfully");
    }

    public String DELETE_ADMIN_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "user_id", "collaborator_user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int collaboratorUserId = Helper.validatePositiveInt(body, "collaborator_user_id");
        if (userId == collaboratorUserId) {
            throw new ModelError("cannot remove playlist creator from admins!");
        }
        PlaylistModel.removeAdminFromPlaylist(playlistId, userId, collaboratorUserId);

        return Helper.messageToJson("playlist collaborator removed successfully");
    }

    public String PUT_VIDEO_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "custom_order", "video_id", "collaborator_user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int collaboratorUserId = Helper.validatePositiveInt(body, "collaborator_user_id");
        int order = Helper.validatePositiveInt(body, "custom_order");
        VideoModel.addVideoToPlaylist(playlistId, videoId, order, collaboratorUserId);

        return Helper.messageToJson("video added to playlist successfully");
    }

    public String EDIT_VIDEO_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "new_custom_order", "video_id", "collaborator_user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int collaboratorUserId = Helper.validatePositiveInt(body, "collaborator_user_id");
        int order = Helper.validatePositiveInt(body, "new_custom_order");
        VideoModel.editVideoFromPlaylist(playlistId, videoId, order, collaboratorUserId);

        return Helper.messageToJson("video from playlist edited successfully");
    }

    public String DELETE_VIDEO_PLAYLIST(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "video_id", "collaborator_user_id"}, JSONObject.getNames(body));
        int videoId = Helper.validatePositiveInt(body, "video_id");
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int collaboratorUserId = Helper.validatePositiveInt(body, "collaborator_user_id");
        VideoModel.removeVideoFromPlaylist(playlistId, videoId, collaboratorUserId);

        return Helper.messageToJson("video from playlist deleted successfully");
    }

    public String GET_PLAYLIST_VIDEOS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"playlist_id", "perpage", "page"}, JSONObject.getNames(body));
        int playlistId = Helper.validatePositiveInt(body, "playlist_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");

        return new Gson().toJson(VideoModel.getVideosOfPlaylist(playlistId, page, perpage));
    }


}
