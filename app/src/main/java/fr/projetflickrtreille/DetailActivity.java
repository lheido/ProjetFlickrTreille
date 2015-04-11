package fr.projetflickrtreille;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import fr.projetflickrtreille.utils.Photo;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by lheido on 29/11/14.
 */
public class DetailActivity extends ActionBarActivity implements SwipeBackActivityBase {
    private static final String EXTRA_IMAGE = "DetailActivity:image";
    private static final String EXTRA_TITLE = "DetailActivity:title";

    private SwipeBackActivityHelper mHelper;
    private PhotoViewAttacher mAttacher;
    private ImageView image;
    private String url;
    private float mImageScale;
    private Matrix mImageMatrix;
    private RectF mImageRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_RIGHT);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra(EXTRA_TITLE));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_clear_mtrl_alpha);
        url = intent.getStringExtra(EXTRA_IMAGE);
        image = (ImageView) findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(image);
//        mImageScale = mAttacher.getScale();
//        mImageMatrix = mAttacher.getDisplayMatrix();
//        mImageRect = mAttacher.getDisplayRect();
//        mAttacher.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
//            @Override
//            public void onMatrixChanged(RectF rectF) {
//                mImageScale = mAttacher.getScale();
//                mImageMatrix = mAttacher.getDisplayMatrix();
//                mImageRect = mAttacher.getDisplayRect();
//                Log.v("onMatrixChanged", "scale = "+mImageScale+"\nmatrix = "+mImageMatrix.toShortString());
//            }
//        });
//        ViewCompat.setTransitionName(image, EXTRA_IMAGE);
        load(url);
    }

    private void load(String url) {
        Picasso.with(this)
                .load(url)
                .into(image, new Callback() {
            @Override
            public void onSuccess() {
                mAttacher.update();
            }

            @Override
            public void onError() {

            }
        });
    }

    private void loadWithPlaceHolder(String url) {
        Picasso.with(this)
                .load(url)
                .placeholder(image.getDrawable())
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
//                        mImageRect = mAttacher.getDisplayRect();
//                        mImageScale = mAttacher.getScale();
//                        Log.v("onSucess", "scale = "+mImageScale);
                        mAttacher.update();
//                        Log.v("onSucess", "updated");
//                        mAttacher.setScale(mImageScale, true);
//                        Log.v("onSucess", "scale = "+mAttacher.getScale());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private void loadBig(String url) {
        String urlBig = url;
        if(!url.endsWith("_b.jpg")){
            urlBig = urlBig.replace(".jpg", "_b.jpg");
        }
        loadWithPlaceHolder(urlBig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_bigger){
            loadBig(url);
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(MainActivity activity, View transitionView, Photo photo) {
//        ActivityOptionsCompat options =
//                ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_IMAGE, photo.getStaticUrl());
        intent.putExtra(EXTRA_TITLE, photo.getTitle());
//        ActivityCompat.startActivity(activity, intent, options.toBundle());
        ActivityCompat.startActivity(activity, intent, Bundle.EMPTY);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
