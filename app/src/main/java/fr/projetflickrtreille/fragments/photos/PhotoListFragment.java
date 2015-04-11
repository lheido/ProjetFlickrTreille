package fr.projetflickrtreille.fragments.photos;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import fr.projetflickrtreille.DetailActivity;
import fr.projetflickrtreille.MainActivity;
import fr.projetflickrtreille.R;
import fr.projetflickrtreille.adapter.ListViewAdapter;
import fr.projetflickrtreille.utils.Photo;

/**
 * Created by lheido on 02/12/14.
 */
public abstract class PhotoListFragment extends ListFragment {

    private ArrayList<Photo> mListItem;
    private ListViewAdapter<Photo> mAdapter;

    public static PhotoListFragment newInstance(final ArrayList<Photo> data){
        return new PhotoListFragment() {
            @Override
            protected void setUp() {
                populateListItem(data);
            }
        };
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListItem = new ArrayList<>();
        mAdapter = new ListViewAdapter<>(getActivity().getApplicationContext(), mListItem);
        setUp();
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DetailActivity.launch(((MainActivity)getActivity()), v.findViewById(R.id.image), mListItem.get(position));
    }

    public void populateListItem(ArrayList<Photo> mListItem) {
        this.mListItem.clear();
        this.mListItem.addAll(mListItem);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mAdapter.notifyDataSetChanged();
    }

    protected abstract void setUp();
}
