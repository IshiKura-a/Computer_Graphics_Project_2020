#include "BmpReader.h"

int len;
int width;
int height;
unsigned char* loadBmp(AAsset * pAsset) {
    BITMAPINFOHEADER bitmapInfoHeader;
    BITMAPFILEHEADER bitmapFileHeader;
    unsigned char* bitmapImage;
    int	imageIdx = 0;
    unsigned char tempRGB;

    if (pAsset == nullptr) return nullptr;

    AAsset_read(pAsset, &bitmapFileHeader, sizeof(BITMAPFILEHEADER));
    if (bitmapFileHeader.bfType != BITMAP_ID) {
        printf("Error in LoadBitmapFile: the file is not a bitmap file\n");
        return nullptr;
    }

    AAsset_read(pAsset, &bitmapInfoHeader, sizeof(BITMAPINFOHEADER));

    len = bitmapInfoHeader.biSizeImage;
    bitmapImage = new unsigned char[len];

    AAsset_read(pAsset, bitmapImage, len);

    for (imageIdx = 0;imageIdx < len; imageIdx += 3) {
        tempRGB = bitmapImage[imageIdx];
        bitmapImage[imageIdx] = bitmapImage[imageIdx + 2];
        bitmapImage[imageIdx + 2] = tempRGB;
    }

    width = bitmapInfoHeader.biWidth;
    height = bitmapInfoHeader.biHeight;
    return bitmapImage;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_project_1cg_texture_TextureManager_loadBmpCPP(
        JNIEnv *env,
        jclass clazz,
        jbyteArray bFileName,
        jobject assetManager) {
    int size = env->GetArrayLength (bFileName);
    auto* ucFileName = new unsigned char[size+1];
    env->GetByteArrayRegion (bFileName, 0, env->GetArrayLength (bFileName), reinterpret_cast<jbyte*>(ucFileName));
    ucFileName[size] = '\0';
    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    AAsset * pAsset = AAssetManager_open(mgr, (char*)ucFileName, AASSET_MODE_UNKNOWN);

    unsigned char* bitmap = loadBmp(pAsset);
    jintArray color = env->NewIntArray(len/3);
    jint* ptr = env->GetIntArrayElements(color, 0);
    for(int i=0; i<len/3; i++) {
        ptr[i] = (255 << 24) | ((uint32_t)bitmap[i*3] << 16) | ((uint32_t)bitmap[i*3+1]) << 8 | ((uint32_t)bitmap[i*3+2]);
    }

    return color;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_project_1cg_texture_TextureManager_getWidthCPP(JNIEnv *env, jclass clazz) {
    return width;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_project_1cg_texture_TextureManager_getHeightCPP(JNIEnv *env, jclass clazz) {
    return height;
}