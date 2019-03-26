package com.nbeerbower;

import com.nbeerbower.detector.SynonymTupleDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;

public class PlagiarismDetector {

    public static void main(String[] args) throws IOException {
        // Print instructions if not enough arguments are provided
	    if (args.length < 3) {
	        printHelp();
	        return;
        }

	    // Initialize tuple detector with provided synonym file
        SynonymTupleDetector detector = new SynonymTupleDetector(args[0]);
	    // Open input streams for comparison files
        FileInputStream file1 = new FileInputStream(new File(args[1]));
        FileInputStream file2 = new FileInputStream(new File(args[2]));

        // Use provided tuple size or default to 3
	    int tupleSize = args.length >= 4 ? Integer.parseInt(args[3]) : 3;
        // Get similarity between the two files
	    double similarity = detector.similarityAsPercentage(file1, file2, tupleSize);

	    // Print result as formatted % string
	    System.out.println(NumberFormat.getPercentInstance().format(similarity));
    }

    private static void printHelp() {
        System.out.println("PlagiarismDetector SYNONYMS_FILE FILE1 FILE2 [TUPLE_SIZE]");
    }
}
