import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SequenceAnalysis{
    
    // Parameters
    public static final int DATABASE_SIZE = 10;
    private static final int SEQUENCE_SIZE = 10;
    private static final int DISTRIBUTION_RATE = 10; // Number of threads for distribute demanding tasks
    private static final double[] WEIGHTS = {0.25, 0.25, 0.25, 0.25}; // Must total 1
    private static final char[] NUCLEOTIDES = {'A', 'C', 'G', 'T'};
    private static ArrayList<String> database = new ArrayList<String>();

    public SequenceAnalysis(){
        
    }

    // Method for random generation of database
    public static void generateDatabase(){
        DistributedStrategy dStrategy = new DistributedStrategy(DISTRIBUTION_RATE);
        // Save txt
        dStrategy.bootSaver(database, DATABASE_SIZE);
    }

    // Pick nucleotide with given probabilities
    private static char pickNucleotide(){
        double sum = 0; // Cumulative probability
        double randNumber = new Random().nextDouble();

        for(int i = 0; i < 4; i++){
            sum += WEIGHTS[i];
            if(randNumber < sum){
                return NUCLEOTIDES[i];
            }
        }

        return ' ';
    }

    public static String generateSequence(){
        String sequence = "";
            for(int i = 0; i < SEQUENCE_SIZE; i++){
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
}