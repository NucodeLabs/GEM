#include <stdlib.h>
#include <forwardsolverJNI_exports.h>
#include <VES.H>

JNIEXPORT jdoubleArray JNICALL Java_ru_nucodelabs_algorithms_forward_1solver_SonetForwardSolver_ves(
    JNIEnv *env,
    jobject obj,
    jdoubleArray resistance,
    jdoubleArray power,
    jint layersCnt,
    jdoubleArray AB_2,
    jint distCnt) {

    double* res = (*env)->GetDoubleArrayElements(env, resistance, 0);
    double* pow = (*env)->GetDoubleArrayElements(env, power, 0);
    int lCnt = (int) layersCnt;
    double* raz = (*env)->GetDoubleArrayElements(env, AB_2, 0);
    int razCnt = (int) distCnt;

    double* resApp = (double*)malloc(sizeof(double) * razCnt);
    Ves(
        res,
        pow,
        lCnt,
        raz,
        razCnt,
        resApp);
    
    jdoubleArray toJavaArr = (*env)->NewDoubleArray(env, razCnt);
    (*env)->SetDoubleArrayRegion(env, toJavaArr, 0, razCnt, resApp);
    return toJavaArr;
}