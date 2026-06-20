//
// Created by Vitalii Andrusyshyn on 10.01.2026.
//

#include <jni.h>
#include <csignal>
#include <unistd.h>
#include <fcntl.h>
#include <cstring>

static int file = -1;

static void writeCrash(int signal) {
    const char* msg;

    switch (signal) {
        case SIGSEGV: msg = "Native crash with SIGSEGV: segmentation fault\n"; break;
        case SIGABRT: msg = "Native crash with SIGABRT: abort\n"; break;
        case SIGFPE:  msg = "Native crash with SIGFPE: arithmetic error\n"; break;
        case SIGILL:  msg = "Native crash with SIGILL: illegal instruction\n"; break;
        case SIGBUS:  msg = "Native crash with SIGBUS: bus error\n"; break;
        default:      msg = "Native crash with unknown signal\n"; break;
    }

    if (file != -1) {
        write(file, msg, strlen(msg));
        fsync(file);
    }

    _exit(128 + signal);
}

JNIEXPORT void JNICALL
Java_com_cws_print_NativeExceptionHandler_install(
        JNIEnv* env,
        jobject thiz,
        jstring filepath
) {
    const char* cpath = (*env)->GetStringUTFChars(env, filepath, NULL);
    file = open(cpath, O_WRONLY | O_CREAT | O_APPEND, 0666);
    (*env)->ReleaseStringUTFChars(env, filepath, cpath);

    if (file == -1) return;

    signal(SIGSEGV, writeCrash);
    signal(SIGABRT, writeCrash);
    signal(SIGFPE,  writeCrash);
    signal(SIGILL,  writeCrash);
    signal(SIGBUS,  writeCrash);
}