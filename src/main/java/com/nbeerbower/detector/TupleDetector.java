package com.nbeerbower.detector;

import java.io.InputStream;

public interface TupleDetector {
    /**
     * Compares similarity of two input streams and outputs result as a percentage
     *
     * @param in1
     * @param in2
     * @param tupleSize
     * @return similarity of in1 and in2 as a percentage using N-tuples where N = tupleSize
     */
    double similarityAsPercentage(InputStream in1, InputStream in2, int tupleSize);
}
