#include <stdlib.h>
#include <forwardsolver_exports.h>
#include <VES.H>

jdoubleArray JNICALL Java_ru_nucodelabs_gem_ForwardSolver_solve(
    JNIEnv *env,
    jobject obj,
    jdoubleArray resistance,
    jdoubleArray power,
    jint layersCnt,
    jdoubleArray AB_2,
    jint distCnt) {

    double* res = env->GetDoubleArrayElements(resistance, 0);
    double* pow = env->GetDoubleArrayElements(power, 0);
    int lCnt = (int) layersCnt;
    double* raz = env->GetDoubleArrayElements(AB_2, 0);
    int razCnt = (int) distCnt;

    double* resApp = (double*)malloc(sizeof(double) * razCnt);
    Ves(
        res,
        pow,
        lCnt,
        raz,
        razCnt,
        resApp);
    
    jdoubleArray toJavaArr = env->NewDoubleArray(razCnt);
    env->SetDoubleArrayRegion(toJavaArr, 0, razCnt, resApp);
    return toJavaArr;
}