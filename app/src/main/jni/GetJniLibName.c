#include <jni.h>
#include <string.h>
#include <android/log.h>
#define LOG_TAG "System.out"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#include "nwpu_aslp_wakeup_NdkJniUtils.h"
#include "./utils/android_log_print.h"
#include "./fbank/getfbank.h"
#include "./dnn/getdnn.h"

jfloat w0[jINPUT_DIMEN][jLAYER_DIMEN]; jfloat b0[jLAYER_DIMEN];
jfloat w1[jLAYER_DIMEN][jLAYER_DIMEN]; jfloat b1[jLAYER_DIMEN];
jfloat w2[jLAYER_DIMEN][jLAYER_DIMEN]; jfloat b2[jLAYER_DIMEN];
jfloat w3[jLAYER_DIMEN][jOUTPUT_DIMEN]; jfloat b3[jOUTPUT_DIMEN];

JNIEXPORT jboolean JNICALL
Java_nwpu_aslp_wakeup_NdkJniUtils_initDnn(JNIEnv *env, jobject instance,
                                                              jobjectArray jw0, jobjectArray jw1,
                                                              jobjectArray jw2, jobjectArray jw3,
                                                              jfloatArray jb0, jfloatArray jb1,
                                                              jfloatArray jb2, jfloatArray jb3) {
    //("Init b0 b1 b2 b3")
    (*env)->GetFloatArrayRegion(env, jb0, 0, jLAYER_DIMEN, b0);
    (*env)->GetFloatArrayRegion(env, jb1, 0, jLAYER_DIMEN, b1);
    (*env)->GetFloatArrayRegion(env, jb2, 0, jLAYER_DIMEN, b2);
    (*env)->GetFloatArrayRegion(env, jb3, 0, jOUTPUT_DIMEN, b3);

    //("Init w0 w1 w2 w3")
    jint i,j;
    jarray myarray;
    for (i = 0; i < jINPUT_DIMEN; i++) {
        myarray = ((*env)->GetObjectArrayElement(env, jw0,i));
        jfloat *coldata = (*env)->GetFloatArrayElements(env, (jfloatArray)myarray, 0);
        for (j=0; j<jLAYER_DIMEN; j++) {
            w0[i][j] = coldata[j];
        }
        (*env)->DeleteLocalRef(env, myarray);
    }
    for (i = 0; i < jLAYER_DIMEN; i++) {
        myarray = ((*env)->GetObjectArrayElement(env, jw1,i));
        jfloat *coldata = (*env)->GetFloatArrayElements(env, (jfloatArray)myarray, 0);
        for (j=0; j<jLAYER_DIMEN; j++) {
            w1[i][j] = coldata[j];
        }
        (*env)->DeleteLocalRef(env, myarray);
    }
    for (i = 0; i < jLAYER_DIMEN; i++) {
        myarray = ((*env)->GetObjectArrayElement(env, jw2,i));
        jfloat *coldata = (*env)->GetFloatArrayElements(env, (jfloatArray)myarray, 0);
        for (j=0; j<jLAYER_DIMEN; j++) {
            w2[i][j] = coldata[j];
        }
        (*env)->DeleteLocalRef(env, myarray);
    }
    for (i = 0; i < jLAYER_DIMEN; i++) {
        myarray = ((*env)->GetObjectArrayElement(env, jw3,i));
        jfloat *coldata = (*env)->GetFloatArrayElements(env, (jfloatArray)myarray, 0);
        for (j=0; j<jOUTPUT_DIMEN; j++) {
            w3[i][j] = coldata[j];
        }
        (*env)->DeleteLocalRef(env, myarray);
    }
}

JNIEXPORT jfloatArray JNICALL Java_nwpu_aslp_wakeup_NdkJniUtils_getdnn(JNIEnv *env, jobject obj, jfloatArray jInMat){

    jfloat InMat[jINPUT_DIMEN];
    jfloat OutMat[jOUTPUT_DIMEN];
    (*env)->GetFloatArrayRegion(env,jInMat,0,jINPUT_DIMEN,InMat);
    getdnn(w0,b0,w1,b1,w2,b2,w3,b3,InMat,OutMat);
    jfloatArray result = (*env)->NewFloatArray(env,jOUTPUT_DIMEN);
    (*env)->SetFloatArrayRegion(env,result,0,jOUTPUT_DIMEN,OutMat);
    return result;
}

JNIEXPORT jfloatArray JNICALL Java_nwpu_aslp_wakeup_NdkJniUtils_pcm2fbank(JNIEnv *env, jobject obj, jshortArray buffer, jint length){
    short array[length];
    int nSample = length;
    int frSize = 400;
    int frRate = 160;
    int row = (nSample - frSize) / frRate+1;
    int fbank_len =  row*NUMCHANS;
    float fbank[fbank_len];
    (*env)->GetShortArrayRegion(env,buffer,0,length,array);
    getfbank(array,length,fbank,fbank_len);

    jfloatArray result = (*env)->NewFloatArray(env,fbank_len);
    (*env)->SetFloatArrayRegion(env,result,0,fbank_len,fbank);
    return result;
}

