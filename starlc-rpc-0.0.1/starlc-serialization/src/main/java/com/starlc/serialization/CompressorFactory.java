package com.starlc.serialization;

import com.starlc.serialization.impl.SnappyCompressor;

public class CompressorFactory {
    public static Compressor get(byte extraInfo) {
        switch (extraInfo & 24) {
            case 0x0:
                return new SnappyCompressor();
            default:
                return new SnappyCompressor();
        }
    }
}
