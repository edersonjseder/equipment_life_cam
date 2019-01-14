package cam.equipment.life.com.equipmentlifecam.utils;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String STORAGE_PATH_UPLOADS = "equipment_pictures/";

    private static final String TAG = Utils.class.getSimpleName();

    // Class not instantiable
    private Utils (){}

    public static Uri buildImageUrl(String imageUrl) {

        Log.v(TAG, "Built Image URI " + imageUrl);

        Uri builtUri = Uri.parse(imageUrl);

        return builtUri;
    }

    public static String getFileExtension(Uri uri, ContentResolver contentResolver) {

        ContentResolver cR = contentResolver;

        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    public static String getPlainText(String text) {

        String[] split;
        String result = "";

        try {

            split = text.split("%2F|\\?");

            System.out.println(split);

            result = split[1];

        } catch (Exception ex) {
            ex.getMessage();
        }

        return result;
    }

    public static String getFormattedDate(Date dateRegistered) {

        String date = "";

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            date = sdf.format(dateRegistered);

            System.out.println(date);

        } catch (Exception ex) {

        }

        return date;
    }

}
