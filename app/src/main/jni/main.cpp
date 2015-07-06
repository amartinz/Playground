#include <android/log.h>

#include <sys/types.h>
#include <sys/stat.h>

#include <fcntl.h>
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define APPNAME "Playground"
#define BUFFER_SIZE (512 * 1024)

#define DEBUG 1

#if DEBUG
#define ALOGV(x) __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, x);
#define _ALOGV(x,y) __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, x, y);
#else
#define ALOGV(x)
#define _ALOGV(x,y)
#endif

// always use the NativeWrapper: Java_alex_playground_utils_NativeWrapper

extern "C" {
    JNIEXPORT jstring JNICALL
    Java_alex_playground_utils_NativeWrapper_stringFromJNI(JNIEnv *env, jobject thiz)
    {
        // defining the string we send to the java code as parameter
        jstring jstr = env->NewStringUTF("This comes from jni.");

        // finding the class where our method lives
        jclass clazz = env->FindClass("alex/playground/utils/NativeWrapper");

        // getting the method id, it takes a string as parameter "(Ljava....)" and returns a string "(...)Ljava..."
        jmethodID messageMe = env->GetMethodID(clazz, "stringFromJava", "(Ljava/lang/String;)Ljava/lang/String;");

        // call the method and as it returns a string, store the returned string in a jstring called "result"
        jstring result = (jstring) env->CallObjectMethod(thiz, messageMe, jstr);

        // getting the string so we can use it in C
        const char* stringFromJava = env->GetStringUTFChars(result, NULL);

        // LOG IT!
        _ALOGV("%s\n", stringFromJava);

        // returning a new string
        return env->NewStringUTF("Hello from C++ JNI!");
    }

    JNIEXPORT jstring JNICALL
    Java_alex_playground_utils_NativeWrapper_readMemoryInfo(JNIEnv *env, jobject thiz, jstring file)
    {
        const char *file_path = env->GetStringUTFChars(file, 0);
        _ALOGV("file_path -> %s", file_path);

        int fd;
        int err = 0;
        char *string;

        fd = open(file_path, O_RDONLY);
        if (fd < 0) {
            ALOGV("Error opening file");
            close(fd);
            goto err;
        }

        string = (char*) malloc(BUFFER_SIZE);
        if (string == NULL) {
            ALOGV("Error: out of memory");
            goto err;
        }

        if (read(fd, string, BUFFER_SIZE) < 0) {
            ALOGV("Error reading the file");
            free(string);
            goto err;
        } else {
            string[BUFFER_SIZE] = '\0';
        }

        jstring result;
        if (string) {
            _ALOGV("string is: %s", string);
            result = env->NewStringUTF(string);
            //result = env->NewStringUTF("Successfully read file!");
        } else {
            result = env->NewStringUTF("Could not read file!");
        }
        free(string);
        close(fd);

        return result;

err:
        return env->NewStringUTF("Could not read file!");
    }
}
