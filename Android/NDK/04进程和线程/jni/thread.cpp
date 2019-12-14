#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>
#include <jni.h>
#include <android/log.h>

bool g_isRUn = true;
int g_nNumber = 0;

# define MAXNUM 100000

pthread_mutex_t mutext = PTHREAD_MUTEX_INITIALIZER;
sem_t sem;

void* threadProc(void* lpParam){
    for (int i = 0; i < MAXNUM; i++)
    {
        sem_wait(&sem);
        g_nNumber++;
        sem_post(&sem);
    }
    
    return (void*)8;
}

int main(int argc, char* argv[]){
    pthread_t tid;
    pthread_attr_t attr;
    int result;

    sem_init(&sem, 0, 1);
    pthread_attr_init(&attr);
    result = pthread_create(&tid, &attr, threadProc, NULL);
    
    for (int i = 0; i < MAXNUM; i++)
    {
        pthread_mutex_lock(&mutext);
        g_nNumber++;
        pthread_mutex_unlock(&mutext);
    }
    pid_t tid = getpid();
    void* value;
    pthread_join(tid, &value);
    printf("tid: %d, g_nNumber: %d\r\n", tid, g_nNumber);
    
    return 0;
}