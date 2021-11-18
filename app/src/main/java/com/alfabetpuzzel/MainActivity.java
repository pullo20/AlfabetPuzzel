package com.alfabetpuzzel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.alfabetpuzzel.Model.MyItem;
import com.alfabetpuzzel.adapter.ImageRecyclerAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<Drawable> savedPhotos = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();
    private int mGridRows;
    private boolean defaultAdapter;
    private static int REQUEST_PHOTO_CROPPING = 1;

    /**
     * Scale an image to the size of a view, and rotate 90 degrees to obtain the image in portrait orientation
     * @param viewSize the size of the view for the image to be scaled to
     * @param photopath the file path of the image to be scaled
     * @return a Drawable of the given image scaled to a size suitable to fit into the target view
     */
    private Drawable scalePhoto(int viewSize, String photopath) {
        // scale image previews to fit the allocated View to save app memory
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photopath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/viewSize, photoH/viewSize);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(photopath, bmOptions);
        return new BitmapDrawable(getResources(), bitmap);
    }

    /**
     * Setup Buttons and their onClickListeners that allow the user to choose settings, take a photo, and start a puzzle.
     * Also setup the recycler view which displays image choices for the puzzle.
     * @param savedInstanceState get previously saved activity instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final int[] drawableInts = {
//                R.drawable.dfdfdefaultgrid, R.drawable.dfdfcarpet, R.drawable.dfdfcat, R.drawable.dfdfclock,
//                R.drawable.dfdfcrab, R.drawable.dfdfdarklights, R.drawable.dfdfnendou,
//                R.drawable.dfdfrazer, R.drawable.dfdfsaiki, R.drawable.dfdfmms
//        };

        getSavedPhotos();
        mRecyclerView = findViewById(R.id.pictureRecyclerView);
        // improves performance given that recycler does not change size based on its contents (the images)
        mRecyclerView.setHasFixedSize(true);
        int orientation = getResources().getConfiguration().orientation;
        // use layout manager -  horizontal orientation = 0, vertical = 1
        RecyclerView.LayoutManager layoutManager;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            layoutManager = new LinearLayoutManager(this, 1, false);

//        } else {
            layoutManager = new GridLayoutManager(this,2);
//        }
        mRecyclerView.setLayoutManager(layoutManager);
        // set adapter to default use default image dataset
        final ImageRecyclerAdapter testAdapter = new ImageRecyclerAdapter(MyItem.iconList,this);
        mRecyclerView.setAdapter(testAdapter);
        defaultAdapter = true;
        // toggle recycler view between default images and photos taken and saved using this app
        ToggleButton adapterButton = findViewById(R.id.adapterButton);
        final ImageRecyclerAdapter photoAdapter = new ImageRecyclerAdapter(savedPhotos, this);
        //TODO: could use a grid layout manager to allow for a grid in recycler view rather than list (or a choice)

        // set button listener to change between datasets (defaults or photos) for the recycler view
        adapterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defaultAdapter = !defaultAdapter;  // update boolean to track which dataset is displayed
                if (isChecked) {  // recycler displaying default images -> change to app photo gallery
                    mRecyclerView.swapAdapter(photoAdapter, true);
//                    testAdapter.resetSelection();
                    // inform adapter of dataset change to defaults
                    photoAdapter.notifyDataSetChanged();
                    photoAdapter.setIsDefaultImages();  // update boolean which tells adapter which dataset is shown
                } else {  // recycler displaying app gallery -> defaults
                    mRecyclerView.swapAdapter(testAdapter, true);
//                    testAdapter.resetSelection();
                    testAdapter.notifyDataSetChanged();
                    testAdapter.setIsDefaultImages();
                }
            }
        });

        // move to cropper activity to crop gallery images or take photo with camera
        Button cameraGalleryButton = findViewById(R.id.photoCropButton);
        final Intent cropperIntent = new Intent(this, com.alfabetpuzzel.PhotoCropping.class);
        cameraGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(cropperIntent, REQUEST_PHOTO_CROPPING);
            }
        });

        final RadioGroup setGrid = findViewById(R.id.setGrid);
        setGrid.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int x = 0; x<4; x++) {
                    RadioButton radioButton = (RadioButton)group.getChildAt(x);
                    if (radioButton.getId() == checkedId) {
                        mGridRows = x + 3;
                        testAdapter.setmGridRows(x + 3);
                        break;
                    }
                }
            }
        });

        final Intent gameIntent = new Intent(this, com.alfabetpuzzel.PuzzleActivity.class);
        mGridRows = 4; // default amount of grid rows is 4
        final int[] defaultPuzzles = {R.drawable.grid9, R.drawable.grid15, R.drawable.grid25, R.drawable.grid36};

        // on click listener for the load button creates an intent to start the game activity and sets extras to give
        // that activity the information of grid size and image to use
        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameIntent.putExtra("numColumns", mGridRows);  // set extra for grid size
                // remove any previous extra so the game activity does not use it instead of the intended image/photo
                gameIntent.removeExtra("photoPath");  // app taken photo
                gameIntent.removeExtra("appPhotoPath"); // saved photos
                gameIntent.removeExtra("drawableId");  // default images
                 // get drawable id or photo path for selected image from recycler view adapter
                int selectedImage = testAdapter.getSelection();
                // if no selection, check for selected grid size to send the appropriate default image
                if (selectedImage == -1) {
                    gameIntent.putExtra("drawableId", defaultPuzzles[mGridRows - 3]);  // 3x3 grid is index 0 in array
                } else {  // there is a selected item from whichever dataset is displayed
                    if (defaultAdapter) {  // selection is from default images
                        // if there is a selection, send the id and puzzle number to the game activity
                        gameIntent.putExtra("drawableId", MyItem.iconList[selectedImage]);
                        gameIntent.putExtra("puzzleNum", selectedImage);
                    } else {  // selection is from app photos
                        //TODO: send photo path, with no puzzlenum extra as savefile does not support it
                        gameIntent.putExtra("appPhotoPath", photoPaths.get(selectedImage));
                        gameIntent.putExtra("puzzleNum", -1);
                    }
                }
                startActivity(gameIntent);  // start game activity
            }
        });

    }

    /**
     * Search the app picture directory for photos of .jpg or .png type and store them in instance variables as
     * drawables (image) and strings (filepath) which can be used to display the photos and send to the Puzzle Activity
     */
    private void getSavedPhotos() {
        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] storedImages = imageDir.listFiles();  //TODO: why warning - even when no files no exception
        if (storedImages == null) {
            return;
        }
        float density = getResources().getDisplayMetrics().density;
        long recyclerViewPx = Math.round(150 * density);
        // checks for files in the apps image directory which are not of the jpg or png type
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                boolean acceptedType = false;
                String pathName = pathname.getName();
                if (pathName.endsWith(".jpg") || pathName.endsWith(".png"))
                    acceptedType = true;
                return acceptedType;
            }
        };

        for (File file : storedImages) {
            // check for empty or wrong file types and delete them
            if (file.length() == 0 || !fileFilter.accept(file)) {
                boolean deletedFile = file.delete();
            } else {
                String imagePath = file.getAbsolutePath();
                photoPaths.add(imagePath);
                Drawable imageBitmap = scalePhoto((int)recyclerViewPx, imagePath);
                savedPhotos.add(imageBitmap);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: start photocropping with code then when press back set photo data as list of paths then update here
        //  also consider case where cropper goes to game, then back to here directly from solved UI
        Log.i(TAG, "requestCode: "+requestCode);
        Log.i(TAG, "resultCode: "+resultCode);
        if (requestCode == REQUEST_PHOTO_CROPPING && resultCode == RESULT_OK && data != null) {
            // must update recycler view adapter for photos, as new photos may have been loaded, notify adapter data changed
            ArrayList<String> newPhotos= data.getStringArrayListExtra("savedPhotos");
            float density = getResources().getDisplayMetrics().density;
            long recyclerViewPx = Math.round(150 * density);
            // create bitmap for each new photo and add to adapters data set
            for (String photoPath : newPhotos) {
                if (photoPath != null) {  // just to be sure no null paths sneak in
                    photoPaths.add(photoPath);
                    Drawable imageBitmap = scalePhoto((int)recyclerViewPx, photoPath);
                    savedPhotos.add(imageBitmap);
                }
            }
            if (mRecyclerView.getAdapter() != null) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
