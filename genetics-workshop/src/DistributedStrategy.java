import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DistributedStrategy {

    private List<Thread> threads;
    private int nThreads;
    private long start; // Start for measure the execution time
    private long end;
    private ExecutorService executorService;
    
    public DistributedStrategy(int nThreads){

        this.threads = new ArrayList<>();
        this.nThreads = nThreads;
        this.start = System.nanoTime();
        this.executorService = Executors.newFixedThreadPool(nThreads + 1);
    }

    public void bootSaver(ArrayList<String> database, int size){
        threads.clear();
        int sublistSize = size / this.nThreads;
        int index = 0; // Hand the current position

        for(int i = 0; i < this.nThreads + 1; i++){
            
            int finalIndex;

            // Avoid out of bounds exception
            finalIndex = (index + sublistSize) > size ? size : index + sublistSize;

            List<String> sublist = database.subList(index, finalIndex);
            index = finalIndex;

            executorService.submit(new SaverThread("data/database.txt", sublist)); // Hand all threads
        }
        
        // Wait for each thread to finish
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
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