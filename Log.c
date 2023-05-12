#include <stdio.h> 
#include <wchar.h>  // wide char handling
#include "Log.h"	// generated by javah


void printf_utf16(char*  str_ptr, int len){
	// printf("%d",len);
	printf("[Logger] ");
	for(int i =0; i<len*2;i+=2){
		printf("%c", str_ptr[i]);
	}
	printf("\n");
}


JNIEXPORT void JNICALL Java_Log_Log(JNIEnv *env, jobject obj, jstring str){
	jboolean iscopy; 
	char *str_ptr = (*env)->GetStringCritical(env, str, &iscopy); 
	jsize len = (*env)->GetStringUTFLength(env, str);
	printf_utf16(str_ptr, len);
	// wprintf(L"[Log] %s\n", str_ptr);
}
JNIEXPORT void JNICALL Java_Log_LogPassword(JNIEnv *env, jobject obj, jstring str){

	jboolean iscopy; 
	char *str_ptr = (*env)->GetStringCritical(env, str, &iscopy); 
	jsize len = (*env)->GetStringUTFLength(env, str);
	for(int i =0; i< len *2; i++){
		str_ptr[i] = '*';
	}
	
	printf_utf16(str_ptr, len);
	// wprintf(L"[Log] was the buffer a copy? : %ls\n", iscopy ? L"true" : L"false");
	// wprintf(L"[Log] %ls\n", str_ptr);
}