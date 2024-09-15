import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaverThread extends Thread{
    private String filePath;
    private int nSequences;

    public SaverThread(String filePath, int nSequences) {
        this.filePath = filePath;
        this.nSequences = nSequences;
    }

    public void run() {
        List<String> sequences = SequenceAnalysis.generateList(nSequences);
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, true))) { // Write over existing text
            for (String sequence : sequences) {
                fileWriter.write(sequence);
                fileWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Writing error: " + e.getMessage());
        }
    }
}