package youtube.server.controllers;

import com.google.gson.Gson;
import org.json.JSONObject;
import youtube.server.models.HistoryModel;

public class HistoryController {

    public String GET_HISTORIES(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "perpage", "page"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int perpage = Helper.validatePositiveInt(body, "perpage");
        int page = Helper.validatePositiveInt(body, "page");
        return new Gson().toJson(HistoryModel.getHistory(userId, perpage, page));
    }

    public String DELETE_HISTORY(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id", "history_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        int historyId = Helper.validatePositiveInt(body, "history_id");
        HistoryModel.removeHistory(historyId, userId);
        return Helper.messageToJson("History deleted successfully");
    }

    public String DELETE_ALL_USER_HISTORY(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"user_id"}, JSONObject.getNames(body));
        int userId = Helper.validatePositiveInt(body, "user_id");
        HistoryModel.removeAllHistory(userId);
        return Helper.messageToJson("all user history deleted successfully");
    }
}
