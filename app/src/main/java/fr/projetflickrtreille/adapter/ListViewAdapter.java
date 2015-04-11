package fr.projetflickrtreille.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.projetflickrtreille.R;
import fr.projetflickrtreille.utils.Photo;

/**
 * Created by lheido on 26/11/14.
 */
public class ListViewAdapter<T> extends BaseAdapter {

    private ArrayList<T> mListItems;
    private final Context mContext;


    public ListViewAdapter(Context context, ArrayList<T> list) {
        mListItems = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public T getItem(int i) {
        return mListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        T elt = mListItems.get(i);
        ViewHolder holder;
        holder = new ViewHolder();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        holder.image = (ImageView) convertView.findViewById(R.id.image);
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.ownerName = (TextView) convertView.findViewById(R.id.owner_name);
        convertView.setTag(holder);

        holder.title.setText(((Photo)elt).getTitle());
        holder.ownerName.setText(((Photo)elt).getOwnerName());
        Picasso.with(mContext)
                .load(((Photo)elt).getStaticUrl())
                .fit()
                .centerCrop()
                .into(holder.image);
        return convertView;
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView ownerName;
    }

}
