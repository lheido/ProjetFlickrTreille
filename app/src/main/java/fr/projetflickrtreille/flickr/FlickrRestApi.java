package fr.projetflickrtreille.flickr;


import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by lheido on 29/11/14.
 */
public class FlickrRestApi {
    public final static String baseUrlApi = "https://api.flickr.com/services/rest/";
    public final static String API_KEY = "be5b835f4d73549ed2177dbf3f40df55";
    public static final String API_KEY_SECRET = "de7f5a20bdccf8d3";
    public final static String METHOD_SEARCH = "flickr.photos.search";
//    public final static String METHOD_PEOPLE_GET_PUBLIC_PHOTOS = "flickr.people.getPublicPhotos";

//    private static String method;
//    private static String format;
//    private static String noJsonCallback;
    private static String perPage = "100";
//    private static String tags;
//    private static String api_key;
//    private static String extras;
//    private static String userId;
//    private static String page;

    public static final String KEY_METHOD = "method";
    public static final String KEY_FORMAT = "format";
    public static final String KEY_NO_JSON_CALLBACK = "nojsoncallback";
    public static final String KEY_PER_PAGE = "per_page";
    public static final String KEY_PAGE = "page";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_API_KEY = "api_key";
    public static final int DEFAULT_PER_PAGE = 100;

    /**
     * Build flickr api url with params.
     * @param params
     * @return the url built with @param.
     */
    public static String build(Map<String, String> params){
        String url = baseUrlApi;
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()){
            url += ((first)?"?":"&") + entry.getKey() + "=" + entry.getValue();
            first = false;
            if (entry.getKey().equals(KEY_PER_PAGE)) {
                perPage = entry.getValue();
            }
        }
        return url;
    }

    public static OAuthRequest buildSigned(Map<String, String> params, Token accessToken) {
        OAuthService service = new ServiceBuilder()
                .provider(FlickrApi.class)
                .apiKey(FlickrRestApi.API_KEY)
                .apiSecret(API_KEY_SECRET).build();
        OAuthRequest request = new OAuthRequest(Verb.GET, FlickrRestApi.baseUrlApi);
        for (Map.Entry<String, String> entry : params.entrySet()){
            request.addQuerystringParameter(entry.getKey(), entry.getValue());
            if (entry.getKey().equals(KEY_PER_PAGE)) {
                perPage = entry.getValue();
            }
        }
        service.signRequest(accessToken, request);
        return request;
    }

    /**
     * Return HashMap with json format, nojsoncallback=1 and app api key.
     * @return HashMap with json format, nojsoncallback=1 and app api key.
     */
    public static HashMap<String, String> getDefaultParams(){
        HashMap<String, String> params = new HashMap<>();
        params.put(KEY_FORMAT, "json");
        params.put(KEY_NO_JSON_CALLBACK, "1");
        params.put(KEY_API_KEY, API_KEY);
        return params;
    }

    public static int retrievePagesLoaded(int listSize){
        return (listSize / Integer.parseInt(perPage)) + 1;
    }

//    public static String search(Context context, String tags, int perPage, int page){
//        HashMap<String, String> params = new HashMap<>();
//        params.put(KEY_METHOD, METHOD_SEARCH);
//        params.put(KEY_FORMAT, "json");
//        params.put(KEY_NO_JSON_CALLBACK, "1");
//        params.put(KEY_PER_PAGE, ""+perPage); // 100 by default, may not be included in params.
//        params.put(KEY_PAGE, ""+page); // 1 by default, may not be included in params.
//        String filter = (UserPref.useTextInsteadOfTagsEnabled(context))? "text":"tags";
//        params.put(filter, tags);
//        params.put(KEY_EXTRAS, "owner_name");
//        params.put(KEY_API_KEY, API_KEY);
//        return build(params);
//    }
//
//    protected static void reset(){
//        method = null;
//        format = null;
//        noJsonCallback = null;
//        tags = null;
//        api_key = null;
//        perPage = "100";
//        extras = null;
//        userId = null;
//    }
//
//    protected static String build(Context context){
//        String url = baseUrl;
//        url += (method != null)? "method="+method :"";
//        url += (format != null)? "&format="+format :"&format=json";
//        url += (noJsonCallback != null)? "&nojsoncallback="+noJsonCallback :"&nojsoncallback=1";
//        url += (perPage != null)? "&per_page="+perPage :"";
//        url += (page != null)? "&page="+page :"";
//        url += (userId != null)? "&user_id="+userId :"";
//        url += (tags != null)? tagsOrText(context, tags) :"";
//        url += (extras != null)? "&extras="+extras :"";
//        url += (api_key != null)? "&api_key="+api_key :"&api_key="+API_KEY;
//        reset();
//        return url;
//    }
//
//    private static String tagsOrText(Context context, String tags) {
//        String filter = (UserPref.useTextInsteadOfTagsEnabled(context))? "text":"tags";
//        return "&"+filter+"="+tags;
//    }
//
//    /**
//     * Build url with search method and tags
//     * @param context
//     * @param tags
//     * @return url for flickr api request.
//     */
//    public static String search(Context context, String tags){
//        setMethod(METHOD_SEARCH);
//        setTags(tags);
//        setExtras("owner_name"); //',' pour plusieurs type d'extras
//        return build(context);
//    }
//
//    public static String search(Context context, String tags, int perPage){
//        setPerPage(""+perPage);
//        return search(context, tags);
//    }
//
//    public static String search(Context context, int page, String tags) {
//        setPage("" + page);
//        return search(context, tags);
//    }
//    /**
//     * Build url with people.getPublicPhotos method and flickr user_id
//     * @param context
//     * @param userId
//     * @return url for flickr api request.
//     */
//    public static String getPublicPhotos(Context context, String userId) {
//        setMethod(METHOD_PEOPLE_GET_PUBLIC_PHOTOS);
//        setExtras("owner_name");
//        setUserId(userId);
//        return build(context);
//    }
//
//    protected static void setMethod(String method) {
//        FlickrRestUrlBuilder.method = method;
//    }
//
//    protected static void setFormat(String format) {
//        FlickrRestUrlBuilder.format = format;
//    }
//
//    protected static void setNoJsonCallback(String noJsonCallback) {
//        FlickrRestUrlBuilder.noJsonCallback = noJsonCallback;
//    }
//
//    protected static void setTags(String tags) {
//        FlickrRestUrlBuilder.tags = tags;
//    }
//
//    protected static void setApi_key(String api_key) {
//        FlickrRestUrlBuilder.api_key = api_key;
//    }
//
//    protected static void setPerPage(String perPage) {
//        FlickrRestUrlBuilder.perPage = perPage;
//    }
//
//    protected static void setExtras(String extras) {
//        FlickrRestUrlBuilder.extras = extras;
//    }
//
//    protected static void setUserId(String userId) {
//        FlickrRestUrlBuilder.userId = userId;
//    }
//
//    public static void setPage(String page) {
//        FlickrRestUrlBuilder.page = page;
//    }
}
