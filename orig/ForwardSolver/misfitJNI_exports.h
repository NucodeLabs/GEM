#pragma once
#include <jni.h>

extern "C" JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateRelativeDeviation(
    JNIEnv *env,
    jobject obj,
    jdouble resistanceExperimental, 
    jdouble resistanceTheoretical
);

extern "C" JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateRelativeDeviationWithError(
    JNIEnv *env,
    jobject obj,
    jdouble resistanceExperimental, 
    jdouble resistanceExperimentalError,
    jdouble resistanceTheoretical
);

extern "C" JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateResistanceApparent(
    JNIEnv *env,
    jobject obj,
    jdouble AB_2,
    jdouble MN_2,
    jdouble voltage,
    jdouble amperage
);