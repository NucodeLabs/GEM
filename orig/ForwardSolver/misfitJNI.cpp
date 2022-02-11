#include <MisfitFunctions.h>
#include <misfitJNI_exports.h>

JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateRelativeDeviation(
    JNIEnv *env,
    jobject obj,
    jdouble resistanceExperimental, 
    jdouble resistanceTheoretical
) 
{
    return (jdouble)CalcRelativeDeviation(
        (double)resistanceExperimental,
        (double)resistanceTheoretical    
    );
}

JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateRelativeDeviationWithError(
    JNIEnv *env,
    jobject obj,
    jdouble resistanceExperimental, 
    jdouble resistanceExperimentalError,
    jdouble resistanceTheoretical
)
{
    return (jdouble)CalcRelativeDeviationWithError(
        (double)resistanceExperimental,
        (double)resistanceExperimentalError,
        (double)resistanceTheoretical
    );
}

JNIEXPORT jdouble JNICALL Java_ru_nucodelabs_algorithms_MisfitFunctions_calculateResistanceApparent(
    JNIEnv *env,
    jobject obj,
    jdouble AB_2,
    jdouble MN_2,
    jdouble voltage,
    jdouble amperage
)
{
    return (jdouble)CalcRok(
        (double)AB_2,
        (double)MN_2,
        (double)voltage,
        (double)amperage
    );
}