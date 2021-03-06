package com.porpeeranut.footanalysis2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by root on 3/4/2558.
 */
public class Dip {

    public static double TH = 0.7;

    public static void bit2mat(Bitmap in, double[][] out) {
        int h = in.getHeight();
        int w = in.getWidth();

        double mx = -9999;
        double mn = 9999;
        for (int i=0;i<h;i++) {
            for (int j = 0; j < w; j++) {
                int pixel = in.getPixel(j, i);
                int R = (pixel >> 16) & 0xff;
                int G = (pixel >> 8) & 0xff;
                int B = pixel & 0xff;

                double r = R / 255.0;
                double g = G / 255.0;
                double b = B / 255.0;

                out[i][j] = 2*g - r - b;
                out[i][j] = Math.max(out[i][j],0);
                mx = Math.max(out[i][j], mx);
                mn = Math.min(out[i][j], mn);

            }
        }
    }

    public static ArrayList<Point> findmark(Bitmap im) {
        int h = im.getHeight();
        int w = im.getWidth();
        Log.e("size", h+" "+w);
        double[][] mat = new double[h][w];

        bit2mat(im,mat);
        bwimageth(mat, TH, mat);

        //dilate(mat, 4);
        //dilate(mat, 4);

        int[][] label = new int[h][w];

        int c = find_components(mat, label);
        Log.e("compo",c+"");
        ArrayList<Point> res = new ArrayList<Point>();
        double[][] tmp = new double[h][w];
        int m1 = 0;
        int m2 = 0;
        int m3 = 0;
        Point p1 = new Point();
        Point p2 = new Point();
        Point p3 = new Point();
        if (c < 50) {
            for (int i=1;i<=c;i++) {
                bwimagev(label, i, tmp);
                int m00 = moment(tmp,0,0);
                int m01 = moment(tmp,0,1);
                int m10 = moment(tmp,1,0);
                if (m00>m1) {
                    p3.set(p2.x,p2.y);
                    m3 = m2;
                    p2.set(p1.x,p1.y);
                    m2 = m1;
                    p1.set(m01/m00, m10/m00);
                    m1 = m00;
                }
                else if (m00>m2) {
                    p3.set(p2.x,p2.y);
                    m3 = m2;
                    p2.set(m01/m00, m10/m00);
                    m2 = m00;
                }
                else if (m00>m3) {
                    p3.set(m01 / m00, m10 / m00);
                    m3 = m00;
                }
            }
            Log.e("position1", p1.x+" "+p1.y);
            Log.e("position2", p2.x+" "+p2.y);
            Log.e("position3", p3.x+" "+p3.y);
            res.add(p1);
            res.add(p2);
            res.add(p3);
        } else
            Log.v("error", "c > 50");
        return res;
    }

    public static Bitmap findmark2(Bitmap im) {
        int h = im.getHeight();
        int w = im.getWidth();
        Log.e("size", h+" "+w);
        double[][] mat = new double[h][w];

        bit2mat(im,mat);
        bwimageth(mat, TH, mat);

        //dilate(mat, 4);
        //dilate(mat, 4);

        int[][] label = new int[h][w];

        int c = find_components(mat, label);
        Log.e("eiei", c+"");
        return mat2bit(mat);
    }

    public static Bitmap mat2bit(double[][] mat) {
        int h = mat.length;
        int w = mat[0].length;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);

        for (int i=0;i<h;i++) {
            for (int j = 0; j < w; j++) {
                int gray = (int)(255*mat[i][j]);
                bmp.setPixel(j,i, Color.rgb(gray, gray, gray));
            }
        }
        return bmp;
    }

    public static void norm(double[][] mat, double mn, double mx) {
        for (int i=0;i<mat.length;i++) {
            for (int j=0;j<mat[i].length;j++) {
                mat[i][j] = (mat[i][j]-mn)/(mx-mn);
            }
        }
    }

    public static int sum(double[][] image) {
        int sum = 0;
        for (int i=0;i<image.length;i++) {
            for (int j=0;j<image[i].length;j++) {
                sum+=image[i][j];
            }
        }
        return sum;
    }

    public static void bwimageth(double[][] image, double th, double[][] out) {
        for (int i=0;i<image.length;i++) {
            for (int j=0;j<image[i].length;j++) {
                if (image[i][j]>th) out[i][j] = 1.0;
                else out[i][j] = 0.0;
            }
        }
    }

    public static void dfs(int[][] label, double[][] m, int x, int y, int current_label) {
        int dx[] = {+1, 0, -1, 0};
        int dy[] = {0, +1, 0, -1};
        int h = m.length;
        int w = m[0].length;

        if (x < 0 || x == h) return;
        if (y < 0 || y == w) return;
        if (label[x][y] > 0 || m[x][y] == 0) return;

        label[x][y] = current_label;

        for (int direction = 0; direction < 4; ++direction)
            dfs(label, m, x + dx[direction], y + dy[direction], current_label);
    }

    public static int find_components(double[][] image, int[][] label) {
        int h = image.length;
        int w = image[0].length;

        int component = 0;
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                if (label[i][j] == 0 && image[i][j] > 0) {
                    dfs(label, image, i, j, ++component);
                }
            }
        }
        return component;
    }

    public static int moment(double[][] mat, int p, int q) {
        int s = 0;
        for (int i=0;i<mat.length;i++) {
            for (int j=0;j<mat[i].length;j++) {
                s+=Math.pow(i,p)*Math.pow(j,q)*mat[i][j];
            }
        }
        return s;
    }

    public static void bwimagev(int[][] label, int v, double[][] out) {
        for (int i=0;i<label.length;i++) {
            for (int j=0;j<label[i].length;j++) {
                if (label[i][j]==v)
                    out[i][j] = 1.0;
                else
                    out[i][j] = 0.0;
            }
        }
    }
}
