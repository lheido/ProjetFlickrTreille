package fr.projetflickrtreille.flickr;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;

import fr.projetflickrtreille.R;
import fr.projetflickrtreille.utils.Photo;
import fr.projetflickrtreille.utils.UserPref;

/**
 * Created by lheido on 02/12/14.
 */
public class FlickrTask {

    private interface FlikcrResponseCallback{
        void parse(JsonObject data);
    }

    private static void ionisator(final Context context, final String url, final FlikcrResponseCallback callback){
        Ion.with(context)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result != null) {
                            callback.parse(result);
                        }else{
                            if(e != null){
                                e.printStackTrace();
                            }
                            Toast.makeText(context,
                                    R.string.error_result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class Search{

        public interface SearchCallback<T>{

            void listIsPopulated(ArrayList<T> list);

            void listIsEmpty();
        }

        public static void getPhotos(final Context context, final String tags,
                                   final SearchCallback<Photo> callback){
            getPhotosFromPage(context, tags, 1, callback);
        }

        public static void getPhotosFromPage(final Context context, final String tags, final int page, final SearchCallback<Photo> callback){
            HashMap<String, String> params = FlickrRestApi.getDefaultParams();
            params.put(FlickrRestApi.KEY_METHOD, FlickrRestApi.METHOD_SEARCH);
            params.put(FlickrRestApi.KEY_EXTRAS, "owner_name");
            params.put(FlickrRestApi.KEY_PER_PAGE, "10"); // 100 by default, may not be included in params.
            params.put(FlickrRestApi.KEY_PAGE, ""+page); // 1 by default, may not be included in params.
            String filter = (UserPref.useTextInsteadOfTagsEnabled(context))? "text":"tags";
            params.put(filter, tags);
            ionisator(context,
                    FlickrRestApi.build(params),
                    new FlikcrResponseCallback() {
                @Override
                public void parse(JsonObject data) {
                    if (data.has("photos")) {
                        final JsonObject photosAttr = data.getAsJsonObject("photos");
                        if (photosAttr.has("photo")) {
                            ArrayList<Photo> list = Photo.getPhotosFromJsonArray(
                                    context,
                                    photosAttr.getAsJsonArray("photo"));
                            if (!list.isEmpty()) {
                                callback.listIsPopulated(list);
                            } else {
                                callback.listIsEmpty();
                            }
                        }
                    }
                }
            });
        }
    }

}
