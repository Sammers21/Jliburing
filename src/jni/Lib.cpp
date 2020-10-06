#include "Lib.h"
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/uio.h>
#include <linux/fs.h>
#include <sys/ioctl.h>
#include <liburing.h>

off_t get_file_size(int fd)
{
    struct stat st;

    if (fstat(fd, &st) < 0)
    {
        perror("fstat");
        return -1;
    }
    if (S_ISBLK(st.st_mode))
    {
        unsigned long long bytes;
        if (ioctl(fd, BLKGETSIZE64, &bytes) != 0)
        {
            perror("ioctl");
            return -1;
        }
        return bytes;
    }
    else if (S_ISREG(st.st_mode))
        return st.st_size;

    return -1;
}

JNIEXPORT jobject JNICALL Java_Lib_read_1ten_1bytes(JNIEnv *env, jobject obj, jstring fname)
{
    const char *cfname = env->GetStringUTFChars(fname, 0);
    int fd = open(cfname, O_RDONLY);
    int fsize = get_file_size(fd);
    void *buf = (void *)malloc(fsize);
    int ret = read(fd, buf, fsize);
    if (ret == -1)
    {
        perror("read");
        printf("bad read");
    }
    ret = close(fd);
    if (ret == -1)
    {
        perror("close");
        printf("bad close");
    }
    printf("just-read %d bytes\n", fsize);
    // printf("WELL %c", *(buf + 1));
    return env->NewDirectByteBuffer(buf, fsize);
}

JNIEXPORT jobject JNICALL Java_Lib_io_1uring_1read(JNIEnv *env, jobject zhis, jstring fname)
{

    const char *cfname = env->GetStringUTFChars(fname, 0);
    struct io_uring rring;
    int res = io_uring_queue_init(100, &rring, 0);
    if (res != 0)
    {
        printf("io_uring_queue_init");
        exit(res);
    }
    
    struct io_uring* ring = &rring;
    int fd = open(cfname, O_RDONLY);
    int fsize = get_file_size(fd);
    void *buf = (void *)malloc(fsize);
    char greeting[] = "HELLO FROM PAVEL";
    io_uring_sqe* sqe = io_uring_get_sqe(ring);
    io_uring_prep_read(sqe, fd, buf, fsize, 0);
    io_uring_sqe_set_data(sqe, &greeting);
    io_uring_submit(ring);
    struct io_uring_cqe *cqe;
    int ret = io_uring_wait_cqe(ring, &cqe);
    if (ret < 0) {
        printf("io_uring_wait_cqe");
        exit(res);
    }
    if (cqe->res < 0) {
           printf("Async wait failed");
        exit(res);
    }
    char *gragain = (char*)io_uring_cqe_get_data(cqe);
    fprintf( stderr, "GRAGAIN %s", gragain );
    return env->NewDirectByteBuffer(buf, fsize);
}


/*
 * Class:     Lib
 * Method:    io_uring_read_efd
 * Signature: (Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;
 */
JNIEXPORT jobject JNICALL Java_Lib_io_1uring_1read_1efd
  (JNIEnv *env, jobject zthis, jstring fname){
  
}