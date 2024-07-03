package youtube.client.test;

import org.json.JSONObject;

import java.util.ArrayList;

public class Video {
    private String title;
    private String views;
    private String owner;

    public Video(String title, String views, String owner) {
        this.title = title;
        this.views = views;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public String getViews() {
        return views;
    }

    public String getOwner() {
        return owner;
    }

    public static ArrayList<Video> parseJsonToVideos(String body){
        ArrayList<Video> result = new ArrayList<>();
        JSONObject json = new JSONObject(body);
        System.out.println("Response Type: " + json.get("type"));
        System.out.println(json);

        int i = 0;
        while (!json.isNull(String.valueOf(i))){
            JSONObject videoItem = json.getJSONObject(String.valueOf(i));
            System.out.println("Found a video inside response!");

            Video v = new Video(
                    videoItem.getString("title"),
                    videoItem.getString("views"),
                    videoItem.getString("owner")
            );
            result.add(v);
            i++;
        }
        System.out.println("Finished!");
        return result;
    }
}
