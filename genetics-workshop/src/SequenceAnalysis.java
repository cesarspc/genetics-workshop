import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class SequenceAnalysis{
    
    // Parameters
    private static final int DATABASE_SIZE = 1000;
    private static final int SEQUENCE_SIZE = 20;
    private static final double[] WEIGHTS = {0.25, 0.25, 0.25, 0.25}; // Must total 1
    private static final char[] NUCLEOTIDES = {'A', 'C', 'G', 'T'};
    private static ArrayList<String> database = new ArrayList<String>();

    public SequenceAnalysis(){
        
    }

    // Method for random generation of database
    public static void generateDatabase(){
        for(int i = 0; i < DATABASE_SIZE; i++){
            String sequence = "";
            for(int j = 0; j < SEQUENCE_SIZE; j++){
                sequence += pickNucleotide();
            }
            database.add(sequence);
        }

        // Save txt
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("databases/database.txt"))) {
            for (String sequence : database) {
                fileWriter.write(sequence);
                fileWriter.newLine();  // Add a newline after each sequence
            }
        } catch (IOException e) {
            System.err.println("Writing error: " + e.getMessage());
        }
    }

    // Pick nucleotide with given probabilities
    public static char pickNucleotide(){
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
}