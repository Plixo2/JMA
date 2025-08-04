package org.karina.model.loading.binary;

import lombok.RequiredArgsConstructor;
import org.karina.model.model.ClassModel;
import org.karina.model.typing.types.ReferenceType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BinaryClassLoader {
    private final byte[] buffer;
    private final int fileLength;

    private BinaryClassLoader(byte[] buffer) {
        this.buffer = buffer;
        this.fileLength = buffer.length;
    }

    public static ClassModel loadClass(InputStream inputStream) throws IOException {
        var data = readStream(inputStream, false);
        return loadClass(data);
    }

    public static ClassModel loadClass(byte[] data) {
        var length = data.length;
        var loader = new BinaryClassLoader(data);

//        var magic = loader.load

        return null;
    }

    private ClassModel parse() {
        if (this.fileLength < 4 || readInt(0) != 0xCAFEBABE) {
            // error, Bad magic number
        }
        var minor_version = readU2(4);
        var major_version = readU2(6);
        var constant_pool_count = readU2(8);
        var constant_pool_offset = 10;
        // constant pool
        var access_flags = readU2(constant_pool_offset);
        var this_class = readU2(constant_pool_offset + 2);
        var super_class = readU2(constant_pool_offset + 4);
        var interfaces_count = readU2(constant_pool_offset + 6);
        // interfaces
        var interfaces_offset = constant_pool_offset + 8 + (interfaces_count * 2);
        var fields_count = readU2(interfaces_offset);
        // fields
        var fields_offset = interfaces_offset + 2 + (interfaces_count * 2);
        var methods_count = readU2(fields_offset);
        // methods
        var methods_offset = fields_offset + 2 + (fields_count * 2);

        throw new NullPointerException("");
    }


    private int readU1(int p) {
        return this.buffer[p] & 0xFF;
    }

    private int readU2(int p) {
        int b1 = this.buffer[p] & 0xFF;
        int b2 = this.buffer[p + 1] & 0xFF;
        return (b1 << 8) + b2;
    }

    private int readS1(int p) {
        return this.buffer[p];
    }

    private int readS2(int p) {
        int b1 = this.buffer[p];
        int b2 = this.buffer[p + 1] & 0xFF;
        return (b1 << 8) + b2;
    }

    private int readInt(int p) {
        int ch1 = this.buffer[p] & 0xFF;
        int ch2 = this.buffer[p + 1] & 0xFF;
        int ch3 = this.buffer[p + 2] & 0xFF;
        int ch4 = this.buffer[p + 3] & 0xFF;
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    private long readLong(int p) {
        return ((long) this.buffer[p + 0] << 56) + ((long) (this.buffer[p + 1] & 255) << 48) +
                ((long) (this.buffer[p + 2] & 255) << 40) + ((long) (this.buffer[p + 3] & 255) << 32) +
                ((long) (this.buffer[p + 4] & 255) << 24) + ((this.buffer[p + 5] & 255) << 16) + ((this.buffer[p + 6] & 255) << 8) +
                (this.buffer[p + 7] & 255);
    }

    private float readFloat(int p) {
        return Float.intBitsToFloat(readInt(p));
    }

    private double readDouble(int p) {
        return Double.longBitsToDouble(readLong(p));
    }



    //<editor-fold desc="ASM ClassReader">
    private static byte[] readStream(final InputStream inputStream, final boolean close)
            throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        int bufferSize = computeBufferSize(inputStream);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;
            while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
                outputStream.write(data, 0, bytesRead);
                readCount++;
            }
            outputStream.flush();
            if (readCount == 1) {
                return data;
            }
            return outputStream.toByteArray();
        } finally {
            if (close) {
                inputStream.close();
            }
        }
    }
    private static int computeBufferSize(final InputStream inputStream) throws IOException {
        int expectedLength = inputStream.available();
        /*
         * Some implementations can return 0 while holding available data (e.g. new
         * FileInputStream("/proc/a_file")). Also in some pathological cases a very small number might
         * be returned, and in this case we use a default size.
         */
        if (expectedLength < 256) {
            return INPUT_STREAM_DATA_CHUNK_SIZE;
        }
        return Math.min(expectedLength, MAX_BUFFER_SIZE);
    }
    private static final int MAX_BUFFER_SIZE = 1024 * 1024;
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
    //</editor-fold>

}
