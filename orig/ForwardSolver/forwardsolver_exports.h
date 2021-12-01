#ifndef FORWARDSOLVER_H__
#define FORWARDSOLVER_H__
#include <jni.h>

JNIEXPORT jdoubleArray JNICALL Java_ru_nucodelabs_gem_ForwardSolver_solve(
    JNIEnv *env,
    jobject obj,
    jdoubleArray resistance,
    jdoubleArray power,
    jint layersCnt,
    jdoubleArray AB_2,
    jint distCnt);

#endif // FORWARDSOLVER_H__