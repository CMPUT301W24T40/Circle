package com.example.circleapp.Admin;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.circleapp.Firebase.ImageManager;
import com.example.circleapp.R;

import java.util.ArrayList;

public class AdminBrowseImagesFragment extends Fragment {
    ListView listView;
    Button backButton;
    ImageManager imageManager = new ImageManager(getActivity());
    ImageAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_browse_images, container, false);
        listView = rootView.findViewById(R.id.list_view);
        backButton = rootView.findViewById(R.id.back_button);

        adapter = new ImageAdapter(getContext(), new ArrayList<>()); // Initialize adapter
        listView.setAdapter(adapter);

        loadPosters();
        loadPFPs();

        // ListView item click listener
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Uri image = (Uri) parent.getItemAtPosition(position);
            imageClicked(image.toString());
        });

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return rootView;
    }

    /**
     * Loads images from Firebase Storage and updates the ListView.
     */
    private void loadPosters() {
        imageManager.getPosters(images -> {
            adapter.clear();
            adapter.addAll(images);
        });
    }

    private void loadPFPs() {
        imageManager.getPFPs(images -> adapter.addAll(images));
    }

    /**
     * Handles clicks on images.
     *
     * @param image The clicked image
     */
    private void imageClicked(String image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Select one of the following options:");
        builder.setPositiveButton("Delete image", (dialog, which) -> {
            imageManager.deleteImage(image);
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}