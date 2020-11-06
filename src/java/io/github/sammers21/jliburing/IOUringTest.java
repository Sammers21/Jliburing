package io.github.sammers21.jliburing;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

public class IOUringTest {

    @Test
    public void fileReadWorks() throws IOException, InterruptedException {
        ByteBuffer read_ten_bytes = new IOUring().io_uring_read("Makefile");
        final byte[] bytes = new byte[read_ten_bytes.remaining()];
        read_ten_bytes.get(bytes);
        String act = new String(bytes);
        var expected = new String(Files.readAllBytes(Path.of("Makefile")));
        Assert.assertEquals(expected, act);
    }
}
