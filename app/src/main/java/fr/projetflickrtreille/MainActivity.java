package fr.projetflickrtreille;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.scribe.model.Token;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import fr.projetflickrtreille.fragments.NavigationDrawerFragment;
import fr.projetflickrtreille.fragments.connect.ConnectWebView;
import fr.projetflickrtreille.fragments.photos.PhotosDisplayFragment;
import fr.projetflickrtreille.flickr.FlickrTask;
import fr.projetflickrtreille.utils.Photo;
import fr.projetflickrtreille.utils.TokenInvalideException;
import fr.projetflickrtreille.utils.UserPref;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    SearchView.OnQueryTextListener, ConnectWebView.ConnectWebViewListener {

    private static final String RETAIN_TITLE = "retain_title";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private SearchView mSearchView;
    private static long back_pressed = 0;
    private LinearLayout mActionBarTitleItem;
    private TextView mActionBarTitle;
    private static boolean logged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setElevation(0);
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            findViewById(R.id.container).setElevation(4);
        }

        mTitle = getTitle();

        // Set up the drawer.
        if (findViewById(R.id.relative_land_layout) == null) {
            mNavigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mActionBarTitleItem = (LinearLayout) getLayoutInflater().inflate(R.layout.abc_action_bar_title_item, null);
        mActionBarTitle = (TextView) mActionBarTitleItem.findViewById(R.id.action_bar_title);
        mActionBarTitle.setText(mTitle);
        mActionBarTitle.setTextColor(getResources().getColorStateList(R.color.action_bar_text_color));
        mActionBarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mActionBarTitle.setClickable(true);
        mActionBarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNavigationDrawerFragment == null || !mNavigationDrawerFragment.isDrawerOpen()){
                    String s = (!mTitle.equals(getResources().getString(R.string.app_name)))? mTitle.toString():"";
                    mSearchView.onActionViewExpanded();
                    mSearchView.setQuery(s, false);
                }
            }
        });
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(mActionBarTitleItem);

        if(savedInstanceState != null){
            mTitle = savedInstanceState.getString(RETAIN_TITLE);
            mActionBarTitle.setText(mTitle);
        }
        else if (logged) {
            try {
                Token accessToken = UserPref
                        .getAccessTokenFromLoginInfos(getApplicationContext());
            } catch (TokenInvalideException e) {
                replaceContainer(ConnectWebView.newInstance(this));
            }
        } else {
            if (!UserPref.autoLoginEnabled(this)) {
                replaceContainer(ConnectWebView.newInstance(this));
            }
        }
    }

    private void performSearch(final String search) {
        try {
            final String tags = URLEncoder.encode(search, "utf-8");
            FlickrTask.Search.getPhotos(this, tags, new FlickrTask.Search.SearchCallback<Photo>() {
                @Override
                public void listIsPopulated(ArrayList<Photo> list) {
                    replaceContainer(PhotosDisplayFragment.newInstance(tags, list));
                    mTitle = search;
                    mActionBarTitle.setText(mTitle);
                }

                @Override
                public void listIsEmpty() {
                    Toast.makeText(getApplicationContext(),R.string.no_result, Toast.LENGTH_SHORT)
                         .show();
                }
            });
        } catch (UnsupportedEncodingException e) {}
    }

    public void replaceContainer(Fragment frag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(RETAIN_TITLE, mTitle.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNavigationDrawerOpened() {
        mActionBarTitle.setText(R.string.app_name);
    }

    @Override
    public void onNavigationDrawerClosed() {
        mActionBarTitle.setText(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(mActionBarTitleItem);
            mActionBarTitle.setText(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment == null || !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            try {
                mSearchView.setIconifiedByDefault(true);
                mSearchView.setOnQueryTextListener(this);
            }catch (Exception e){e.printStackTrace();}
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        try {
            if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
            else Toast.makeText(getBaseContext(), R.string.double_back_toast, Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }catch (Exception ex){ex.printStackTrace();}

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        try {
            mSearchView.onActionViewCollapsed();
        }catch (Exception e){e.printStackTrace();}
        performSearch(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onSuccess(Token accessToken) {
        logged = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(fragmentManager.findFragmentById(R.id.container))
                .commit();
    }

    @Override
    public void onError(Exception ex) {
        logged = false;
        Toast.makeText(
                getApplicationContext(),
                ex.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
