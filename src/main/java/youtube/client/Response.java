package youtube.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import youtube.server.models.*;

import java.util.ArrayList;

public class Response {

    private boolean isError;
    private int id;
    private String message;
    private String responseContent;

    public Response(String r) {
        responseContent = r;
        System.out.println(r);
        JSONObject response = null;
        try {
            response = new JSONObject(r);
        } catch (JSONException e) {
            System.out.println("response was not a JSON object"); // never should happen
            // this.isError = true;
            // this.message = "Invalid JSON response";
        }

        if (response != null) {
            if (response.has("error")) {
                isError = true;
                message = response.getString("error");
                return;
            }
            if (response.has("message")) {
                isError = false;
                message = response.getString("message");
                if (response.has("id")) {
                    id = response.getInt("id");
                }
            }

        }
    }

    public User toUser() {
        return new Gson().fromJson(responseContent, User.class);
    }

    // public ArrayList<User> toUsers() {
    //     return new Gson().fromJson(responseContent, new TypeToken<ArrayList<User>>(){}.getType());
    // }

    public Video toVideo() {
        return new Gson().fromJson(responseContent, Video.class);
    }

    public ArrayList<Video> toVideos() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<Video>>() {
        }.getType());
    }


    public Comment toComment() {
        return new Gson().fromJson(responseContent, Comment.class);
    }

    public ArrayList<Comment> toComments() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<Comment>>() {
        }.getType());
    }


    public Playlist toPlaylist() {
        return new Gson().fromJson(responseContent, Playlist.class);
    }

    public ArrayList<Playlist> toPlaylists() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<Playlist>>() {
        }.getType());
    }

    public ArrayList<PlaylistVideo> toPlaylistVideos() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<PlaylistVideo>>() {
        }.getType());
    }

    // public History toHistory() {
    //     return new Gson().fromJson(responseContent, History.class);
    // }

    public ArrayList<History> toHistories() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<History>>() {
        }.getType());
    }

    // public Notification toNotification() {
    //     return new Gson().fromJson(responseContent, Notification.class);
    // }

    public ArrayList<Notification> toNotifications() {
        return new Gson().fromJson(responseContent, new TypeToken<ArrayList<Notification>>() {
        }.getType());
    }


    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
