// Copyright © 2017,
// Laboratory for Atmospheric Research at Washington State University,
// All rights reserved.

package edu.wsu.lar.airpact_fire.ui.fragment.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import edu.wsu.lar.airpact_fire.data.object.PostObject;
import edu.wsu.lar.airpact_fire.data.object.UserObject;
import edu.wsu.lar.airpact_fire.ui.activity.GalleryActivity;
import lar.wsu.edu.airpact_fire.R;

public class MainGalleryFragment extends Fragment {

    private static final String sActionBarTitle = "Gallery";
    private static final int sColumnCount = 2;

    private UserObject mUserObject;
    private List<PostObject> mPostObjects;

    private TextView mPostCountTextView;
    private TextView mSubmissionCountTextView;
    private TextView mQueueCountTextView;
    private TableLayout mPostTableLayout;

    public MainGalleryFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        ((GalleryActivity) getActivity()).setActionBarTitle(sActionBarTitle);

        mUserObject = ((GalleryActivity) getActivity()).getUserObject();
        mPostObjects = mUserObject.getPosts();

        View view = inflater.inflate(R.layout.fragment_main_gallery, container, false);
        mPostCountTextView = (TextView) view.findViewById(R.id.post_count_text_view);
        mSubmissionCountTextView = (TextView) view.findViewById(R.id.submission_count_text_view);
        mQueueCountTextView = (TextView) view.findViewById(R.id.queue_count_text_view);
        mPostTableLayout = (TableLayout) view.findViewById(R.id.post_table_layout);

        // Populate posts once table has been drawn
        ViewTreeObserver viewTreeObserver = mPostTableLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPostTableLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                populatePosts();
            }
        });

        return view;
    }

    private void populatePosts() {

        if (mPostObjects == null) { return; }

        int submissionCount = 0;
        int queueCount = 0;
        int postCount = 0;

        int postWidth = Math.round(mPostTableLayout.getWidth() / (float) sColumnCount); // dp

        TableRow tableRow = new TableRow(getActivity());
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        mPostTableLayout.addView(tableRow);

        for (final PostObject postObject : mPostObjects) {

            if (postObject.getMode() == 1) continue; // Skip drafts
            else if (postObject.getMode() == 2) queueCount++;
            else if (postObject.getMode() == 3) submissionCount++;
            postCount++;

            // Add row if full
            if ((postCount - 1) % sColumnCount == 0) {
                tableRow = new TableRow(getActivity());
                tableRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));
                mPostTableLayout.addView(tableRow);
            }

            // Compress post bitmap
            Bitmap postDisplayBitmap = postObject.getThumbnail();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            postDisplayBitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
            postDisplayBitmap = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(out.toByteArray()));

            // Resize bitmap
            int postHeight = Math.round(postWidth / (float) postDisplayBitmap.getWidth()
                    * postDisplayBitmap.getHeight());
            postDisplayBitmap = Bitmap.createScaledBitmap(
                    postDisplayBitmap, postWidth, postHeight, false);

            // Add to a post view in the table row
            FrameLayout postLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(
                    R.layout.layout_main_gallery_post, null, false);
            ImageView backgroundImageView = (ImageView) postLayout.findViewById(
                    R.id.background_image_view);
            backgroundImageView.setImageBitmap(postDisplayBitmap);

            // React to pressing on image
            backgroundImageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            // Set overlay
                            ImageView view = (ImageView) v;
                            view.getDrawable().setColorFilter(0x33000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            // Clear overlay
                            ImageView view = (ImageView) v;
                            view.getDrawable().clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }

                    return false;
                }
            });

            // Open post details when image clicked
            backgroundImageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Fragment postDetailsFragment = new GalleryPostDetailsFragment();
                    ((GalleryPostDetailsFragment) postDetailsFragment).setPostObject(postObject);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.gallery_container, postDetailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

            // Add to row
            tableRow.addView(postLayout);
        }

        // Update count banner
        mPostCountTextView.setText(postCount + " POSTS");
        mSubmissionCountTextView.setText(submissionCount + " SUBMITTED");
        mQueueCountTextView.setText(queueCount + " QUEUED");
    }
}
