package vladimir.yandex;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;

public class Utils {
    public static boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static File getPath(){
        return App.getAppContext().getCacheDir();
    }
}
