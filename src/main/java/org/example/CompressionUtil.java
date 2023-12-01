package org.example;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionUtil {
    public static byte[] compress(byte[] input) throws Exception {

        // Create a Deflater object to compress data
        Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION,true);

        // Set the input for the compressor
        compressor.setInput(input);

        // Call the finish() method to indicate that we have
        // no more input for the compressor object
        compressor.finish();

        // Compress the data
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] readBuffer = new byte[1024];
        int readCount = 0;

        while (!compressor.finished()) {
            readCount = compressor.deflate(readBuffer);
            if (readCount > 0) {
                // Write compressed data to the output stream
                bao.write(readBuffer, 0, readCount);
            }
        }

        // End the compressor
        compressor.end();

        // Return the written bytes from output stream
        return bao.toByteArray();
    }

    public static byte[] decompress(byte[] input)
            throws Exception {
        // Create an Inflater object to compress the data
        Inflater decompressor = new Inflater(true);

        // Set the input for the decompressor
        decompressor.setInput(input);

        // Decompress data
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] readBuffer = new byte[1024];
        int readCount = 0;

        while (!decompressor.finished()) {
            readCount = decompressor.inflate(readBuffer);
            if (readCount > 0) {
                // Write the data to the output stream
                bao.write(readBuffer, 0, readCount);
            }
        }

        // End the decompressor
        decompressor.end();

        // Return the written bytes from the output stream
        return bao.toByteArray();
    }


}
