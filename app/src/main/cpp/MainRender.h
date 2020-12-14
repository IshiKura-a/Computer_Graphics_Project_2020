#include <jni.h>

#ifndef PROJECT_CG_MAINRENDER_H
#define PROJECT_CG_MAINRENDER_H

void onSurfaceCreatedCPP();

void onSurfaceChangedCPP(int width, int height);

void onDrawFrameCPP();

extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onSurfaceCreatedCPP(
        JNIEnv *env,
        jclass clazz);
extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onSurfaceChangedCPP(
        JNIEnv *env,
        jclass clazz,
        jint width,
        jint height);
extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onDrawFrameCPP(
        JNIEnv *env,
        jclass clazz);
#endif //PROJECT_CG_MAINRENDER_H