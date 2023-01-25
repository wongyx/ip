package duke;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import duke.exceptions.CorruptedFileException;
import duke.tasks.*;

public class Storage {
    private final String FILE_PATH;

    public Storage(String filePath) {
        this.FILE_PATH = filePath;
    }

    public ArrayList<Task> loadData() throws CorruptedFileException, IOException {
        ArrayList<Task> taskList = new ArrayList<>();
        File targetFile = new File(FILE_PATH);
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
        Scanner scanner = new Scanner(targetFile);
        while (scanner.hasNext()) {
            String taskData = scanner.nextLine();
            taskList.add(readData(taskData));
        }
        return taskList;
    }

    private Task readData(String data) throws CorruptedFileException {
        String[] dataSegments = data.split(" / ");
        String taskType = dataSegments[0];
        boolean isDone = dataSegments[1].equals("1");
        String taskDescription = dataSegments[2];
        if (taskType.equals("T")){
            return new Todo(taskDescription, isDone);
        } else if (taskType.equals("D")) {
            String[] taskInfo = taskDescription.split(" - ");
            try {
                LocalDate byDate = LocalDate.parse(taskInfo[1].trim(), DateTimeFormatter.ofPattern("d/MM/yyyy"));
                return new Deadline(taskInfo[0], byDate, isDone);
            } catch (DateTimeParseException e) {
                throw new CorruptedFileException("Saved files have corrupted data for dates.\n"
                        + "Delete data/tasks.txt and restart Duke to try again");
            }
        } else if (taskType.equals("E")) {
            String[] taskInfo = taskDescription.split("-");
            try {
                LocalDate fromDate = LocalDate.parse(taskInfo[1].trim(), DateTimeFormatter.ofPattern("d/MM/yyyy"));
                LocalDate toDate = LocalDate.parse(taskInfo[2].trim(), DateTimeFormatter.ofPattern("d/MM/yyyy"));
                return new Event(taskInfo[0], fromDate, toDate, isDone);
            } catch (DateTimeParseException e) {
                throw new CorruptedFileException("Saved files have corrupted data for dates.\n"
                        + "Delete data/tasks.txt and restart Duke to try again");
            }
        } else {
            throw new CorruptedFileException("Saved files have corrupted data for header.\n"
                                            + "Delete data/tasks.txt and restart Duke to try again");
        }
    }

    public void saveData(TaskList taskList) throws IOException {
        FileWriter fw = new FileWriter(FILE_PATH);
        for (Task task : taskList.tasks) {
            fw.write(task.toDataFormatString() + "\n");
        }
        fw.close();
    }
}