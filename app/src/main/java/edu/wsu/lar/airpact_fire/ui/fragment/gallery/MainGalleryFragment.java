// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import edu.wsu.lar.airpact_fire.app.manager.AppManager;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.ui.activity.GalleryActivity;
import lar.wsu.edu.airpact_fire.R;

public class MainGalleryFragment extends Fragment {

    private static final String sActionBarTitle = "Gallery";
    private static final int sColumnCount = 5;

    private AppManager mAppManager;
    private UserObject mUserObject;
    private List<PostObject> mPostObjects;

    private TableLayout mPostTableLayout;

    public MainGalleryFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((GalleryActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mAppManager = ((GalleryActivity) getActivity()).getAppManager();
        mUserObject = ((GalleryActivity) getActivity()).getUserObject();
        mPostObjects = mUserObject.getPosts();

        View view = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        mPostTableLayout = (TableLayout) view.findViewById(R.id.post_table_layout);

        populatePosts();

        return view;
    }

    void populatePosts() {

        if (mPostObjects == null) { return; }
        TableRow tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                )
        );

        for (PostObject postObject : mPostObjects) {

            // Compress post bitmap
            Bitmap postDisplayBitmap = postObject.getImageObjects().get(0).getImageBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            postDisplayBitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
            postDisplayBitmap = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(out.toByteArray()));

            // Resize bitmap
            int postWidth = mPostTableLayout.getWidth() / sColumnCount;
            int postHeight = (postWidth / postDisplayBitmap.getWidth())
                    * postDisplayBitmap.getHeight();
            //postDisplayBitmap = Bitmap.createScaledBitmap(
            //        postDisplayBitmap, postWidth, postHeight, false);

            // Add to a post view in the table row
            FrameLayout layout = (FrameLayout) getActivity().getLayoutInflater().inflate(
                    R.layout.layout_main_gallery_post, null, false);
            ImageView backgroundImageView = (ImageView) layout.findViewById(
                    R.id.background_image_view);
            backgroundImageView.setImageBitmap(postDisplayBitmap);
            tableRow.addView(layout);
        }
        mPostTableLayout.addView(tableRow);
    }
}
