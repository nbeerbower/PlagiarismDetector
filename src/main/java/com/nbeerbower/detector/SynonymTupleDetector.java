package com.nbeerbower.detector;

import java.io.*;
import java.util.*;

/**
 * Implementation of TupleDetector that can handle synonyms of words
 */
public class SynonymTupleDetector implements TupleDetector {

    // Maps words to a set of synonyms
    private Map<String, Set<String>> thesaurus;

    /**
     * @param synonymsFile path to file containing lists of synonyms
     */
    public SynonymTupleDetector(String synonymsFile) throws IOException {
        // Initialize thesaurus map
        thesaurus = new HashMap<>();

        // Read groups of synonyms line by line and add them to the thesaurus
        BufferedReader reader = new BufferedReader(new FileReader(synonymsFile));
        String line;
        while ((line = reader.readLine()) != null) {
            addSynonymGroup(line.split("\\s+"));
        }
    }

    /**
     * @param in1 text stream
     * @param in2 text stream
     * @param tupleSize size of tuples to use
     * @return similarity of in1 and in2 as a percentage using N-tuples where N = tupleSize
     */
    public double similarityAsPercentage(InputStream in1, InputStream in2, int tupleSize) {
        if (tupleSize < 1) throw new IllegalArgumentException("Tuple size must be at least 1");

        // Read only words (ignoring punctuation and whitespace) from input streams
        List<String> words1 = readWords(in1);
        List<String> words2 = readWords(in2);

        // Return 0 if tupleSize is greater than either input
        if (tupleSize > words1.size() || tupleSize > words2.size()) {
            return 0;
        }

        int detectedTuples = 0;
        int totalTuples = 0;

        // Build tuple tree map for fast lookup
        HashMap<Set<String>, Node<Set<String>>> tupleMap = new HashMap<>();
        // Iterate over all tuples in the second file
        for (int i = 0; i <= (words2.size() - tupleSize); i++) {
            // Use consistent case for each word
            String word = words2.get(i).toLowerCase();
            // Get tuple tree for the first word in this tuple
            Node<Set<String>> currentNode = tupleMap.computeIfAbsent(lookupSynonyms(word), Node::new);
            // Add following words in tuple as children
            for (int j = 1; j < tupleSize; j++) {
                word = words2.get(i+j).toLowerCase();
                currentNode = currentNode.addNeighbor(new Node<>(lookupSynonyms(word)));
            }
            totalTuples++;
        }

        // Iterate over all tuples in the first file
        for (int i = 0; i <= (words1.size() - tupleSize); i++) {
            // Use consistent case for each word
            String word = words1.get(i).toLowerCase();
            // Attempt to find matching tuple in generated tuple map
            Node<Set<String>> currentNode = tupleMap.get(lookupSynonyms(word));
            // Traverse the tree until tuple is found or we reach a leaf
            for (int j = 0; currentNode != null && j < tupleSize-1; j++) {
                String nextWord = words1.get(i+j+1).toLowerCase();
                currentNode = currentNode.findNeighbor(lookupSynonyms(nextWord));
            }
            // If we found a match then increment detected tuples
            if (currentNode != null) detectedTuples++;
        }

        return (double) detectedTuples / totalTuples;
    }

    /**
     * @param inputStream text stream
     * @return list of words delimited in inputStream by punctuation and whitespace
     */
    private List<String> readWords(InputStream inputStream) {
        // Get words ignoring punctuation and spaces
        Scanner scanner = new Scanner(inputStream).useDelimiter("[ ,!?.]+");
        List<String> words = new ArrayList<String>();
        while (scanner.hasNext()) {
            words.add(scanner.next());
        }

        return words;
    }

    /**
     * @param word to lookup in thesaurus
     * @return set of synonyms of word
     */
    private Set<String> lookupSynonyms(String word) {
        return thesaurus.computeIfAbsent(word.toLowerCase(), k -> {
            // If no synonyms are found, make a new set containing this word
            Set<String> set = new HashSet();
            set.add(k);
            return set;
        });
    }

    /**
     * Creates a new synonym group in the thesaurus
     * using the set of words in synonyms
     *
     * @param synonyms array of words to add to new synonym group in thesaurus
     */
    private void addSynonymGroup(String[] synonyms) {
        Set<String> group = new HashSet<String>(synonyms.length);
        // associate previous synonyms with new group
        for (String word : synonyms) {
            Set<String> previousSynonyms = thesaurus.get(word.toLowerCase());
            if (previousSynonyms != null && !previousSynonyms.equals(group)) {
                group.addAll(previousSynonyms);
            }
            group.add(word.toLowerCase());
        }
        // ensure all words point to the same set
        for (String word : group) {
            thesaurus.put(word.toLowerCase(), group);
        }
    }
}
