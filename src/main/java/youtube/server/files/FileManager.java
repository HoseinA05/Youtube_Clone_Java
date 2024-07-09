package youtube.server.files;

import org.json.JSONObject;
import youtube.SortTypes;
import youtube.server.controllers.Helper;
import youtube.server.models.ModelError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

public class FileManager {

    public static String storePhoto(String photo) throws Exception {
        byte[] photoContent = Base64.getDecoder().decode(photo);
        String path = UUID.randomUUID().toString().replace("-", "");
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\photos\\" + path);
        if (file.exists()) {
            throw new RuntimeException("file path already exists!");
        }
        var out = new FileOutputStream(file);
        out.write(photoContent);
        out.close();
        // creating file
        return path;
    }

    public static void deletePhoto(String photoUUID) throws Exception {
        if (photoUUID.isEmpty()){
            return;
        }
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\photos\\" + photoUUID);
        if (!file.delete()){
            System.out.println("failed to delete photo:"+photoUUID);
        }
    }

    public static String storeVideo(String video) throws Exception {
        byte[] videoContent = Base64.getDecoder().decode(video);
        String path = UUID.randomUUID().toString().replace("-", "");
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\videos\\" + path);
        if (file.exists()) {
            throw new RuntimeException("file path already exists!");
        }
        var in = new FileOutputStream(file);
        in.write(videoContent);
        in.close();
        // creating file
        return path;
    }

    public static void deleteVideo(String videoUUID) throws Exception {
        if (videoUUID.isEmpty()){
            return;
        }
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\photos\\" + videoUUID);
        if (!file.delete()){
            System.out.println("failed to delete photo:"+videoUUID);
        }
    }

    public String GET_PHOTO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"path"}, JSONObject.getNames(body));
        String path = Helper.validateRequiredString(body, "path");
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\photos\\" + path);
        if (!file.exists()) {
            throw new ModelError("photo not exist");
        }
        var in = new FileInputStream(file);
        byte[] c = in.readAllBytes();
        // in.write(videoContent);
        in.close();
        return new String(c);
    }

    public String GET_VIDEO(JSONObject body) throws Exception {
        Helper.validateAllowedKeys(new String[]{"path"}, JSONObject.getNames(body));
        String path = Helper.validateRequiredString(body, "path");
        File file = new File("Youtube_Clone_Java\\src\\main\\java\\youtube\\server\\files\\videos\\" + path);
        if (!file.exists()) {
            throw new ModelError("video not exist");
        }
        var in = new FileInputStream(file);
        byte[] c = in.readAllBytes();
        // in.write(videoContent);
        in.close();
        return new String(c);
    }
}
