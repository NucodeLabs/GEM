#include <dlfcn.h>
#include <stdlib.h>

#include <jni.h>

#if defined(__APPLE__)
  #define LIBVES_NAME "libves.dylib"
#elif defined(__linux__)
  #define LIBVES_NAME "libves.so"
#endif

JNIEXPORT jdoubleArray JNICALL Java_ru_nucodelabs_gem_ForwardSolver_solve(
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

    void* dlHandle = dlopen(LIBVES_NAME, RTLD_NOW);
    if (dlHandle == NULL) {
        printf("DLL NOT OPENED !!!\n%s\n", dlerror());
        exit(0);
    }

    typedef void (*vesType)(
        double*,
        double*,
        int,
        double*,
        int,
        double*
    );

    vesType Ves = (vesType)dlsym(dlHandle, "Ves");
    if (Ves == NULL) {
        printf("FUNCTION NOT LOADED !!!\n");
        exit(0);
    }

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
