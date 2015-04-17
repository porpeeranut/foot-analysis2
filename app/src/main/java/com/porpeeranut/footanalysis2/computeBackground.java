package com.porpeeranut.footanalysis2;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class computeBackground extends AsyncTask<String, String, ArrayList<Double>> {
    Context context;
    ProgressDialog dialog;
    public AsyncResponse delegate = null;

    public computeBackground(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Loading", "Please wait...", true, false);
    }

    @Override
    protected ArrayList<Double> doInBackground(String... params) {
        ArrayList<Double> angles = new ArrayList<Double>();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        String mp4path = mediaStorageDir.getPath() + File.separator + "VID_foot.mp4";

        final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mp4path);
        ArrayList<Bitmap> bmFrame = new ArrayList<Bitmap>();
        long durationMs = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        for (int i = 0; i < durationMs; i += 500) {
            Bitmap tmp = mediaMetadataRetriever.getFrameAtTime(i * 1000);
            tmp = Bitmap.createScaledBitmap(tmp, tmp.getWidth() / 5, tmp.getHeight() / 5, false);
            ArrayList<Point> points = Dip.findmark(tmp);
            double angle;
            if (points != null) {
                angle = findAngle(points);
                angles.add(angle);
                Log.e("angle", ""+angle);
            }
            //bmFrame.add(mediaMetadataRetriever.getFrameAtTime(i * 1000));
        }

        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return angles;
    }

    private double findAngle(ArrayList<Point> points) {
        int x1 = points.get(0).x;
        int x2 = points.get(1).x;
        int x3 = points.get(2).x;
        int y1 = points.get(0).y;
        int y2 = points.get(1).y;
        int y3 = points.get(2).y;
        Point point1, point2, point3;
        double a, b, c, d, angle;

        if (Math.abs(x1-x2) < Math.abs(x2-x3)) {
            if (Math.abs(x1-x2) < Math.abs(x3-x1)) {
                // x1-x2 is min
                if (y1 > y2) {
                    point2 = points.get(0);
                    point1 = points.get(1);
                } else {
                    point2 = points.get(1);
                    point1 = points.get(0);
                }
                point3 = points.get(2);
            } else {
                // x3-x1 is min
                if (y1 > y3) {
                    point2 = points.get(0);
                    point1 = points.get(2);
                } else {
                    point2 = points.get(2);
                    point1 = points.get(0);
                }
                point3 = points.get(1);
            }
        } else {
            if (Math.abs(x2-x3) < Math.abs(x3-x1)) {
                // x2-x3 is min
                if (y2 > y3) {
                    point2 = points.get(1);
                    point1 = points.get(2);
                } else {
                    point2 = points.get(2);
                    point1 = points.get(1);
                }
                point3 = points.get(0);
            } else {
                // x3-x1 is min
                if (y1 > y3) {
                    point2 = points.get(0);
                    point1 = points.get(2);
                } else {
                    point2 = points.get(2);
                    point1 = points.get(0);
                }
                point3 = points.get(1);
            }
        }
        x1 = point1.x;
        x2 = point2.x;
        x3 = point3.x;
        y1 = point1.y;
        y2 = point2.y;
        y3 = point3.y;
        a = (x1-x2)*(x3-x2);
        b = (y1-y2)*(y3-y2);
        c = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
        d = Math.sqrt((x3-x2)*(x3-x2)+(y3-y2)*(y3-y2));
        angle = Math.acos((a+b)/(c*d)) * (180/Math.PI);
        return angle;
    }

    @Override
    protected void onPostExecute(ArrayList<Double> angles) {
        dialog.dismiss();
        delegate.processFinish(angles);
    }
}