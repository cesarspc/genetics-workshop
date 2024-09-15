import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DistributedStrategy {

    private int nThreads;
    private long start; // Start for measure the execution time
    private long end;
    private ExecutorService executorService;
    
    public DistributedStrategy(int nThreads){
        this.nThreads = nThreads;
        this.start = System.nanoTime();
        this.executorService = Executors.newFixedThreadPool(nThreads + 1);
    }

    public void bootSaver(ArrayList<String> database, int size){
        int sublistSize = size / this.nThreads;
        int sequenceIndex = 0; // Hand the current position

        for(int i = 0; i < this.nThreads + 1; i++){
            
            int currentSize;
            // Avoid out of bounds exception
            currentSize = (sequenceIndex + sublistSize) > size ? size - sequenceIndex : sublistSize;

            executorService.submit(new SaverThread("data/database.txt", SequenceAnalysis.generateList(currentSize))); // Hand all threads

            sequenceIndex += currentSize;
        }
        
        // Wait for each thread to finish
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            System.err.println("Saving error: " + e.getMessage());
        }
        
        List<String> finalText = new ArrayList<>();
        finalText.add("--------------------");

        // Insert final string at the end of sequences
        new SaverThread("data/database.txt", finalText).start();

        this.finishTask();
    }

   

    public void finishTask() {
        end = System.nanoTime();
        long duration = (end - start) / 1000000;

        System.out.println("Execution time: " + duration + "ms.");
    }
}