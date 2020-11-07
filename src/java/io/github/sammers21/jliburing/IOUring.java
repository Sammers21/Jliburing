package io.github.sammers21.jliburing;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;

public class IOUring implements Closeable {

    static {
        System.load("/home/sammers/Jliburing/liblb.so");
    }

    private final Long ringAddress;
    private final int queueDepth;
    private final long flags;
    private final Thread evloop;
    private final ConcurrentMap<String, SingleEmitter<ByteBuffer>> completionsMap;

    public IOUring(int queueDepth, int flags) {
        this.ringAddress = ring_init(queueDepth, flags);
        this.queueDepth = queueDepth;
        this.flags = flags;
        this.evloop = new Thread(this::evloop);
        this.completionsMap = new ConcurrentHashMap<>();
    }

    public IOUring() {
        this(100, 0);
    }


    public native ByteBuffer io_uring_read(String fname);
    public native void read0(String fname, int size, int offset);
    private native CQE wait_cqe();
    private native long ring_init(int queueDepth, int flags);
    private native void close0();

    public Single<ByteBuffer> read(String fname, int size, int offset) {
        return Single.create(emitter -> {
            completionsMap.put(fname + size + offset, emitter);
            this.read0(fname, size, offset);
        });
    }

    private void evloop() {

    }

    @Override
    public void close() throws IOException {

    }
}