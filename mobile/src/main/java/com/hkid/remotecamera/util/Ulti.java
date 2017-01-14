package com.hkid.remotecamera.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hkid.remotecamera.presenter.home.HomeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 4/19/15.
 */
public class Ulti {

    private static final String TAG = Ulti.class.getName();

    public static boolean isAppRunningBackground(Context context) {
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        // get the name of the package:
        String packageName = componentInfo.getPackageName();

        // Log.v(TAG, "TESTING value = " + "2012-2-27 10:46:00"
        // .compareTo("2012-2-29 10:14:00"));

        // check if the app's just come back from background
        if (!packageName.equals(context.getApplicationContext().getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean isAppOnTop(Context context){
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if(taskInfo.size() > 0){
            ComponentName componentInfo = taskInfo.get(0).topActivity;

            // get the name of the package:
            String packageName = componentInfo.getPackageName();

            if(context.getApplicationContext().getPackageName().equalsIgnoreCase(packageName) && componentInfo.getShortClassName().contains(HomeActivity.class.getSimpleName())){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }


    }

    public static void createFolder(String folderName){
        File mkDir = new File(folderName);
        if(!mkDir.exists()){
            mkDir.mkdirs();
        }
    }

    public static void saveDrawableToSdCard(Context context, int resourceId, String fileName){
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), resourceId);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, fileName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        releaseBitmap(bm);
    }

    public static int[] getScreenSize(Context context){
        int[] sizeOfScreen = new int[2];
        Point size = new Point();
        WindowManager w = ((Activity)context).getWindowManager();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            sizeOfScreen[0] = size.x;
            sizeOfScreen[1] = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            sizeOfScreen[0] = d.getWidth();
            sizeOfScreen[1] = d.getHeight();
        }

        return sizeOfScreen;
    }

    public static int getDimenValueFromDimenXML(Context context, int dimenId){
        return (int) (context.getResources().getDimension(dimenId) / context.getResources().getDisplayMetrics().density);
    }
    // dip to px
    public static int convertDensityToPixel(Context context, int dip) {
//		return (int) (dip * context.getResources().getDisplayMetrics().density);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dip * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // px to dip
    public static int convertPixelToDensity(Context context, int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixel, context.getResources().getDisplayMetrics());
    }

    public static int convertPixelsToDIP(Context context, int pixels) {
        // Resources resources = context.getResources();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = pixels / (metrics.densityDpi / 160f);
        return (int) dp;
        // DisplayMetrics displayMetrics =
        // context.getResources().getDisplayMetrics();
        // return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
        // pixels, displayMetrics);

    }

    // px to dp
    public static int convertPixelToDp(Context context, int pixel) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return (int) ((pixel / displayMetrics.density) + 0.5);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }

        return false;
    }

    public static void deleteFilesInFolder(String folderPath){
        File folder = new File(folderPath);
        try {
            File[] files = folder.listFiles();
            for (File file : files){
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static boolean deleteFile(String filePath, Context context){
        File file = new File(filePath);
        if(file.exists()){
            boolean isDeleted = file.delete();
            // request scan
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(filePath)));
            context.sendBroadcast(scanIntent);
            return isDeleted;
        }else {
            return true;
        }
    }

    public static boolean saveBitmapToSDCard(Bitmap bitmap, String filePath){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            releaseBitmap(bitmap);
        } finally {
            try {
                if (out != null) {
                    out.close();
                    releaseBitmap(bitmap);
                    return true;
                }
            } catch (IOException e) {
                releaseBitmap(bitmap);
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void releaseBitmap(Bitmap bitmap){
        try{
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public static Map.Entry getEntryOfHashMap(int index, LinkedHashMap<?, ?> hashMap){

        Iterator iterator = hashMap.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if(n == index){
                return entry;
            }
            n ++;
        }
        return null;
    }







    public static void generateKeyHash(Context context){
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo("com.viewnine.safeapp",  PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (android.content.pm.Signature signature : info.signatures)
        {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());

            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }



    public static void initRecorder(MediaRecorder mediaRecorder, SurfaceHolder surfaceHolder, Camera camera, int mCameraId, String fileName, Camera.Size sizeOfCamera){

        camera.unlock();
        try {
            camera.enableShutterSound(false);

        }catch (Exception e){
            e.printStackTrace();
        }
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        try {
            mediaRecorder.setProfile(CamcorderProfile.get(Constants.CAMERA_QUALITY));
        }catch (Exception e){
            e.printStackTrace();
        }

        int rotateVideo = Constants.POSITIVE_90_DEGREE;
        int videoBitRate = 0;
        if(mCameraId != Constants.DEFAULT_CAMERA){
            rotateVideo = Constants.DEGREE_270;
            videoBitRate = Constants.FRONT_CAMERA_BIT_RATE;
        }else {
            videoBitRate = Constants.BACK_CAMERA_BIT_RATE;
        }

        mediaRecorder.setVideoEncodingBitRate(videoBitRate);
        mediaRecorder.setOrientationHint(rotateVideo);
        mediaRecorder.setMaxDuration(Constants.DEFAULT_TIME_TO_RECORDING);
        mediaRecorder.setOutputFile(fileName);

        if(sizeOfCamera != null){
//            mediaRecorder.setVideoSize(sizeOfCamera.width, sizeOfCamera.height);
        }

    }




    public static Uri addVideoToMediaStore(Context context, File videoFile) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, videoFile.toString());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String getTimeFromMedia(Context context, File file){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long millis = Long.parseLong(time );

        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return hms;
    }
}
