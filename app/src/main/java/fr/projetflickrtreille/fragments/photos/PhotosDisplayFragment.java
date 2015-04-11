package fr.projetflickrtreille.fragments.photos;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nirhart.parallaxscroll.views.ParallaxListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.projetflickrtreille.DetailActivity;
import fr.projetflickrtreille.MainActivity;
import fr.projetflickrtreille.R;
import fr.projetflickrtreille.adapter.ListViewAdapter;
import fr.projetflickrtreille.flickr.FlickrRestApi;
import fr.projetflickrtreille.flickr.FlickrTask;
import fr.projetflickrtreille.utils.Photo;

/**
 * Created by lheido on 13/12/14.
 */
public abstract  class PhotosDisplayFragment extends Fragment
        implements AdapterView.OnItemClickListener, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String CURRENT_TOP_ITEM = "current_top_item";
    private ArrayList<Photo> mListItem;
    private ListViewAdapter<Photo> mAdapter;
    private Photo firstItem;
    private ParallaxListView listView;
    private View headerView;
    private String tags;
    private SwipeRefreshLayout swipeLayout;
    private int currentIndex;
    private ImageButton actionButton;
    private boolean isButtonAnimationUp;

    public static PhotosDisplayFragment newInstance(final String tags, final ArrayList<Photo> data){
        return new PhotosDisplayFragment() {
            @Override
            public void setUp() {
                setTags(tags);
                populateListItem(data);
            }
        };
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_TOP_ITEM, listView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListItem = new ArrayList<>();
        setUp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_photos_display, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        actionButton = (ImageButton) view.findViewById(R.id.action_button);
        actionButton.setTranslationY(getTranslationValue(getActivity().getApplicationContext()));
        actionButton.setVisibility(View.GONE);
        actionButton.setOnClickListener(this);
        actionButton.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (isButtonAnimationUp) {
                    actionButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isButtonAnimationUp) {
                    actionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        listView = (ParallaxListView) view.findViewById(R.id.list_view);
        mAdapter = new ListViewAdapter<>(getActivity().getApplicationContext(), mListItem);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int lastVisible = listView.getLastVisiblePosition();
                    if (lastVisible == mListItem.size()) {
                        if(actionButton.getVisibility() == View.GONE) {
                            isButtonAnimationUp = true;
                            actionButton.animate().translationYBy(-getTranslationValue(getActivity().getApplicationContext()));
                        }
                    } else {
                        if(actionButton.getVisibility() == View.VISIBLE) {
                            isButtonAnimationUp = false;
                            actionButton.animate().translationYBy(getTranslationValue(getActivity().getApplicationContext()));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i2, int i3) {}
        });
        updateListView();
        if(savedInstanceState != null && savedInstanceState.containsKey(CURRENT_TOP_ITEM)){
            currentIndex = savedInstanceState.getInt(CURRENT_TOP_ITEM);
            listView.post( new Runnable() {
                @Override
                public void run() {
                    listView.setSelection(currentIndex + 1);
                }
            });
        }
        return view;
    }

    public static int getTranslationValue(Context context){
        return context.getResources().getDimensionPixelSize(R.dimen.action_button_diameter)+
                context.getResources().getDimensionPixelSize(R.dimen.action_button_margin);
    }

    public void populateListItem(ArrayList<Photo> mListItem) {
        this.mListItem.clear();
        this.mListItem.addAll(mListItem);
        this.firstItem = this.mListItem.remove(0);
    }

    public void updateListView(){
        updateHeader();
        this.mAdapter.notifyDataSetChanged();
    }

    public void updateHeader(){
        listView.removeHeaderView(headerView);
        listView.setAdapter(null);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.item_header, listView, false);
        listView.addParallaxedHeaderView(headerView);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        headerView.setOnClickListener(this);
        TextView title = (TextView)headerView.findViewById(R.id.title);
        title.setText(firstItem.getTitle());
        TextView ownerName = (TextView)headerView.findViewById(R.id.owner_name);
        ownerName.setText(firstItem.getOwnerName());
        ImageView headerImage = (ImageView) headerView.findViewById(R.id.image);
        Picasso.with(getActivity().getApplicationContext())
                .load(firstItem.getStaticUrl())
                .fit()
                .centerCrop()
                .into(headerImage);
    }

    public abstract void setUp();

    @Override
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
        launchDetail(v.findViewById(R.id.image), mListItem.get(position - 1));
    }

    @Override
    public void onClick(View view) {
        if(view instanceof ImageButton){
            loadMore();
        } else {
            launchDetail(headerView.findViewById(R.id.image), firstItem);
        }
    }

    private void launchDetail(View transitionView, Photo photo){
        DetailActivity.launch(((MainActivity) getActivity()), transitionView, photo);
    }

    public void loadMore(){
        FlickrTask.Search.getPhotosFromPage(getActivity().getApplicationContext(), tags,
                FlickrRestApi.retrievePagesLoaded(mListItem.size())+1,
                new FlickrTask.Search.SearchCallback<Photo>() {
                    @Override
                    public void listIsPopulated(ArrayList<Photo> list) {
                        mListItem.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(listView.getLastVisiblePosition()+1);
                    }

                    @Override
                    public void listIsEmpty() {
                        Toast.makeText(getActivity().getApplicationContext(),R.string.no_result,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        FlickrTask.Search.getPhotos(getActivity().getApplicationContext(), tags,
                new FlickrTask.Search.SearchCallback<Photo>() {
                    @Override
                    public void listIsPopulated(ArrayList<Photo> list) {
                        populateListItem(list);
                        updateListView();
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void listIsEmpty() {
                        swipeLayout.setRefreshing(false);
                        Toast.makeText(
                                getActivity().getApplicationContext(), R.string.no_result,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
