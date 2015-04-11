package fr.projetflickrtreille.fragments.connect;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


import java.util.HashMap;

import fr.projetflickrtreille.R;
import fr.projetflickrtreille.flickr.FlickrRestApi;
import fr.projetflickrtreille.utils.JsInterface;
import fr.projetflickrtreille.utils.TokenInvalideException;
import fr.projetflickrtreille.utils.UserPref;

/**
 * Created by lheido on 05/01/15.
 */
public abstract class ConnectWebView extends Fragment implements JsInterface.Callback, View.OnClickListener {

    private WebView webView;
    private EditText input;
    private Button button;
    private Token requestToken;
    private ConnectWebViewListener listener;

    public static ConnectWebView newInstance(final ConnectWebViewListener listener){
        return new ConnectWebView() {
            @Override
            public ConnectWebViewListener setListener() {
                return listener;
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = setListener();
    }

    public abstract ConnectWebViewListener setListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.connect_webview, container, false);
        input = (EditText) view.findViewById(R.id.auth_code_input);
        button = (Button) view.findViewById(R.id.button_auth_code);
        button.setOnClickListener(this);
        webView = (WebView) view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(), "JsInterface");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:window.JsInterface.getHtml" +
                        "(document.querySelector(\"#Main p:nth-child(2) span\").innerHTML);");
            }
        });
        webView.requestFocus();
        setUp();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        JsInterface.setCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        JsInterface.setCallback(null);
    }

    private void setUp(){
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OAuthService service = new ServiceBuilder()
                        .provider(FlickrApi.class)
                        .apiKey(FlickrRestApi.API_KEY)
                        .apiSecret(FlickrRestApi.API_KEY_SECRET).build();
                // Obtain the Request Token
                requestToken = service.getRequestToken();
                String authorizationUrl = service.getAuthorizationUrl(requestToken) + "&perms=read";
                publishProgress(authorizationUrl);
                return null;
            }
            @Override
            protected void onProgressUpdate (String... prog){
                if(webView != null){
                    webView.loadUrl(prog[0]);
                }
            }
        }.execute();
    }

    @Override
    public void onGetHtml(final String code) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                input.setText(code);
                if( Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
                    button.callOnClick();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(input != null && input.getText() != null && !input.getText().toString().isEmpty()){
            view.setEnabled(false);
            String code = input.getText().toString();
            new AsyncTask<String, Void, Boolean>(){

                public Token accessToken;

                @Override
                protected Boolean doInBackground(String... code) {
                    OAuthService service = new ServiceBuilder()
                            .provider(FlickrApi.class)
                            .apiKey(FlickrRestApi.API_KEY)
                            .apiSecret(FlickrRestApi.API_KEY_SECRET).build();
                    try {
                        accessToken = service.getAccessToken(requestToken, new Verifier(code[0]));
                        HashMap<String, String> params = FlickrRestApi.getDefaultParams();
                        params.put("method", "flickr.test.login");
                        OAuthRequest request = FlickrRestApi.buildSigned(params, accessToken);
                        Response response = request.send();
                        JsonElement jsonElt = new JsonParser().parse(response.getBody());
                        String resultat = jsonElt.getAsJsonObject().get("stat").getAsString();
                        return resultat.equals("ok");
                    } catch (Exception ex){
                        ex.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean resultat) {
                    if (getActivity().getApplicationContext() != null) {
                        if (resultat) {
                            UserPref.setLoginInfos(getActivity().getApplicationContext(), accessToken);
                            listener.onSuccess(accessToken);
                        } else {
                            listener.onError(new TokenInvalideException());
                        }
                    }
                }
            }.execute(code);
        }
    }


    /**
     * interface implémenter par ConnectFragment
     */
    public interface ConnectWebViewListener{
        void onSuccess(Token accessToken);
        void onError(Exception ex);
    }
}
