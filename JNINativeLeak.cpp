#include "JNINativeLeak.h"
#include <stdlib.h>
#include <memory.h>


JNIEXPORT void JNICALL Java_JNINativeLeak_allocateMemory
  (JNIEnv *env, jobject obj, jint size) {

  	char* bytes = (char*) malloc(size);
//        printf("Allocated %d bytes at %p \n", size, (void*)bytes);

        for (int i=0; i<40; i++) {
          strcpy(bytes+i*25, "Alert: JNI Memory Leak  ");
        }

  }
