#include "MainRender.h"
#include <GLES2/gl2.h>

void onSurfaceCreatedCPP() {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
}

void onSurfaceChangedCPP(int width, int height) {
    glViewport(0, 0, width, height);
}

void onDrawFrameCPP() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearColor(0,0,0,1);

    glEnable(GL_DEPTH_TEST);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onSurfaceCreatedCPP(
        JNIEnv* env,
        jclass clazz) {
    onSurfaceCreatedCPP();
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onSurfaceChangedCPP(
        JNIEnv* env,
        jclass clazz,
        jint width,
        jint height) {
    onSurfaceChangedCPP(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_project_1cg_MainRender_onDrawFrameCPP(
        JNIEnv* env,
        jclass clazz) {
    onDrawFrameCPP();
}