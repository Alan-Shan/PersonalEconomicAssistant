#include <jni.h>

JNIEXPORT jstring JNICALL
Java_top_ilum_pea_ui_home_ApiHelper_getNYTKey(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "dVdrMzh1WXE1cVVFRDl6T1pkVHN5UGhMdnBjQUJBZUc");
}