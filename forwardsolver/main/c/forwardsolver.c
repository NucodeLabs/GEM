#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>

JNIEXPORT jdoubleArray JNICALL
Java_ru_nucodelabs_gem_ForwardSolver_solve(JNIEnv *env, jobject obj,
jdoubleArray resistance,
jdoubleArray power,
jint layersCnt,
jdoubleArray AB_2,
jint distCnt)
{
    void* hDll = dlopen("libveser.dylib", RTLD_LAZY);
    typedef void (*FuncType)(double*, double*, int, double*, int, double);
    FuncType func = (FuncType) dlsym(hDll, "Ves");
    double* Roker = (double*)malloc(sizeof(double * distCnt));
    func(resistance, power, layersCnt, AB_2, distCnt, Roker);
    return (jdoubleArray) Roker;
}
