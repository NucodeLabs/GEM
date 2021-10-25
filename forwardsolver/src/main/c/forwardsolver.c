#include <stdlib.h>

#include <jni.h>

#if defined(__APPLE__)
    #include <dlfcn.h>
    #define LIBVES_NAME "libves.dylib"
#elif defined(__linux__)
    #include <dlfcn.h>
    #define LIBVES_NAME "libves.so"
#elif defined(_MSC_VER)
    #include <windows.h>
    #define LIBVES_NAME "forwardsolver\\src\\main\\lib\\ves.dll"
#else 
    #define LIBVES_NAME "unsupported_os_NO_DL_FOUND"
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

    typedef void (*vesType)(
        double*,
        double*,
        int,
        double*,
        int,
        double*
    );

#if defined(_MSC_VER)
    HMODULE dlHandle = LoadLibrary(LIBVES_NAME); 
 
    if (dlHandle == NULL) {
        fprintf(stderr, "DLL NOT OPENED!!!\nERRCODE = %ld\n", GetLastError());
        exit(0);
    } 

    vesType Ves = (vesType)GetProcAddress(dlHandle, "Ves");
    if (Ves == NULL) {
        fprintf(stderr, "FUNCTION NOT LOADED!!!\nERRCODE = %ld\n", GetLastError());
        exit(0);
    } 
#else 
    void* dlHandle = dlopen(LIBVES_NAME, RTLD_NOW);
    
    if (dlHandle == NULL) {
        fprintf(stderr, "DLL NOT OPENED !!!\n%s\n", dlerror());
        exit(0);
    }

    vesType Ves = (vesType)dlsym(dlHandle, "Ves");
    if (Ves == NULL) {
        fprintf(stderr, "FUNCTION NOT LOADED !!!\n%s\n", dlerror());
        exit(0);
    }
#endif

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
