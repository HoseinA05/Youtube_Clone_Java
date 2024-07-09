package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;
import youtube.server.models.NotificationModel;

public class NotificationController {

    public String GET_UNSEEN_NOTIFICATION_COUNT(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        return new Gson().toJson(NotificationModel.getUnseenNotificationsCount(userId));
    }

    public String GET_UNSEEN_NOTIFICATIONS(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "perpage", "page"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");
        return new Gson().toJson(NotificationModel.getUnseenNotifications(userId, perpage, page));
    }

    public String SET_NOTIFICATION_SEEN(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"notification_id", "user_id"}, JSONObject.getNames(body));
        int notificationId = Helper.validatePositiveInt(body, "notification_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        NotificationModel.setNotificationSeen(notificationId, userId);
        return Helper.messageToJson("notification set as seen successfully");
    }

    public String SET_ALL_NOTIFICATION_SEEN(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        NotificationModel.setAllNotificationsSeen(userId);
        return Helper.messageToJson("all notifications set as seen successfully");
    }

    public String DELETE_NOTIFICATION(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"notification_id", "user_id"}, JSONObject.getNames(body));
        int notificationId = Helper.validatePositiveInt(body, "notification_id");
        int userId = Helper.validatePositiveInt(body, "user_id");
        NotificationModel.deleteNotification(notificationId, userId);
        return Helper.messageToJson("notification deleted successfully");
    }

    public String DELETE_ALL_NOTIFICATION(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        NotificationModel.deleteAllNotification(userId);
        return Helper.messageToJson("user notifications deleted successfully");
    }

}
