package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;

import youtube.server.files.FileManager;
import youtube.server.models.ModelError;
import youtube.server.models.PlaylistModel;
import youtube.server.models.User;
import youtube.server.models.UserModel;

import java.util.Map;

public class UserController {

    public String GET_USERINFO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "current_user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        if (body.has("current_user_id")) {
            int currentUserId = Helper.validatePositiveInt(body, "current_user_id");
            return new Gson().toJson(UserModel.authGetUserById(userId, currentUserId));
        }

        return new Gson().toJson(UserModel.getUserById(userId));
    }

    public String SIGN_IN(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"username", "password"}, JSONObject.getNames(body));
        String username = Helper.validateRequiredString(body, "username");
        String password = Helper.validateRequiredString(body, "password");
        User user = UserModel.getUserByPassword(username, password);

        return new Gson().toJson(user);
    }

    public String FOLLOW(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "following_user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int followingId = Helper.validatePositiveInt(body, "following_user_id");
        UserModel.setFollowing(userId, followingId);

        return Helper.messageToJson("follow created successfully");
    }

    public String UNFOLLOW(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "following_user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int followingId = Helper.validatePositiveInt(body, "following_user_id");
        UserModel.removeFollowing(userId, followingId);

        return Helper.messageToJson("follow deleted successfully");
    }

    public String SET_USER_CHANNEL_PHOTO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"channelPhoto", "user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        String photo = Helper.validateRequiredString(body, "channelPhoto");
        String channelPhoto = FileManager.storePhoto(photo);

        UserModel.setUserChannelPhoto(channelPhoto, userId);
        return Helper.messageToJson("user profile photo edited successfully");
    }

    public String SET_USER_PROFILE_PHOTO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"profilePhoto", "user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        String photo = Helper.validateRequiredString(body, "profilePhoto");
        String profilePhoto = FileManager.storePhoto(photo);

        UserModel.setUserProfilePhoto(profilePhoto, userId);
        return Helper.messageToJson("user profile photo edited successfully");
    }

    public String EDIT_USER(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"newUsername", "newPassword", "newAboutMe", "user_id", "oldPassword"}, JSONObject.getNames(body));
        String newUsername = Helper.validateRequiredString(body, "newUsername");
        String newPassword = Helper.validateRequiredString(body, "newPassword");
        String newAboutMe = Helper.validateRequiredString(body, "newAboutMe");
        int userId = Helper.validatePositiveInt(body, "user_id");
        String oldPassword = Helper.validateRequiredString(body, "oldPassword");
        UserModel.editUser(newUsername, newPassword, newAboutMe, userId, oldPassword);
        return Helper.messageToJson("user edited successfully");
    }

    public String CREATE_USER(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"email", "username", "password"}, JSONObject.getNames(body));
        String email = Helper.validateRequiredString(body, "email");
        String username = Helper.validateRequiredString(body, "username");
        String password = Helper.validateRequiredString(body, "password");
        int id = UserModel.createUser(email, username, password);
        PlaylistModel.createPlaylist("Liked videos","users all liked video will be here",id,false);
        PlaylistModel.createPlaylist("Watch later","users watch list",id,false);

        return Helper.messageAndIdToJson("user created successfully", id);
    }

    public String DELETE_USER(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "password"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        String password = Helper.validateRequiredString(body, "password");
        UserModel.deleteUser(userId, password);
        var thumbnails = UserModel.getThumbnailsById(userId);
        if (thumbnails!=null){
            FileManager.deletePhoto(thumbnails[0]);
            FileManager.deletePhoto(thumbnails[1]);
        }
        return Helper.messageToJson("user deleted successfully");
    }

}
