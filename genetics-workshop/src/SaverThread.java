import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaverThread extends Thread{
    private String filePath;
    private List<String> sequencesToSave;

    public SaverThread(String filePath, List<String> sequencesToSave) {
        this.filePath = filePath;
        this.sequencesToSave = sequencesToSave;
    }

    public void run() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, true))) { // Write over existing text
            for (String sequence : this.sequencesToSave) {
                fileWriter.write(sequence);
                fileWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Writing error: " + e.getMessage());
        }
    }
}