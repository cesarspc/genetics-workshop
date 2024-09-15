import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class MotifThread extends Thread{
    private String filePath;
    private HashMap<String, Integer> candidates;
    private int firstIndex;
    private int secondIndex;
    private int motifSize;

    public MotifThread(String filePath, HashMap<String, Integer> candidates, int firstIndex, int secondIndex, int motifSize) {
        this.filePath = filePath;
        this.candidates = candidates;
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        this.motifSize = motifSize;
    }

    public void run() {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 0;

            while ((line = reader.readLine()) != null) {
                // Iterate on selected lines
                if (currentLine > firstIndex - 1 && currentLine < secondIndex + 1) {
                    
                    // For each candidate
                    for(String candidate : candidates.keySet()){
                        int counter = 0;
                        int index = 0;
                        // Count how many ocurrences of candidate there are in each line
                        while ((index = line.indexOf(candidate, index)) != -1) { // Find since last index
                            index += motifSize;
                            counter += 1;
                        }
                        
                        candidates.put(candidate, candidates.get(candidate) + counter); // Sumar contador

                    }
                }
                currentLine++;
            }
        } catch (Exception e){
            System.err.println("Reading error: " + e.getMessage());
        }

    }
}