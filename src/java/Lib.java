import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class Lib {

    static {
        System.load("/home/sammers/Jliburing/liblb.so");
    }
    
    public native ByteBuffer read_ten_bytes(String fname);
    public native ByteBuffer io_uring_read(String fname);

    public native CompletableFuture<ByteBuffer> io_uring_read_efd(String fname);
    
    public static void main(String[] args) {
        System.out.println("Hello world");
        ByteBuffer read_ten_bytes = new Lib().io_uring_read("Makefile");
        final byte[] bytes = new byte[read_ten_bytes.remaining()];
        read_ten_bytes.get(bytes);
        System.out.println("Hello world\n" + new String(bytes));
    }
}