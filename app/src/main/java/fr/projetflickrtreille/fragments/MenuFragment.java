package fr.projetflickrtreille.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import fr.projetflickrtreille.R;
import fr.projetflickrtreille.utils.UserPref;

/**
 * Created by lheido on 06/01/15.
 */
public class MenuFragment extends Fragment {

    private CheckedTextView settingsTagsOrTextCheckedTextView;
    private CheckedTextView settingsHdImageCheckedTextView;
    private CheckedTextView settingsAutoLoginCheckedTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        settingsTagsOrTextCheckedTextView = (CheckedTextView) view.findViewById(R.id.settings_tags_or_text);
        settingsTagsOrTextCheckedTextView.setChecked(UserPref.useTextInsteadOfTagsEnabled(getActivity().getApplicationContext()));
        settingsTagsOrTextCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsTagsOrTextCheckedTextView.toggle();
                UserPref.setTagsOrText(getActivity().getApplicationContext(),
                        settingsTagsOrTextCheckedTextView.isChecked());
            }
        });
        settingsHdImageCheckedTextView = (CheckedTextView) view.findViewById(R.id.settings_hd_image);
        settingsHdImageCheckedTextView.setChecked(UserPref.useHdImageEnabled(getActivity().getApplicationContext()));
        settingsHdImageCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsHdImageCheckedTextView.toggle();
                UserPref.setUseHdImage(getActivity().getApplicationContext(),
                        settingsHdImageCheckedTextView.isChecked());
            }
        });
        settingsAutoLoginCheckedTextView = (CheckedTextView) view.findViewById(R.id.settings_auto_login);
        settingsAutoLoginCheckedTextView.setChecked(UserPref.autoLoginEnabled(getActivity().getApplicationContext()));
        settingsAutoLoginCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsAutoLoginCheckedTextView.toggle();
                UserPref.setAutoLoginEnabled(getActivity().getApplicationContext(),
                        settingsAutoLoginCheckedTextView.isChecked());
            }
        });
        return view;
    }
}
