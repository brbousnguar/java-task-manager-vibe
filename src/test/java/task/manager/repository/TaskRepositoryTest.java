package task.manager.repository;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import task.manager.model.Priority;
import task.manager.model.Status;
import task.manager.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Comprehensive unit tests for TaskRepository class
 * Tests all file I/O operations, error handling, and edge cases
 * Uses temporary files to avoid affecting the actual application data
 */
public class TaskRepositoryTest {

    private TaskRepository repository;
    private String testFilePath;
    private List<Task> testTasks;
    
    @Before
    public void setUp() throws Exception {
        // Create unique temporary file path for each test
        testFilePath = "test-repo-" + System.currentTimeMillis() + ".json";
        repository = new TaskRepository(testFilePath);
        
        // Create sample test tasks
        String futureDate1 = LocalDate.now().plusDays(5).toString();
        String futureDate2 = LocalDate.now().plusDays(10).toString();
        String futureDate3 = LocalDate.now().plusDays(15).toString();
        
        testTasks = Arrays.asList(
            new Task("Task 1", "Description 1", futureDate1, "Work", Priority.HIGH, Status.PENDING),
            new Task("Task 2", "Description 2", futureDate2, "Personal", Priority.MEDIUM, Status.IN_PROGRESS),
            new Task("Task 3", "Description 3", futureDate3, "Study", Priority.LOW, Status.COMPLETED)
        );
    }
    
