

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <jni.h>
#include <android/log.h>
#include <string>

#define TAG "NativeLib"
extern "C"
JNIEXPORT void JNICALL
Java_com_neethasolutions_camerafunpool_MainActivity_adaptiveThresholdFromJNI(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong matAddr) {


        // get Mat from raw address
        cv::Mat &mat = *(cv::Mat *) matAddr;
    cv::cvtColor(mat,mat,cv::COLOR_RGB2GRAY);

        clock_t begin = clock();

        cv::adaptiveThreshold(mat, mat, 255, cv::ADAPTIVE_THRESH_MEAN_C, cv::THRESH_BINARY_INV, 21, 5);

        // log computation time to Android Logcat
        double totalTime = double(clock() - begin) / CLOCKS_PER_SEC;
        __android_log_print(ANDROID_LOG_INFO, TAG, "adaptiveThreshold computation time = %f seconds\n",
                            totalTime);
    // TODO: implement adaptiveThresholdFromJNI()
}

    extern "C"
    JNIEXPORT void JNICALL
    Java_com_neethasolutions_camerafunpool_MainActivity_cartoonFilterFromJNI(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong matAddr) {
        cv::Mat &originalimage = *(cv::Mat *) matAddr;

        cv::cvtColor(originalimage,originalimage,cv::COLOR_RGB2BGR);

   /*     int count = 7;
        while(true){

            cv::bilateralFilter(originalimage,originalimage,9,9,7);

            if(count==0){
                break;
            }
                count=count-1;
        }*/

        cv::Mat result ;
        cv::cvtColor(originalimage,result,cv::COLOR_BGR2GRAY);
        cv::medianBlur(result,result,7);
        cv::adaptiveThreshold( result, result, 255, cv::ADAPTIVE_THRESH_MEAN_C, cv::THRESH_BINARY, 9, 2);
        cv::cvtColor(result,result,cv::COLOR_GRAY2BGR);



        cv::bitwise_and(originalimage,result,originalimage);
        cv::cvtColor(originalimage,originalimage,cv::COLOR_BGR2RGB);

    }




    extern "C"
    JNIEXPORT void JNICALL
    Java_com_neethasolutions_camerafunpool_MainActivity_sketchFilterFromJNI(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong matAddr) {
        // TODO: implement sketchFilterFromJNI()
        cv::Mat &originalimage = *(cv::Mat *) matAddr;




        cv::Mat canvasBlend ;

        cv::Mat imageCartoon;

        cv::Mat sketchImage;

        cv::Mat imageGrey;

        cv::cvtColor(originalimage,originalimage,cv::COLOR_RGB2BGR);

        cv::cvtColor(originalimage,imageGrey,cv::COLOR_BGR2GRAY);

        cv::Mat inverse;


//cv::bitwise_not(imageGrey,inverse);--> same as below
        inverse = 255- imageGrey;

        cv::Mat blurImage;

        cv::GaussianBlur(inverse,blurImage,cv::Size(21,21),0,0);

        cv::divide(imageGrey, 255 - blurImage, originalimage, 256);

        cv::cvtColor(originalimage,originalimage,cv::COLOR_BGR2RGB);

      //  outPutImage = dodge(imageGrey,blurImage);
    }