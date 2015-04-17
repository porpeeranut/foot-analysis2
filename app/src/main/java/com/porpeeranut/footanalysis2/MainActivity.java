package com.porpeeranut.footanalysis2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements AsyncResponse {

    Activity act;
    ImageView imageView;
    Bitmap siden;
    Bitmap sideup;
    computeBackground asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this;
        asyncTask = new computeBackground(act);
        asyncTask.delegate = this;

        imageView = (ImageView) findViewById(R.id.imageView);
        Button btnRec = (Button) findViewById(R.id.btnRec);
        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageView.setImageBitmap(Dip.findmark2(siden));

                //Dip.findmark(siden);

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri fileUri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality (high at 1)

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            }
        });

        siden = BitmapFactory.decodeResource(getResources(), R.drawable.siden);
        siden = Bitmap.createScaledBitmap(siden, siden.getWidth() / 5, siden.getHeight() / 5, false);
        sideup = BitmapFactory.decodeResource(getResources(), R.drawable.sideup);
        sideup = Bitmap.createScaledBitmap(sideup, sideup.getWidth() / 5, sideup.getHeight() / 5, false);
        imageView.setImageBitmap(siden);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Uri videoUri = data.getData();
            //Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            //mVideoView.setVideoURI(videoUri);

            asyncTask.execute();

            /*runOnUiThread(new Runnable() {
                public void run() {
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
                    String mp4path = mediaStorageDir.getPath() + File.separator + "VID_foot.mp4";


                    final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(mp4path);
                    ArrayList<Bitmap> bmFrame = new ArrayList<Bitmap>();
                    long durationMs = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    for (int i = 0; i < durationMs; i += 500) {
                        Dip.findmark(siden);
                        //bmFrame.add(mediaMetadataRetriever.getFrameAtTime(i * 1000));
                    }
                    *//*imageView.post(new Runnable() {
                        public void run() {
                            imageView.setImageBitmap(mediaMetadataRetriever.getFrameAtTime(0 * 1000));
                        }
                    });*//*

                    Toast.makeText(act, "" + bmFrame.size(), Toast.LENGTH_LONG).show();
                }
            });*/
        }


    }

    private static Uri getOutputMediaFileUri(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camera", "failed to create directory");
                return null;
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_foot.mp4");
        return Uri.fromFile(mediaFile);
    }

    @Override
    public void processFinish(ArrayList<Double> angles) {
        Log.e("angle size", ""+angles.size());
    }
}
