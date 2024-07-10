package youtube.client;

import javafx.concurrent.Task;
import org.json.JSONObject;
import youtube.RequestTypes;
import youtube.YoutubeApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Map;

public class FileHandler {
    // public static String getPhoto(String photoUUID) {
    //
    // }

    public static String getVideo(String videoUUID) throws Exception {
        String path = "Youtube_Clone_Java\\src\\main\\resources\\youtube\\temp\\videos\\" + videoUUID;
        var f = new File(path);
        if (f.exists()) {
            System.out.println("File Existed!");
            return f.toURI().toURL().toString();
        }
        JSONObject body = new JSONObject(Map.ofEntries(
                Map.entry("path", videoUUID)
        ));
        //
        String response = RequestHandler.sendRequest(Requests.createRequestContent(RequestTypes.GET_VIDEO, body));
        Response r = new Response(response);
        if (r.isError()) {
            System.out.println("getVideo failed");
            System.out.println("server error:" + r.getMessage());
            return "";
        }
        System.out.println("creating file");
        var out = new FileOutputStream(f);
        out.write(Base64.getDecoder().decode(response));
        // out.write(new String("daasdf").getBytes());
        out.close();
        return f.toURI().toURL().toString();
    }

    public static String getPhoto(String photoUUID) throws Exception {
        String path = "Youtube_Clone_Java\\src\\main\\resources\\youtube\\temp\\photos\\" + photoUUID;
        var f = new File(path);
        if (f.exists()) {
            System.out.println("File Existed!");
            return f.toURI().toURL().toString();
        }
        JSONObject body = new JSONObject(Map.ofEntries(
                Map.entry("path", photoUUID)
        ));
        //
        String response = RequestHandler.sendRequest(Requests.createRequestContent(RequestTypes.GET_PHOTO, body));
        Response r = new Response(response);
        if (r.isError()) {
            System.out.println("getPhoto failed");
            System.out.println(r.getMessage());
            return "";
        }
        System.out.println("creating file");
        var out = new FileOutputStream(f);
        out.write(Base64.getDecoder().decode(response));
        // out.write(new String("daasdf").getBytes());
        out.close();
        return f.toURI().toURL().toString();
    }

    // public static Task<String> getPhoto(String photoUUID) {
    //     return new Task<String>() {
    //         @Override
    //         protected String call() throws Exception {
    //             String path = YoutubeApplication.class.getResource("temp\\photos\\" + photoUUID).toExternalForm();
    //             System.out.println(path);
    //             var f = new File(path);
    //             if (f.exists()) {
    //                 return path;
    //             }
    //             JSONObject body = new JSONObject(Map.ofEntries(
    //                     Map.entry("path", photoUUID)
    //             ));
    //             //
    //             String response = RequestHandler.sendRequest(Requests.createRequestContent(RequestTypes.GET_PHOTO, body));
    //             var out = new FileOutputStream(f);
    //             out.write(response.getBytes());
    //             out.close();
    //             return path;
    //         }
    //     };
    // }
}
