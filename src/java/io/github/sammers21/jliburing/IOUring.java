package io.github.sammers21.jliburing;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;

public class IOUring {

    static {
        System.load("/home/sammers/Jliburing/liblb.so");
    }

    private final Thread evloop;
    private final ConcurrentMap<String, SingleEmitter<ByteBuffer>> completionsMap = new ConcurrentHashMap<>();

    public IOUring() {
        this.evloop = new Thread(this::evloop);
        evloop.start();
    }

    Single<ByteBuffer> read(String fname, int size, int offset) {
        return Single.create(emitter -> {
            completionsMap.put(fname + size + offset, emitter);
            this.read0(fname, size, offset);
        });
    }

    public native ByteBuffer read_ten_bytes(String fname);

    public native ByteBuffer io_uring_read(String fname);

    public native void read0(String fname, int size, int offset);

    public native CQE wait_cqe();

    public static void main(String[] args) {
        System.out.println("Hello world");
        ByteBuffer read_ten_bytes = new IOUring().io_uring_read("Makefile");
        final byte[] bytes = new byte[read_ten_bytes.remaining()];
        read_ten_bytes.get(bytes);
        System.out.println("Hello world\n" + new String(bytes));
    }

    private void evloop() {

    }
}