import java.util.ArrayList;
import java.util.List;

public class DistributedStrategy {

    private List<Thread> threads;
    private int splits;
    private long start; // Start for measure the execution time
    private long end;
    
    public DistributedStrategy(int splits){

        this.threads = new ArrayList<>();
        this.splits = splits;
        this.start = System.nanoTime();
    }

    public void bootSaver(ArrayList<String> database){
        threads.clear();
        int sublistSize = database.size() / this.splits;
        int index = 0; // Hand the current position
        List<String> sublist = new ArrayList<>();

        for(int i = 0; i < this.splits + 1; i++){
            
            int finalIndex;

            // Avoid out of bounds exception
            if (index + sublistSize > database.size()){
                finalIndex = database.size();
            } else {
                finalIndex = index + sublistSize;
            }

            sublist = database.subList(index, finalIndex);
            index = finalIndex;
            SaverThread saver = new SaverThread("data/database.txt", sublist);
            threads.add(saver);
            saver.start();
        }
        

        for(Thread saver : threads){
            try {
                saver.join(); // Waits for each thread to finish
            } catch(InterruptedException e) {
                System.err.println("Interrupted exception: " + e.getMessage());
            }
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