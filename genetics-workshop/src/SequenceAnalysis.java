import java.util.Vector;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

class SequenceAnalysis{
    
    // Parameters
    private static int databaseSize = 1500;
    private static int sequenceSize = 10;
    private static double[] weights = {0.25, 0.25, 0.25, 0.25}; // Must total 1
    private static String databasePath = "data/database.txt";

    private static final char[] NUCLEOTIDES = {'A', 'C', 'G', 'T'};

    // Approximate number of threads for distribute demanding tasks (2 is the number that gives the best result on my pc)
    private static final int DISTRIBUTION_RATE = 2;
    private int motifSize;

    private HashMap<String, Integer> candidates;

    public SequenceAnalysis(int motifSize){
        candidates = new HashMap<>();
        this.motifSize = motifSize;
        for(String candidate : findCombinations(this.motifSize)){
            candidates.put(candidate, 0);
        }
    }

    // Method for random generation of database
    public void distributedAnalysis(){
        DistributedStrategy dStrategy = new DistributedStrategy(DISTRIBUTION_RATE, databasePath);
        // Save txt
        dStrategy.bootAnalysis(databaseSize, candidates, motifSize);
        shannonEntropy();
    }

    // Pick nucleotide with given probabilities
    private static char pickNucleotide(){
        double sum = 0; // Cumulative probability
        double randNumber = new Random().nextDouble();

        for(int i = 0; i < 4; i++){
            sum += weights[i];
            if(randNumber < sum){
                return NUCLEOTIDES[i];
            }
        }

        return ' ';
    }

    private static String generateSequence(){
        String sequence = "";
            for(int i = 0; i < sequenceSize; i++){
                sequence += pickNucleotide();
            }
        return sequence;
    }

    public static List<String> generateList(int n){
        List<String> sublist = new Vector<>();
            for(int i = 0; i < n; i++){
                sublist.add(generateSequence());
            }
        return sublist;
    }

    // Recursive method to iterate for all combinations
    private void generateCombinations(String current, int size, List<String> candidates) {
        if (current.length() == size) {
            candidates.add(current);
            return;
        }

        for (char nucleotide : NUCLEOTIDES) {
            generateCombinations(current + nucleotide, size, candidates);
        }
    }

    public List<String> findCombinations(int combinationSize) {
        List<String> candidates = new ArrayList<>();
        generateCombinations("", combinationSize, candidates);
        return candidates;
    }

    public void countMotifOcurrences(){
        DistributedStrategy dStrategy = new DistributedStrategy(DISTRIBUTION_RATE, databasePath);
        // Save txt
        dStrategy.entryMotifs(candidates, motifSize);
    }

    public void shannonEntropy() {
        int totalMotifs = 0;
        for (int count : candidates.values()) {
            totalMotifs += count;
        }
    
        // Calculate probabilities for each motif and use them for entropy formula
        double entropy = 0.0;
        for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
            int frequency = entry.getValue();
            if (frequency > 0) {
                double probability = (double) frequency / totalMotifs;
                entropy += probability * Math.log(probability) / Math.log(2); // Log properties for log base 2 probability
            }
        }
    
        // Invert sign 
        entropy = -entropy;
    
        System.out.println("Shannon entropy: " + entropy);
    }
}