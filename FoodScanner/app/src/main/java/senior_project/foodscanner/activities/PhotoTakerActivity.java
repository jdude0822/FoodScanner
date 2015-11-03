package senior_project.foodscanner.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileInputStream;

import senior_project.foodscanner.ImageDirectoryManager;
import senior_project.foodscanner.R;
import senior_project.foodscanner.ui.components.ErrorDialogFragment;
import senior_project.foodscanner.ui.components.ImageBrowser;

/**
 * Activity for taking an X number of pictures.
 * <p/>
 * You can indicate how many pictures to take and the name of each one by putting the extra EXTRA_PIC_NAMES with a String[] array.
 * Activity Defaults to taking one picture.
 * <p/>
 * To get the pictures that were taken you need to start this activity with startActivityForResult().
 * The result data Intent has the extra named EXTRA_IMAGE_FILES, which contains a File[] array for the image files.
 * It is recommended to delete these files after you are done with them.
 * <p/>
 * This class uses the old Camera api instead of Camera2 api, because Camera2 is for API level 21+.
 * <p/>
 * Features:
 * -Back/Cancel
 * -Navigate between pictures
 * -Take/Retake pictures
 * <p/>
 * User flow:
 * -Finish button is disabled
 * -Take picture
 * -Picture is displayed
 * -Cycle through pictures and take the rest of the pictures
 * -Finish button is enabled when all pictrues are taken
 */
public class PhotoTakerActivity extends AppCompatActivity implements ErrorDialogFragment.ErrorDialogListener, ImageBrowser.ActionButtonListener, ImageBrowser.FinishButtonListener {
    public static final String EXTRA_PIC_NAMES = "pic_names";
    public static final String RESULT_IMAGE_FILES = "image_files";

    private static final int RESULT_CAMERA = 0;
    private static final String SAVEINST_FILES = "picFiles";
    private static final String SAVEINST_INDEX = "current_index";
    private String[] picNames = {"Picture"};
    private ImageBrowser picBrowser;
    private File[] picFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra(EXTRA_PIC_NAMES)) {
            picNames = getIntent().getStringArrayExtra(EXTRA_PIC_NAMES);
        }
        picFiles = new File[picNames.length];

        setContentView(R.layout.activity_photo_taker);
        Bitmap defbmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
        Drawable defPic = new BitmapDrawable(getResources(), defbmp);
        picBrowser = new ImageBrowser(this, picNames, defPic);
        picBrowser.setActionButtonListener(this);
        picBrowser.setFinishButtonListener(this);
        picBrowser.setActionButtonText("Take Picture");
        picBrowser.setFinishButtonEnabled(false);
        ((FrameLayout) findViewById(R.id.container)).addView(picBrowser);
        picBrowser.setCurrentIndex(0);


        if(savedInstanceState != null) {
            // restore pictures taken
            picFiles = (File[]) savedInstanceState.getSerializable(SAVEINST_FILES);
            for(int i = 0; i < picFiles.length; i++) {
                if(picFiles[i] != null) {
                    picBrowser.setImage(i, imageFileToBitmapDrawable(picFiles[i]));
                }
            }
            picBrowser.setCurrentIndex(savedInstanceState.getInt(SAVEINST_INDEX));
            if(!picBrowser.containsNullImage()) {
                picBrowser.setFinishButtonEnabled(true);
            }
        } else {
            // clear image directory
            if(!ImageDirectoryManager.clearImageDirectory(this)) {
                ErrorDialogFragment.showErrorDialog(this, "Clear image directory failed.");
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(SAVEINST_FILES, picFiles);
        savedInstanceState.putInt(SAVEINST_INDEX, picBrowser.getCurrentIndex());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActionButton() {
        openCamera();
    }

    @Override
    public void onFinishButton() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_IMAGE_FILES, picFiles);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onErrorDialogClose() {
        finish();
    }


    private void openCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.EXTRA_FILENAME, picBrowser.getCurrentImageName());
        startActivityForResult(intent, RESULT_CAMERA);
    }

    private BitmapDrawable imageFileToBitmapDrawable(File f) {
        FileInputStream fis;
        BitmapDrawable bmp = null;
        try {
            fis = new FileInputStream(f);
            bmp = new BitmapDrawable(getResources(), fis);
            fis.close();
        } catch(java.io.IOException e) {
            Log.e("PhotoTakerActivity", "onActivityResult", e);
            ErrorDialogFragment.showErrorDialog(this, "Exception: " + e.getMessage());
        }
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RESULT_CAMERA:
                if(resultCode == RESULT_OK) {
                    //get result
                    File f = (File) data.getSerializableExtra(CameraActivity.RESULT_IMAGE_FILE);
                    picFiles[picBrowser.getCurrentIndex()] = f;

                    //update picBrowser
                    picBrowser.setImage(picBrowser.getCurrentIndex(), imageFileToBitmapDrawable(f));
                    if(!picBrowser.containsNullImage()) {
                        picBrowser.setFinishButtonEnabled(true);
                    }
                }
                break;
            default:
                break;
        }
    }

}
