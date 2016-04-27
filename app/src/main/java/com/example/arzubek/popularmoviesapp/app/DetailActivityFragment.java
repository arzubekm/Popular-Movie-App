package com.example.arzubek.popularmoviesapp.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private String mdOrgTitle;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mdOrgTitle = intent.getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.original_title_id))
                    .setText(mdOrgTitle);
            ((TextView) rootView. findViewById(R.id.release_date_id))
                    .setText(intent.getStringExtra((Intent.EXTRA_REFERRER_NAME)));
            ((TextView) rootView. findViewById(R.id.vote_average_id))
                    .setText(intent.getStringExtra((Intent.EXTRA_SHORTCUT_NAME)));
            ((TextView) rootView. findViewById(R.id.overview_id))
                    .setText(intent.getStringExtra((Intent.EXTRA_INSTALLER_PACKAGE_NAME)));
        }
        String url = intent.getStringExtra(Intent.EXTRA_REFERRER);
        PosterImageView imgView = (PosterImageView) rootView.findViewById(R.id.poster_path_id);
        Context context = getActivity();
        Picasso.with(context).load(url).into(imgView);
        return rootView;
    }
}
