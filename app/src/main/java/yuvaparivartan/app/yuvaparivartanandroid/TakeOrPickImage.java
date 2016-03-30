package yuvaparivartan.app.yuvaparivartanandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class TakeOrPickImage extends ActionBarActivity {





    public TextView camp_start_date, tentative_end_date, actual_end_date, tempDate;
    SpAdapter adapter;
    private Context context;
    private String last_selecteddate;
    private ArrayAdapter<String> spinnerAdapter;
    private JSONArray stateListArray;
    private JSONArray districtListArray;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor sharededitor;
    private JSONObject agendaInfo;
    private EditText name;
    private EditText address;
    private TextView save;
    private Spinner course;
    private TextView take_image;
    private TextView take;
    private TextView pick;
    private Uri fileUri;
    private File mediaFile;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 202;
    private int PICK_IMAGE_REQUEST = 1;
    private TextView done;

    public void commonInitialization()
    {
        context = TakeOrPickImage.this;

        take = (TextView) findViewById(R.id.take);
        pick = (TextView) findViewById(R.id.pick);
        done = (TextView) findViewById(R.id.done);
        
        sharedpreferences = getSharedPreferences("MyPref", 0);
        sharededitor = sharedpreferences.edit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_or_pick_image);
        commonInitialization();

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //currmme
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Uri.fromFile(getOutputMediaFileForMonitoring());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                new CommonFunction().resizeImage(mediaFile);

                ImageView image_to_show = (ImageView) findViewById(R.id.image_to_show);

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath(), bmOptions);
                image_to_show.setImageBitmap(bitmap);

            }
        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(getOutputMediaFileForMonitoring());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                    new CommonFunction().resizeImage(mediaFile);



                    ImageView image_to_show = (ImageView) findViewById(R.id.image_to_show);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap1 = BitmapFactory.decodeFile(mediaFile.getAbsolutePath(), bmOptions);
                    image_to_show.setImageBitmap(bitmap1);


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }




    }



    public File getOutputMediaFileForMonitoring(){

        String AppName = (String) context.getApplicationInfo().loadLabel(context.getPackageManager());
        String take_image_table_name = sharedpreferences.getString(new CommonFunction().take_image_table_name,"");
        String take_image_column_name = sharedpreferences.getString(new CommonFunction().take_image_column_name,"");
        String take_image_row_id = sharedpreferences.getString(new CommonFunction().take_image_row_id,"");

        File mediaStorageDir = null;
        try {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory(),AppName+"/"+take_image_table_name+"/"+take_image_column_name);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){


                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }

        try {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + take_image_row_id+".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaFile;
    }


}
