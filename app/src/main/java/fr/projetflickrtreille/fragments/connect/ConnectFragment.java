package fr.projetflickrtreille.fragments.connect;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.scribe.model.Token;

import fr.projetflickrtreille.MainActivity;
import fr.projetflickrtreille.R;
import fr.projetflickrtreille.utils.TokenInvalideException;
import fr.projetflickrtreille.utils.UserPref;

/**
 * Created by lheido on 12/12/14.
 */
public class ConnectFragment extends Fragment implements ConnectWebView.ConnectWebViewListener {

    private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";
    private AuthListener listener;
    private boolean mPauseListener;


    public static Fragment newInstance() {
        return new ConnectFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.connect_fragment, container, false);
//        setRetainInstance(true);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if(UserPref.autoLoginEnabled(getActivity().getApplicationContext())){
            try {
                Token accessToken = UserPref
                        .getAccessTokenFromLoginInfos(getActivity().getApplicationContext());
                if(listener != null && !mPauseListener) {
                    listener.onAuthSucess();
                }
            } catch (TokenInvalideException e) {
                transaction.replace(R.id.connect_container, ConnectWebView.newInstance(this));
                transaction.commit();
            }
        } else {
            transaction.replace(R.id.connect_container, ConnectWebView.newInstance(this));
            transaction.commit();
        }
        return view;
    }

    @Override
    public void onSuccess(Token accessToken) {
        if(listener != null && !mPauseListener) {
            listener.onAuthSucess();
        }
    }

    @Override
    public void onError(Exception ex) {
        Toast.makeText(
                getActivity().getApplicationContext(),
                ex.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AuthListener) activity;
        } catch (ClassCastException e) {
            listener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPauseListener = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPauseListener = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /**
     * connect interface
     */
    public interface AuthListener {
        void onAuthSucess();
    }
}
