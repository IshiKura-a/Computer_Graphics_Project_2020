#include <jni.h>
#include <cstdio>
#include <iostream>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#ifndef PROJECT_CG_BMPREADER_H
#define PROJECT_CG_BMPREADER_H

#define BITMAP_ID 0x4D42

typedef uint16_t WORD;
typedef uint32_t DWORD;
typedef int32_t LONG;

#pragma pack(push)
#pragma pack(1)
typedef struct tagBITMAPFILEHEADER {
    WORD    bfType;
    DWORD   bfSize;
    WORD    bfReserved1;
    WORD    bfReserved2;
    DWORD   bfOffBits;
} BITMAPFILEHEADER;

typedef struct tagBITMAPINFOHEADER{
    DWORD      biSize;
    LONG       biWidth;
    LONG       biHeight;
    WORD       biPlanes;
    WORD       biBitCount;
    DWORD      biCompression;
    DWORD      biSizeImage;
    LONG       biXPelsPerMeter;
    LONG       biYPelsPerMeter;
    DWORD      biClrUsed;
    DWORD      biClrImportant;
} BITMAPINFOHEADER;
#pragma pack(pop)

unsigned char* loadBmp(AAsset * pAsset);

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_project_1cg_texture_TextureManager_loadBmpCPP(
        JNIEnv *env,
        jclass clazz,
        jbyteArray bFileName,
        jobject assetManager);

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_project_1cg_texture_TextureManager_getWidthCPP(JNIEnv *env, jclass clazz);

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_project_1cg_texture_TextureManager_getHeightCPP(JNIEnv *env, jclass clazz);
#endif //PROJECT_CG_BMPREADER_H
