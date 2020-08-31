package com.neethasolutions.camerafunpool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final String TAG = "MainActivity";

    private VideoWriter videoWriter;

    private int cameraFilterType = 0;
    private int cameraViewType=1;
    private  final Matrix mMatrix = new Matrix();
    String path;
    Button cartoonFilter;
    Button cameraView ;
    Button sketchFilter ;

    private boolean isRecord = false;

    Button capture;


    // Used to load the 'native-lib' library on application startup.

    private CameraBridgeViewBase cameraBridgeViewBase;

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if(status== LoaderCallbackInterface.SUCCESS){
                System.loadLibrary("native-lib");


                cameraBridgeViewBase.setCameraIndex(cameraBridgeViewBase.CAMERA_ID_FRONT);
                cameraBridgeViewBase.enableView();
            }
            else{
                super.onManagerConnected(status);
            }

        }
    };
    private String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Example of a call to a native method
         path = getExternalFilesDir(null).getPath();


        //Permission for Android 6+
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST);

         cartoonFilter = findViewById(R.id.cartoonfilter);
          cameraView = findViewById(R.id.cameraviewchange);
         sketchFilter = findViewById(R.id.sketchfilter);
         capture = findViewById(R.id.capture);





        cartoonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraFilterType=0;
                Log.e(TAG, "CARTOON FILTER");
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(capture.getText().toString().equals("Start")){



            //        videoWriter = new VideoWriter();
                //  videoWriter.open(nameVideo, VideoWriter.fourcc('M','J','P','G'),
              //        30.0,  new Size(cameraBridgeViewBase.getWidth(),cameraBridgeViewBase.getHeight()));

                    isRecord = true;

                    recordfilepath();
                    java.util.Date date= new java.util.Date();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());
                    File file = new File(recordfilepath(), "VID_" + timeStamp + ".avi");
                    Log.d(TAG, "file : " + file);
                    try {
                        if(!file.exists()){
                            file.createNewFile();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    filepath = file.getAbsolutePath();
                    Log.d("FilePath", "file PAth : " + filepath);

                    capture.setText("Stop");

                }

                else{
                   // videoWriter.release();
                    capture.setText("Start");
                    isRecord = false;
                }



                Log.e(TAG, "CAPTURE");
            }
        });

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraViewType==0){
                    cameraViewType=1;
                    cameraBridgeViewBase.disableView();
                    cameraBridgeViewBase.setCameraIndex(cameraBridgeViewBase.CAMERA_ID_FRONT);
                    cameraBridgeViewBase.enableView();

                    Log.e(TAG, "FRONT CAMERA");
                }
                else{
                    cameraViewType=0;
                    cameraBridgeViewBase.disableView();
                    cameraBridgeViewBase.setCameraIndex(cameraBridgeViewBase.CAMERA_ID_BACK);

                    cameraBridgeViewBase.enableView();
                    Log.e(TAG, "BACK CAMERA");
                }
            }
        });

        sketchFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
cameraFilterType=1;
                Log.e(TAG, "SKETCH FILTER");
            }
        });



        cameraBridgeViewBase = findViewById(R.id.main_surface);


        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    private String recordfilepath() {
        //        ongetTime();
        File sddir = Environment.getExternalStorageDirectory();
        File vrdir = new File(sddir, "CameraFun");
        if(!vrdir.exists()){
            vrdir.mkdir();
        }
        String mTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(vrdir, "KLI_" + mTimeStamp + ".avi");
        String filepath = file.getAbsolutePath();
        Log.e("debug mediarecorder", filepath);
        return filepath;
    }



        @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                cameraBridgeViewBase.setCameraPermissionGranted();
            } else {
                String message = "Camera permission was not granted";
                Log.e(TAG, message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "Unexpected permission request");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null)
            cameraBridgeViewBase.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null)
            cameraBridgeViewBase.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }



    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame frame) {
        // get current camera frame as OpenCV Mat object
        Mat mat = frame.rgba();





        // native call to process current camera frame
       //adaptiveThresholdFromJNI(mat.getNativeObjAddr());

        if(cameraFilterType==0){
            cartoonFilterFromJNI(mat.getNativeObjAddr());
        }
        else{
            sketchFilterFromJNI(mat.getNativeObjAddr());
        }



        //cartoonFilterFromJNI(mat.getNativeObjAddr());

        if (isRecord) {

            Log.w(TAG, "onCameraFrame: 录制");
            if (videoWriter == null) {
                Log.w(TAG, "onCameraFrame: 初始化");
                //'P','I','M','1'
                // 'M','P','E','G'
                // 'M','J','P','G'
                videoWriter = new VideoWriter(filepath, VideoWriter.fourcc('M', 'J', 'P', 'G'), 25.0D, mat.size());
                videoWriter.open(filepath, VideoWriter.fourcc('M', 'J', 'P', 'G'), 25.0D, mat.size());
                Log.i(TAG, "onCameraFrame: recordFilePath" + recordfilepath());
            }
            if (!videoWriter.isOpened()) {
                Log.w(TAG, "onCameraFrame: open");
                videoWriter.open(filepath, VideoWriter.fourcc('M', 'J', 'P', 'G'), 25.0D, mat.size());
            }

            videoWriter.write(mat);
            Log.w(TAG, "onCameraFrame: 写入中 " + mat.toString());
        } else {
            if (videoWriter != null) {
                videoWriter.release();

            }
        }






        // return processed frame for live preview
        return mat;
    }

    private native void adaptiveThresholdFromJNI(long mat);

    private native void cartoonFilterFromJNI(long mat);

    private native void sketchFilterFromJNI(long mat);


 }

