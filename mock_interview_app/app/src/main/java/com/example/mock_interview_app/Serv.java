package com.example.mock_interview_app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Serv extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    String res = "";
    SharedPreferences sp;
    private Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();

//        takePhoto();



    }
    @Override
    public void onStart(Intent i, int startId) {

        //Toast.makeText(this, "Start Services", Toast.LENGTH_SHORT).show();
        handler.post(GpsFinder);





    }
    public Runnable GpsFinder = new Runnable() {

        @SuppressWarnings("deprecation")
        public void run() {

            int camId = -1;

            System.out.println("Preparing to take photo");
            Camera camera = null;
            sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 1; camIdx < cameraCount; camIdx++) {
                SystemClock.sleep(1000);

                Camera.getCameraInfo(camIdx, cameraInfo);
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    camId = camIdx;
                }

                try {
                    camera = Camera.open(camId);
                } catch (RuntimeException e) {
                    System.out.println("Camera not available: " + camId);
                    camera = null;
                    //e.printStackTrace();
                }
                try {
                    if (null == camera) {
                        System.out.println("Could not get camera instance");
                    } else {
                        System.out.println("Got the camera, creating the dummy surface texture");
                        //SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
                        try {
                            //camera.setPreviewTexture(dummySurfaceTextureF);
                            camera.setPreviewTexture(new SurfaceTexture(1));
                            camera.startPreview();
                        } catch (Exception e) {
                            System.out.println("Could not set the surface preview texture");
                            e.printStackTrace();
                        }
                        camera.takePicture(null, null, new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                File pictureFileDir = getExternalFilesDir("");
                                if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                                    return;
                                }
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                                String date = dateFormat.format(new Date());
                                String photoFile = "PictureFront_"+".jpg";

                                SharedPreferences.Editor ed=sp.edit();
                                ed.putString("fl",photoFile);
                                ed.commit();
//                            String filename = pictureFileDir.getPath() + File.separator + photoFile;
                                String filename=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+photoFile;
                                File mainPicture = new File(filename);
                                try {
                                    FileOutputStream fos = new FileOutputStream(mainPicture);
                                    fos.write(data);
                                    fos.close();
                                    res = uploadFile(filename);
                                    System.out.println("image saved"+filename);

                                } catch (Exception error) {
                                    System.out.println("Image could not be saved"+error);
                                }
                                camera.release();
                            }
                        });
                    }
                } catch (Exception e) {
                    camera.release();
                }


            }
            handler.postDelayed(GpsFinder, 50000);// register again to start after 20 seconds...
        }



    };


//    private void takePhoto() {
//        int camId = -1;
//
//        System.out.println("Preparing to take photo");
//        Camera camera = null;
//        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        int cameraCount = 0;
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        cameraCount = Camera.getNumberOfCameras();
//        for (int camIdx = 1; camIdx < cameraCount; camIdx++) {
//            SystemClock.sleep(1000);
//
//            Camera.getCameraInfo(camIdx, cameraInfo);
//            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
//                camId = camIdx;
//            }
//
//            try {
//                camera = Camera.open(camId);
//            } catch (RuntimeException e) {
//                System.out.println("Camera not available: " + camId);
//                camera = null;
//                //e.printStackTrace();
//            }
//            try {
//                if (null == camera) {
//                    System.out.println("Could not get camera instance");
//                } else {
//                    System.out.println("Got the camera, creating the dummy surface texture");
//                    //SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
//                    try {
//                        //camera.setPreviewTexture(dummySurfaceTextureF);
//                        camera.setPreviewTexture(new SurfaceTexture(1));
//                        camera.startPreview();
//                    } catch (Exception e) {
//                        System.out.println("Could not set the surface preview texture");
//                        e.printStackTrace();
//                    }
//                    camera.takePicture(null, null, new Camera.PictureCallback() {
//
//                        @Override
//                        public void onPictureTaken(byte[] data, Camera camera) {
//                            File pictureFileDir = getExternalFilesDir("");
//                            if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
//                                return;
//                            }
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
//                            String date = dateFormat.format(new Date());
//                            String photoFile = "temp"+".jpg";
//
//                            SharedPreferences.Editor ed=sp.edit();
//                            ed.putString("fl",photoFile);
//                            ed.commit();
////                            String filename = pictureFileDir.getPath() + File.separator + photoFile;
//                            String filename=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+photoFile;
//                         File mainPicture = new File(filename);
//                            try {
//                                FileOutputStream fos = new FileOutputStream(mainPicture);
//                                fos.write(data);
//                                fos.close();
//                                res = uploadFile(filename);
//                                System.out.println("image saved"+filename);
//
//                            } catch (Exception error) {
//                                System.out.println("Image could not be saved"+error);
//                            }
//                            camera.release();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                camera.release();
//            }
//
//
//        }
//
//    }




    public String uploadFile(String sourceFileUri) {
//        try {
//            String fileName;
//            fileName = sourceFileUri;
//            String upLoadServerUri = "http://" + sp.getString("ip","")+ ":5000/capture";
//            FileUpload fp = new FileUpload(fileName);
//            Map mp = new HashMap<String, String>();
//
//            String res= fp.multipartRequest(upLoadServerUri, mp, fileName, "files", "application/octet-stream");
//            return res;
//
//        } catch (Exception e) {
//
////            Toast.makeText(getApplicationContext(),"error"+e,Toast.LENGTH_LONG).show();
            return "err";
//        }
    }

}