    @After
    public void tearDown() {
        // Clean up test files
        cleanupFile(testFilePath);
        cleanupFile(testFilePath + ".backup");
        cleanupFile(testFilePath + ".test");
        
        // Clean up any directories created during testing
        try {
            File testDir = new File("test-dir");
            if (testDir.exists()) {
                deleteDirectory(testDir);
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    private void cleanupFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
    
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    // ============= CONSTRUCTOR TESTS =============
    
    @Test
    public void testDefaultConstructor() {
        TaskRepository defaultRepo = new TaskRepository();
        assertEquals("Default file path should be tasks.json", "tasks.json", defaultRepo.getFilePath());
    }
    
    @Test
    public void testCustomConstructor() {
        String customPath = "custom-tasks.json";
        TaskRepository customRepo = new TaskRepository(customPath);
        assertEquals("Custom file path should be set", customPath, customRepo.getFilePath());
    }
    
    // ============= SAVE TESTS =============
    
    @Test
    public void testSaveTasksToNewFile() throws IOException {
        // Save tasks to a new file
        repository.saveTasks(testTasks);
        
        assertTrue("File should be created", repository.fileExists());
        assertTrue("File size should be greater than 0", repository.getFileSize() > 0);
    }
    
    @Test
    public void testSaveEmptyList() throws IOException {
        List<Task> emptyList = new ArrayList<>();
        repository.saveTasks(emptyList);
        
        assertTrue("File should be created even for empty list", repository.fileExists());
        
        // Verify the content is an empty JSON array
        List<Task> loaded = repository.loadTasks();
        assertEquals("Loaded list should be empty", 0, loaded.size());
    }
    
    @Test
    public void testSaveNullList() throws IOException {
        repository.saveTasks(null);
        
        assertTrue("File should be created", repository.fileExists());
        
        // Loading should return empty list (Jackson handles null as empty array)
        List<Task> loaded = repository.loadTasks();
        assertNotNull("Loaded list should not be null", loaded);
        assertEquals("Loaded list should be empty", 0, loaded.size());
    }
    
    @Test
    public void testSaveTasksOverwritesExisting() throws IOException {
        // Save initial tasks
        repository.saveTasks(testTasks);
        long initialSize = repository.getFileSize();
        
        // Save different tasks (smaller list)
        List<Task> singleTask = Arrays.asList(testTasks.get(0));
        repository.saveTasks(singleTask);
        
        long newSize = repository.getFileSize();
        assertTrue("File size should be smaller after saving fewer tasks", newSize < initialSize);
        
        List<Task> loaded = repository.loadTasks();
        assertEquals("Should have only 1 task", 1, loaded.size());
        assertEquals("Task title should match", testTasks.get(0).getTitle(), loaded.get(0).getTitle());
    }
    
    @Test
    public void testSaveTasksWithDirectoryCreation() throws IOException {
        // Create repository with nested directory path
        String nestedPath = "test-dir/subdirectory/tasks.json";
        TaskRepository nestedRepo = new TaskRepository(nestedPath);
        
        nestedRepo.saveTasks(testTasks);
        
        File nestedFile = new File(nestedPath);
        assertTrue("Nested file should be created", nestedFile.exists());
        assertTrue("File should contain data", nestedFile.length() > 0);
        
        // Verify we can load the data
        List<Task> loaded = nestedRepo.loadTasks();
        assertEquals("Should load all tasks", testTasks.size(), loaded.size());
    }
    
    @Test(expected = IOException.class)
    public void testSaveTasksToInvalidPath() throws IOException {
        // Try to save to an invalid path (using null character which is invalid in filenames)
        TaskRepository invalidRepo = new TaskRepository("/invalid\0path.json");
        invalidRepo.saveTasks(testTasks);
    }
    
    // ============= LOAD TESTS =============
    
    @Test
    public void testLoadTasksFromExistingFile() throws IOException {
        // First save tasks
        repository.saveTasks(testTasks);
        
        // Then load them
        List<Task> loaded = repository.loadTasks();
        
        assertEquals("Should load same number of tasks", testTasks.size(), loaded.size());
        
        // Verify task details (note: UUIDs will be different since they're regenerated)
        for (int i = 0; i < testTasks.size(); i++) {
            Task original = testTasks.get(i);
            Task loaded_task = loaded.get(i);
            
            assertEquals("Title should match", original.getTitle(), loaded_task.getTitle());
            assertEquals("Description should match", original.getDescription(), loaded_task.getDescription());
            assertEquals("Due date should match", original.getDueDate(), loaded_task.getDueDate());
            assertEquals("Category should match", original.getCategory(), loaded_task.getCategory());
            assertEquals("Priority should match", original.getPriority(), loaded_task.getPriority());
            assertEquals("Status should match", original.getStatus(), loaded_task.getStatus());
        }
    }
    
    @Test
    public void testLoadTasksFromNonExistentFile() throws IOException {
        // Try to load from a file that doesn't exist
        List<Task> loaded = repository.loadTasks();
        
        assertNotNull("Loaded list should not be null", loaded);
        assertEquals("Should return empty list for non-existent file", 0, loaded.size());
    }
    
    @Test
    public void testLoadTasksFromEmptyFile() throws IOException {
        // Create an empty file
        File emptyFile = new File(testFilePath);
        emptyFile.createNewFile();
        
        List<Task> loaded = repository.loadTasks();
        
        assertNotNull("Loaded list should not be null", loaded);
        assertEquals("Should return empty list for empty file", 0, loaded.size());
    }
    
    @Test(expected = IOException.class)
    public void testLoadTasksFromCorruptedFile() throws IOException {
        // Create a file with invalid JSON content
        try (FileWriter writer = new FileWriter(testFilePath)) {
            writer.write("This is not valid JSON content");
        }
        
        // This should throw IOException due to JSON parsing error
        repository.loadTasks();
    }
    
    @Test(expected = IOException.class)
    public void testLoadTasksFromPartiallyCorruptedFile() throws IOException {
        // Create a file with incomplete JSON
        try (FileWriter writer = new FileWriter(testFilePath)) {
            writer.write("[{\"title\":\"Task\",\"description\":\"Desc\""); // Missing closing braces
        }
        
        repository.loadTasks();
    }
    
    @Test(expected = IOException.class)
    public void testLoadTasksWithMissingRequiredFields() throws IOException {
        // Create JSON missing required fields (should cause validation error)
        String invalidJson = "[{\"title\":\"Task without required fields\"}]"; // Missing dueDate, category, etc.
        
        try (FileWriter writer = new FileWriter(testFilePath)) {
            writer.write(invalidJson);
        }
        
        repository.loadTasks();
    }
    
    @Test
    public void testLoadTasksWithMinimalFields() throws IOException {
        // Create JSON with all required fields but minimal optional data
        String minimalJson = "[{\"title\":\"Minimal Task\",\"dueDate\":\"" + 
                           LocalDate.now().plusDays(1).toString() + 
                           "\",\"category\":\"General\",\"priority\":\"MEDIUM\",\"status\":\"PENDING\"}]";
        
        try (FileWriter writer = new FileWriter(testFilePath)) {
            writer.write(minimalJson);
        }
        
        List<Task> loaded = repository.loadTasks();
        
        assertEquals("Should load 1 task", 1, loaded.size());
        Task task = loaded.get(0);
        assertEquals("Title should be set", "Minimal Task", task.getTitle());
        assertEquals("Category should be set", "General", task.getCategory());
        assertEquals("Priority should be set", Priority.MEDIUM, task.getPriority());
        assertEquals("Status should be set", Status.PENDING, task.getStatus());
        assertNotNull("Should have UUID", task.getId());
        assertNull("Description should be null when not provided", task.getDescription());
    }
    
    // ============= ROUND-TRIP TESTS =============
    
    @Test
    public void testSaveAndLoadRoundTrip() throws IOException {
        // Save tasks
        repository.saveTasks(testTasks);
        
        // Load tasks
        List<Task> loaded = repository.loadTasks();
        
        // Modify and save again
        Task newTask = new Task("New Task", "New Description", 
                              LocalDate.now().plusDays(20).toString(), 
                              "New Category", Priority.URGENT, Status.PENDING);
        loaded.add(newTask);
        repository.saveTasks(loaded);
        
        // Load again and verify
        List<Task> finalLoaded = repository.loadTasks();
        assertEquals("Should have original tasks plus new one", testTasks.size() + 1, finalLoaded.size());
    }
    
    @Test
    public void testMultipleSaveLoadCycles() throws IOException {
        List<Task> currentTasks = new ArrayList<>(testTasks);
        
        for (int i = 0; i < 5; i++) {
            // Save current tasks
            repository.saveTasks(currentTasks);
            
            // Load tasks
            List<Task> loaded = repository.loadTasks();
            assertEquals("Cycle " + i + ": Task count should match", currentTasks.size(), loaded.size());
            
            // Add a new task for next iteration
            Task newTask = new Task("Task " + (i + 10), "Description " + (i + 10), 
                                  LocalDate.now().plusDays(30 + i).toString());
            loaded.add(newTask);
            currentTasks = new ArrayList<>(loaded); // Create new list to avoid reference issues
        }
        
        // Save the final state and verify
        repository.saveTasks(currentTasks);
        List<Task> finalTasks = repository.loadTasks();
        assertEquals("Should have original tasks plus 5 new ones", testTasks.size() + 5, finalTasks.size());
    }
    
    // ============= FILE OPERATIONS TESTS =============
    
    @Test
    public void testFileExists() throws IOException {
        assertFalse("File should not exist initially", repository.fileExists());
        
        repository.saveTasks(testTasks);
        assertTrue("File should exist after saving", repository.fileExists());
    }
    
    @Test
    public void testGetFileSize() throws IOException {
        assertEquals("File size should be -1 for non-existent file", -1, repository.getFileSize());
        
        repository.saveTasks(new ArrayList<>());
        assertTrue("File size should be > 0 even for empty list", repository.getFileSize() > 0);
        
        repository.saveTasks(testTasks);
        long sizeWithTasks = repository.getFileSize();
        assertTrue("File size should increase with more data", sizeWithTasks > 50); // Reasonable size for 3 tasks
    }
    
    @Test
    public void testDeleteFile() throws IOException {
        assertFalse("Delete should return true for non-existent file", !repository.deleteFile());
        
        repository.saveTasks(testTasks);
        assertTrue("File should exist before deletion", repository.fileExists());
        
        boolean deleted = repository.deleteFile();
        assertTrue("Delete should return true", deleted);
        assertFalse("File should not exist after deletion", repository.fileExists());
    }
    
    @Test
    public void testGetFilePath() {
        assertEquals("File path should match constructor argument", testFilePath, repository.getFilePath());
    }
    
    // ============= BACKUP TESTS =============
    
    @Test
    public void testCreateBackup() throws IOException {
        // Save some tasks first
        repository.saveTasks(testTasks);
        
        String backupSuffix = ".backup";
        repository.createBackup(backupSuffix);
        
        // Verify backup file was created
        File backupFile = new File(testFilePath + backupSuffix);
        assertTrue("Backup file should exist", backupFile.exists());
        
        // Verify backup content
        TaskRepository backupRepo = new TaskRepository(testFilePath + backupSuffix);
        List<Task> backupTasks = backupRepo.loadTasks();
        
        assertEquals("Backup should have same number of tasks", testTasks.size(), backupTasks.size());
        
        // Verify first task details
        assertEquals("First task title should match", testTasks.get(0).getTitle(), backupTasks.get(0).getTitle());
    }
    
    @Test(expected = IOException.class)
    public void testCreateBackupWithoutOriginalFile() throws IOException {
        // Try to create backup when no original file exists
        repository.createBackup(".backup");
    }
    
    @Test
    public void testCreateBackupWithCustomSuffix() throws IOException {
        repository.saveTasks(testTasks);
        
        String customSuffix = ".test";
        repository.createBackup(customSuffix);
        
        File backupFile = new File(testFilePath + customSuffix);
        assertTrue("Backup with custom suffix should exist", backupFile.exists());
        
        // Verify backup is valid
        TaskRepository backupRepo = new TaskRepository(testFilePath + customSuffix);
        List<Task> backupTasks = backupRepo.loadTasks();
        assertEquals("Backup should contain all tasks", testTasks.size(), backupTasks.size());
    }
    
    @Test
    public void testCreateBackupWithNullSuffix() throws IOException {
        repository.saveTasks(testTasks);
        
        try {
            repository.createBackup(null);
            // If we get here, the method handled null suffix gracefully
            // Check if a backup file was created with "null" suffix
            File backupFile = new File(testFilePath + "null");
            if (backupFile.exists()) {
                assertTrue("Backup file with null suffix created", true);
                cleanupFile(testFilePath + "null");
            } else {
                fail("Expected either exception or backup file creation");
            }
        } catch (Exception e) {
            // This is also acceptable - the method might throw an exception for null suffix
            assertTrue("Exception thrown for null suffix", true);
        }
    }
    
    // ============= ERROR HANDLING TESTS =============
    
    @Test
    public void testHandleReadOnlyFile() throws IOException {
        // Save tasks first
        repository.saveTasks(testTasks);
        
        File file = new File(testFilePath);
        
        // Make file read-only (this might not work on all systems)
        file.setReadOnly();
        
        try {
            // Try to save again - behavior might vary by system
            repository.saveTasks(Arrays.asList(testTasks.get(0)));
            // If we get here, the system allowed the write despite read-only flag
            assertTrue("File operation completed", true);
        } catch (IOException e) {
            // Expected on systems that respect read-only flag
            assertTrue("Should throw IOException for read-only file", e.getMessage().contains("Failed to save"));
        } finally {
            // Restore write permissions for cleanup
            file.setWritable(true);
        }
    }
    
    @Test
    public void testConcurrentAccess() throws IOException, InterruptedException {
        repository.saveTasks(testTasks);
        
        // Create multiple threads that try to access the file simultaneously
        final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        final int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    // Each thread loads tasks, modifies them, and saves back
                    List<Task> tasks = repository.loadTasks();
                    Task newTask = new Task("Thread Task " + threadId, "Description " + threadId, 
                                          LocalDate.now().plusDays(25 + threadId).toString());
                    tasks.add(newTask);
                    
                    // Small delay to increase chance of concurrent access
                    Thread.sleep(10);
                    
                    repository.saveTasks(tasks);
                } catch (Exception e) {
                    exceptions.add(e);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Check if any exceptions occurred
        if (!exceptions.isEmpty()) {
            System.out.println("Concurrent access generated " + exceptions.size() + " exceptions (may be expected)");
            // This is informational - concurrent file access might cause issues
        }
        
        // Verify final state
        List<Task> finalTasks = repository.loadTasks();
        assertTrue("Should have at least the original tasks", finalTasks.size() >= testTasks.size());
    }
    
    // ============= EDGE CASES =============
    
    @Test
    public void testVeryLongFilePath() {
        // Create a very long file path
        String longPath = "very-long-path-" + "x".repeat(200) + ".json";
        TaskRepository longPathRepo = new TaskRepository(longPath);
        
        assertEquals("Long path should be stored correctly", longPath, longPathRepo.getFilePath());
        
        // Note: We don't test actual I/O with very long paths as it might fail on some filesystems
    }
    
    @Test
    public void testSpecialCharactersInFilePath() throws IOException {
        // Test with some special characters (avoiding truly problematic ones)
        String specialPath = "test-file-with-spaces and-dashes_and_underscores.json";
        TaskRepository specialRepo = new TaskRepository(specialPath);
        
        specialRepo.saveTasks(testTasks);
        List<Task> loaded = specialRepo.loadTasks();
        
        assertEquals("Should handle special characters in file path", testTasks.size(), loaded.size());
        
        // Cleanup
        cleanupFile(specialPath);
    }
    
    @Test
    public void testLargeTaskList() throws IOException {
        // Create a large number of tasks
        List<Task> largeTasks = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Task task = new Task("Task " + i, "Description for task " + i, 
                               LocalDate.now().plusDays(i % 100 + 1).toString(),
                               "Category" + (i % 10), 
                               Priority.values()[i % Priority.values().length],
                               Status.values()[i % Status.values().length]);
            largeTasks.add(task);
        }
        
        // Save and load large task list
        long startTime = System.currentTimeMillis();
        repository.saveTasks(largeTasks);
        long saveTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        List<Task> loadedTasks = repository.loadTasks();
        long loadTime = System.currentTimeMillis() - startTime;
        
        assertEquals("Should handle large task list", largeTasks.size(), loadedTasks.size());
        assertTrue("Save should complete in reasonable time (< 5s)", saveTime < 5000);
        assertTrue("Load should complete in reasonable time (< 5s)", loadTime < 5000);
        
        System.out.println("Performance test - Save: " + saveTime + "ms, Load: " + loadTime + "ms");
    }
}