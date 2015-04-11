package fr.projetflickrtreille.utils;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by lheido on 26/11/14.
 */
public class Photo {


    public static final String MEMBER_ID = "id";
    public static final String MEMBER_TITLE = "title";
    public static final String MEMBER_OWNER = "owner";
    public static final String MEMBER_OWNER_NAME = "ownername";
    public static final String MEMBER_SECRET = "secret";
    public static final String MEMBER_SERVER = "server";
    public static final String MEMBER_FARM = "farm";
    public static final String MEMBER_IS_PUBLIC = "ispublic";
    public static final String MEMBER_IS_FRIEND = "isfriend";
    public static final String MEMBER_IS_FAMILY = "isfamily";

    private String id;
    private String title;
    private String owner;
    private String ownerName;
    private String secret;
    private String server;
    private int farm;
    private String staticUrl;
    private boolean isPublic;
    private boolean isFriend;
    private boolean isFamily;

    public Photo(Context context, JsonObject data) {
        id = data.get(MEMBER_ID).getAsString();
        if(data.has(MEMBER_TITLE)) title = data.get(MEMBER_TITLE).getAsString();
        if(data.has(MEMBER_OWNER)) owner = data.get(MEMBER_OWNER).getAsString();
        if(data.has(MEMBER_OWNER_NAME)) ownerName = data.get(MEMBER_OWNER_NAME).getAsString();
        if(data.has(MEMBER_SECRET)) secret = data.get(MEMBER_SECRET).getAsString();
        if(data.has(MEMBER_SERVER)) server = data.get(MEMBER_SERVER).getAsString();
        if(data.has(MEMBER_FARM)) farm = data.get(MEMBER_FARM).getAsInt();
        if(data.has(MEMBER_IS_PUBLIC)) isPublic = data.get(MEMBER_IS_PUBLIC).getAsBoolean();
        if(data.has(MEMBER_IS_FRIEND)) isFriend = data.get(MEMBER_IS_FRIEND).getAsBoolean();
        if(data.has(MEMBER_IS_FAMILY)) isFamily = data.get(MEMBER_IS_FAMILY).getAsBoolean();
        String hd = (UserPref.useHdImageEnabled(context))?"_b":"";
        staticUrl = "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+hd+".jpg";
    }

    public Photo(Photo source) {
        this.id = source.getId();
        this.title = source.getTitle();
        this.owner = source.getOwner();
        this.ownerName = source.getOwnerName();
        this.secret = source.getSecret();
        this.server = source.getServer();
        this.farm = source.getFarm();
        this.staticUrl = source.getStaticUrl();
        this.isPublic = source.isPublic();
        this.isFriend = source.isFriend();
        this.isFamily = source.isFamily();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public boolean isFamily() {
        return isFamily;
    }

    public void setFamily(boolean isFamily) {
        this.isFamily = isFamily;
    }

    public String getStaticUrl() {
        return staticUrl;
    }

    public int getFarm() {
        return farm;
    }

    public String getServer() {
        return server;
    }

    public String getSecret() {
        return secret;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String toString() {
        return "Photo{id='"+id+"'}";
    }

    public static ArrayList<Photo> getPhotosFromJsonArray(Context context, JsonArray data){
        ArrayList<Photo> result = new ArrayList<Photo>();
        for(JsonElement elt : data){
            result.add(new Photo(context, elt.getAsJsonObject()));
        }
        return result;
    }
}
