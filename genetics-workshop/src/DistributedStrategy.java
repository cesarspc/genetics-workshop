import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.io.FileWriter;


public class DistributedStrategy {

    private int nThreads;
    private long start; // Start for measure the execution time
    private long end;
    private ExecutorService executorServiceDatabase;
    private ExecutorService executorServiceMotif;
    private String filePath;
    
    public DistributedStrategy(int nThreads, String filePath){
        this.nThreads = nThreads;
        this.start = System.nanoTime();
        this.executorServiceDatabase = Executors.newFixedThreadPool(nThreads + 1);
        this.executorServiceMotif = Executors.newFixedThreadPool(nThreads + 1);
        this.filePath = filePath;
    }

    public void bootAnalysis(int databaseSize, HashMap<String, Integer> candidates, int motifSize){
        int sublistSize = databaseSize / this.nThreads;
        int sequenceIndex = 0; // Hand the current position

        // Clear file
        try{
            FileWriter writer = new FileWriter(filePath, false);
            writer.write(""); // Optional: explicitly write an empty string
            writer.close();
        } catch (Exception e){
            System.err.println("Writing error: " + e.getMessage());
        }

        for(int i = 0; i < this.nThreads + 1; i++){

            int currentSize;
            // Avoid out of bounds exception
            currentSize = (sequenceIndex + sublistSize) > databaseSize ? databaseSize - sequenceIndex : sublistSize;

            executorServiceDatabase.submit(new SaverThread(filePath, currentSize)); // Hand all threads

            sequenceIndex += currentSize;
        }
        
        // Wait for each thread to finish
        executorServiceDatabase.shutdown();
        try {
            if (!executorServiceDatabase.awaitTermination(30, TimeUnit.SECONDS)) {
                executorServiceDatabase.shutdownNow();
            }

            entryMotifs(candidates, motifSize);
        } catch (InterruptedException e) {
            executorServiceDatabase.shutdownNow();
            System.err.println("Saving error: " + e.getMessage());
        }

        
    }

    // Method for print the time execution time
    public void finishTask() {
        end = System.nanoTime();
        long duration = (end - start) / 1000000;

        System.out.println("Execution time: " + duration + "ms.");
    }

    public void entryMotifs(HashMap<String, Integer> candidates, int motifSize){
        int sublistSize = candidates.size() / this.nThreads;
        int position = 0;
        int finalPosition;

        for(int i = 0; i < nThreads + 1; i++){
            finalPosition = (position + sublistSize) >= candidates.size() ? candidates.size() -1 : (position + sublistSize);
            executorServiceMotif.submit(new MotifThread(filePath, candidates, position, finalPosition, motifSize));
            position = finalPosition + 1;
        }

        // Wait for each thread to finish
        executorServiceMotif.shutdown();
        try {
            if (!executorServiceMotif.awaitTermination(30, TimeUnit.SECONDS)) {
                executorServiceMotif.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorServiceMotif.shutdownNow();
            System.err.println("Saving error: " + e.getMessage());
        }

        System.out.println(candidates);
        
    }
    
}