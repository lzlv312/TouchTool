#ifndef NATIVE_LIB
#define NATIVE_LIB

#include <jni.h>
#include <string>
#include <algorithm>

#include "native-lib.h"

using namespace std;
using namespace cv;


static jobject createMatchResult(JNIEnv *env, jdouble value, jint x, jint y, jint width, jint height) {
    auto resultClass = (jclass) env->FindClass("top/bogey/touch_tool/utils/MatchResult");
    jmethodID mid = env->GetMethodID(resultClass, "<init>", "(DIIII)V");
    jobject result = env->NewObject(resultClass, mid, value, x, y, width, height);
    return result;
}

static int clamp(int up, int low, int value) {
    return max(low, min(up, value));
}

extern "C" JNIEXPORT jobject JNICALL
Java_top_bogey_touch_1tool_utils_DisplayUtil_nativeMatchTemplate(JNIEnv *env, jclass clazz, jobject bitmap, jobject temp, jint similarity, jboolean fast, jint speed) {
    int scale = speed;

    Mat src = bitmap_to_cv_mat(env, bitmap);
    Mat tmp = bitmap_to_cv_mat(env, temp);
    if (src.empty() || tmp.empty())
        return createMatchResult(env, 0, 0, 0, 0, 0);

    if (scale != 1) {
        resize(src, src, Size(src.cols / scale, src.rows / scale));
        resize(tmp, tmp, Size(tmp.cols / scale, tmp.rows / scale));
    }

    if (fast) {
        cvtColor(src, src, COLOR_BGR2GRAY);
        adaptiveThreshold(src, src, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 2);
        cvtColor(tmp, tmp, COLOR_BGR2GRAY);
        adaptiveThreshold(tmp, tmp, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 2);
    }

    int resultCol = src.cols - tmp.cols + 1;
    int resultRow = src.rows - tmp.rows + 1;

    Mat result;
    result.create(resultCol, resultRow, CV_32FC1);

    matchTemplate(src, tmp, result, TM_CCOEFF_NORMED);

    jclass listCls = env->FindClass("java/util/ArrayList");
    jmethodID listInit = env->GetMethodID(listCls, "<init>", "()V");
    jobject listObj = env->NewObject(listCls, listInit);
    jmethodID listAdd = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");


    int similar = clamp(100, 1, similarity);
    vector<Rect> areas;
    for (int i = 0; i < result.rows; ++i) {
        for (int j = 0; j < result.cols; ++j) {
            bool flag = false;
            int x = j;
            int y = i;
            int width = tmp.cols;
            int height = tmp.rows;

            for (const auto &area: areas) {
                if (area.x < width + x && x < area.x + area.width && area.y < height + y && y < area.y + area.height) {
                    flag = true;
                    break;
                }
            }
            if (flag)
                continue;

            float currValue = result.at<float>(i, j) * 100;
            if (currValue >= similar) {
                env->CallBooleanMethod(listObj, listAdd, createMatchResult(env, currValue, x * scale, y * scale, width * scale, height * scale));
                areas.emplace_back(x, y, width, height);
            }
        }
    }

    src.release();
    tmp.release();
    result.release();
    return listObj;
}

extern "C" JNIEXPORT jobject JNICALL
Java_top_bogey_touch_1tool_utils_DisplayUtil_nativeMatchColor(JNIEnv *env, jclass clazz, jobject bitmap, jintArray rgb, jint similarity) {
    Mat src = bitmap_to_cv_mat(env, bitmap);
    if (src.empty())
        return nullptr;

    cvtColor(src, src, COLOR_BGR2HSV);
    GaussianBlur(src, src, Size(5, 5), 0);
    erode(src, src, 3);

    jint *rgbColor = env->GetIntArrayElements(rgb, JNI_FALSE);
    Mat bgr = Mat(1, 1, CV_8UC3, Vec3b(rgbColor[2], rgbColor[1], rgbColor[0]));
    Mat hsv;
    cvtColor(bgr, hsv, COLOR_BGR2HSV);
    Vec3b hsvColor = hsv.at<Vec3b>(0, 0);
    int similar = clamp(100, 1, similarity);
    double diff = 1 - (similar / 100.0);

    Vec3d lowHsv(max(.0, hsvColor[0] - 180 * diff), max(.0, hsvColor[1] - 255 * diff), max(.0, hsvColor[2] - 255 * diff));

    Vec3d highHsv(min(180.0, hsvColor[0] + 180 * diff), min(255.0, hsvColor[1] + 255 * diff), min(255.0, hsvColor[2] + 255 * diff));

    Mat mask;
    inRange(src, lowHsv, highHsv, mask);
    vector<vector<Point> > contours;
    findContours(mask, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

    jclass listCls = env->FindClass("java/util/ArrayList");
    jmethodID listInit = env->GetMethodID(listCls, "<init>", "()V");
    jobject listObj = env->NewObject(listCls, listInit);
    jmethodID listAdd = env->GetMethodID(listCls, "add", "(Ljava/lang/Object;)Z");

    for (auto &contour: contours) {
        double area = contourArea(contour);
        if (area > 81) {
            Rect r = boundingRect(contour);
            env->CallBooleanMethod(listObj, listAdd, createMatchResult(env, area, r.x, r.y, r.width, r.height));
        }
    }

    src.release();
    return listObj;
}

#endif