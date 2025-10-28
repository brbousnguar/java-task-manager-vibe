package task.manager.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import task.manager.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for persisting tasks to/from JSON file
 * Handles file I/O operations for task data
 */
public class TaskRepository {
    
    private static final String DEFAULT_FILE_PATH = "tasks.json";
    private final String filePath;
    private final ObjectMapper objectMapper;
    
    /**
     * Default constructor using default file path
     */
    public TaskRepository() {
        this(DEFAULT_FILE_PATH);
    }
    
    /**
     * Constructor with custom file path
     * @param filePath path to the JSON file
     */
    public TaskRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        
        // Configure ObjectMapper for better JSON formatting and Java 8 time support
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Saves a list of tasks to JSON file
     * @param tasks list of tasks to save
     * @throws IOException if file writing fails
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        try {
            File file = new File(filePath);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Write tasks to JSON file
            objectMapper.writeValue(file, tasks);
            
        } catch (IOException e) {
            throw new IOException("Failed to save tasks to file: " + filePath, e);
        }
    }
    
    /**
     * Loads tasks from JSON file
     * @return list of tasks, empty list if file doesn't exist or is empty
     * @throws IOException if file reading fails
     */
    public List<Task> loadTasks() throws IOException {
        try {
            File file = new File(filePath);
            
            // Return empty list if file doesn't exist
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            // Return empty list if file is empty
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            
            // Read tasks from JSON file
            TypeReference<List<Task>> typeReference = new TypeReference<List<Task>>() {};
            List<Task> tasks = objectMapper.readValue(file, typeReference);
            
            return tasks != null ? tasks : new ArrayList<>();
            
        } catch (IOException e) {
            throw new IOException("Failed to load tasks from file: " + filePath, e);
        }
    }
    
    /**
     * Checks if the JSON file exists
     * @return true if file exists, false otherwise
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }
    
    /**
     * Deletes the JSON file
     * @return true if file was deleted or didn't exist, false if deletion failed
     */
    public boolean deleteFile() {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true; // File doesn't exist, consider as "deleted"
    }
    
    /**
     * Gets the file path being used
     * @return the JSON file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Gets the size of the JSON file
     * @return file size in bytes, -1 if file doesn't exist
     */
    public long getFileSize() {
        File file = new File(filePath);
        return file.exists() ? file.length() : -1;
    }
    
    /**
     * Creates a backup of the current JSON file
     * @param backupSuffix suffix to add to backup filename (e.g., ".backup")
     * @throws IOException if backup creation fails
     */
    public void createBackup(String backupSuffix) throws IOException {
        File originalFile = new File(filePath);
        if (!originalFile.exists()) {
            throw new IOException("Cannot create backup: original file does not exist");
        }
        
        String backupPath = filePath + backupSuffix;
        
        try {
            List<Task> tasks = loadTasks();
            TaskRepository backupRepo = new TaskRepository(backupPath);
            backupRepo.saveTasks(tasks);
        } catch (IOException e) {
            throw new IOException("Failed to create backup file: " + backupPath, e);
        }
    }
}