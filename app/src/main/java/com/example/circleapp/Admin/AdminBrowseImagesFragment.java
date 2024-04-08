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

/**
 * This class is used to display all existing images in the admin interface (based on data in Firestore).
 */
public class AdminBrowseImagesFragment extends Fragment {
    ListView listView;
    Button backButton;
    ImageManager imageManager = new ImageManager(getActivity());
    ImageAdapter adapter;

    /**
     * Called to have the fragment instantiate its user interface view. The fragment uses a ListView
     * in combination with an instance of the ImageAdapter class to display all images retrieved from
     * the Firebase Cloud Storage. When admin clicks on an image item, it will prompt the admin to either
     * delete that image or dismiss the message.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views
     *                           in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should
     *                           be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     * @return                   The View for the fragment's UI, or null
     * @see ImageAdapter
     * @see ImageManager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_browse_images, container, false);
        listView = rootView.findViewById(R.id.list_view);
        backButton = rootView.findViewById(R.id.back_button);

        adapter = new ImageAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        loadPosters();
        loadPFPs();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Uri image = (Uri) parent.getItemAtPosition(position);
            imageClicked(image.toString());
        });

        backButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return rootView;
    }

    /**
     * Loads posters associated with events from Firebase Storage and updates the ListView.
     */
    private void loadPosters() {
        imageManager.getPosters(images -> {
            adapter.clear();
            adapter.addAll(images);
        });
    }

    /**
     * Loads profile pictures associated with users from Firebase Storage and updates the ListView.
     */
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
            imageManager.deleteImage(image, getContext());
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}