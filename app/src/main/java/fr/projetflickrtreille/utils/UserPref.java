package fr.projetflickrtreille.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.scribe.model.Token;

/**
 * Created by lheido on 09/12/14.
 */
public class UserPref {

    public static final String SETTINGS_TAGS_OR_TEXT = "tags_or_text";
    public static final String SETTINGS_HD_IMAGE = "hd_image";
    public static final String SETTINGS_AUTO_LOGIN = "auto_login";
    public static final String SETTINGS_LOGIN_TOKEN = "login_token";
    public static final String SETTINGS_LOGIN_SECRET = "login_secret";
    public static final String SETTINGS_LOGIN_RAW_RESPONSE = "login_raw_response";

    private static SharedPreferences getPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean useTextInsteadOfTagsEnabled(Context context){
        return getPref(context).getBoolean(SETTINGS_TAGS_OR_TEXT, false);
    }

    public static void setTagsOrText(Context context, boolean value){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(SETTINGS_TAGS_OR_TEXT, value);
        editor.apply();
    }

    public static boolean useHdImageEnabled(Context context) {
        return getPref(context).getBoolean(SETTINGS_HD_IMAGE, false);
    }

    public static void setUseHdImage(Context context, boolean value){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(SETTINGS_HD_IMAGE, value);
        editor.apply();
    }

    public static boolean autoLoginEnabled(Context context) {
        return getPref(context).getBoolean(SETTINGS_AUTO_LOGIN, false);
    }

    public static void setAutoLoginEnabled(Context context, boolean value){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putBoolean(SETTINGS_AUTO_LOGIN, value);
        editor.apply();
    }

    public static void setLoginInfos(Context context, Token accessToken) {
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(SETTINGS_LOGIN_TOKEN, accessToken.getToken());
        editor.putString(SETTINGS_LOGIN_SECRET, accessToken.getSecret());
        editor.putString(SETTINGS_LOGIN_RAW_RESPONSE, accessToken.getRawResponse());
        editor.apply();
    }

    public static Token getAccessTokenFromLoginInfos(Context context) throws TokenInvalideException{
        SharedPreferences pref = getPref(context);
        String token = pref.getString(SETTINGS_LOGIN_TOKEN, "");
        String secret = pref.getString(SETTINGS_LOGIN_SECRET, "");
        String rawResponse = pref.getString(SETTINGS_LOGIN_RAW_RESPONSE, "");
        if(token.isEmpty() || secret.isEmpty() || rawResponse.isEmpty()) throw new TokenInvalideException();
        return new Token(
                token,
                secret,
                rawResponse
        );
    }
}
