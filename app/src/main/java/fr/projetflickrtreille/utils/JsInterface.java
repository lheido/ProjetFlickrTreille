package fr.projetflickrtreille.utils;

import android.webkit.JavascriptInterface;

import java.util.regex.Pattern;

/**
 * Created by lheido on 06/01/15.
 */
public class JsInterface {
    private static Callback callback;

    public static String code;

    public static void setCallback(Callback cb){
        callback = cb;
    }

    public static void setCode(String code) {
        JsInterface.code = code;
        if(callback != null){
            callback.onGetHtml(code);
        }
    }

    @JavascriptInterface
    public void getHtml(String html){
        if(Pattern.matches("[0-9]{1,}-[0-9]{1,}-[0-9]{1,}", html)) {
            setCode(html);
        }
    }

    public interface Callback{
        public void onGetHtml(String code);
    }
}
