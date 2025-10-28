package task.manager.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import task.manager.model.Priority;
import task.manager.model.Status;
import task.manager.model.Task;
import task.manager.repository.TaskRepository;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

/**
 * Comprehensive unit tests for TaskService class
 * Tests all CRUD operations, filtering methods, and edge cases
 * Uses a temporary file for testing to avoid mocking issues with Java 25
 */
public class TaskServiceTest {

    private TaskService taskService;
    private TaskRepository testRepository;
    private String testFilePath;
    
    @Before
    public void setUp() throws Exception {
        // Create temporary file for testing
        testFilePath = "test-tasks-" + System.currentTimeMillis() + ".json";
        testRepository = new TaskRepository(testFilePath);
        taskService = new TaskService(testRepository);
    }
    
    @After
    public void tearDown() {
        // Clean up test file
        File testFile = new File(testFilePath);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        // Also clean up any backup files
        File backupFile = new File(testFilePath + ".backup");
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }
    
    // ============= CREATE TESTS =============
    
    @Test
    public void testCreateTaskWithBasicFields() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        
        Task createdTask = taskService.createTask("New Task", "New Description", futureDate);
        
        assertNotNull("Created task should not be null", createdTask);
        assertEquals("Task title should match", "New Task", createdTask.getTitle());
        assertEquals("Task description should match", "New Description", createdTask.getDescription());
        assertEquals("Task due date should match", futureDate, createdTask.getDueDate());
        assertEquals("Default category should be General", "General", createdTask.getCategory());
        assertEquals("Default priority should be MEDIUM", Priority.MEDIUM, createdTask.getPriority());
        assertEquals("Default status should be PENDING", Status.PENDING, createdTask.getStatus());
        assertNotNull("Task should have UUID", createdTask.getId());
        
        // Verify task was added to service
        assertEquals("Service should contain 1 task", 1, taskService.getAllTasks().size());
    }
    
    @Test
    public void testCreateTaskWithAllFields() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        
        Task createdTask = taskService.createTask("Complete Task", "Full description", 
                futureDate, "Development", Priority.URGENT, Status.IN_PROGRESS);
        
        assertNotNull("Created task should not be null", createdTask);
        assertEquals("Task title should match", "Complete Task", createdTask.getTitle());
        assertEquals("Task description should match", "Full description", createdTask.getDescription());
        assertEquals("Task due date should match", futureDate, createdTask.getDueDate());
        assertEquals("Task category should match", "Development", createdTask.getCategory());
        assertEquals("Task priority should match", Priority.URGENT, createdTask.getPriority());
        assertEquals("Task status should match", Status.IN_PROGRESS, createdTask.getStatus());
        
        assertEquals("Service should contain 1 task", 1, taskService.getAllTasks().size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTaskWithNullTitle() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask(null, "Description", futureDate);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTaskWithEmptyTitle() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("", "Description", futureDate);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTaskWithPastDate() {
        String pastDate = LocalDate.now().minusDays(1).toString();
        taskService.createTask("Task", "Description", pastDate);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateTaskWithInvalidDateFormat() {
        taskService.createTask("Task", "Description", "invalid-date");
    }
    
    // ============= READ TESTS =============
    
    @Test
    public void testGetTaskById() {
        // Add a task first
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Test Task", "Description", futureDate);
        UUID taskId = createdTask.getId();
        
        Task retrievedTask = taskService.getTaskById(taskId);
        
        assertNotNull("Retrieved task should not be null", retrievedTask);
        assertEquals("Task IDs should match", taskId, retrievedTask.getId());
        assertEquals("Task titles should match", "Test Task", retrievedTask.getTitle());
    }
    
    @Test
    public void testGetTaskByIdWithNullId() {
        Task result = taskService.getTaskById(null);
        assertNull("Result should be null for null ID", result);
    }
    
    @Test
    public void testGetTaskByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        Task result = taskService.getTaskById(nonExistentId);
        assertNull("Result should be null for non-existent ID", result);
    }
    
    @Test
    public void testGetAllTasks() {
        // Initially should be empty
        List<Task> tasks = taskService.getAllTasks();
        assertEquals("Initial task list should be empty", 0, tasks.size());
        
        // Add some tasks
        String futureDate1 = LocalDate.now().plusDays(5).toString();
        String futureDate2 = LocalDate.now().plusDays(10).toString();
        taskService.createTask("Task 1", "Description 1", futureDate1);
        taskService.createTask("Task 2", "Description 2", futureDate2);
        
        tasks = taskService.getAllTasks();
        assertEquals("Should have 2 tasks", 2, tasks.size());
        
        // Verify it returns a copy (modifying returned list doesn't affect internal list)
        tasks.clear();
        List<Task> tasksAfterClear = taskService.getAllTasks();
        assertEquals("Internal list should still have 2 tasks", 2, tasksAfterClear.size());
    }
    
    // ============= UPDATE TESTS =============
    
    @Test
    public void testUpdateTaskBasicFields() {
        // Create a task first
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Original Title", "Original Description", futureDate);
        UUID taskId = createdTask.getId();
        
        // Update the task
        String newFutureDate = LocalDate.now().plusDays(10).toString();
        Task updatedTask = taskService.updateTask(taskId, "Updated Title", "Updated Description", newFutureDate);
        
        assertNotNull("Updated task should not be null", updatedTask);
        assertEquals("Task ID should remain same", taskId, updatedTask.getId());
        assertEquals("Title should be updated", "Updated Title", updatedTask.getTitle());
        assertEquals("Description should be updated", "Updated Description", updatedTask.getDescription());
        assertEquals("Due date should be updated", newFutureDate, updatedTask.getDueDate());
    }
    
    @Test
    public void testUpdateTaskAllFields() {
        // Create a task first
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Original Title", "Original Description", futureDate);
        UUID taskId = createdTask.getId();
        
        // Update all fields
        String newFutureDate = LocalDate.now().plusDays(10).toString();
        Task updatedTask = taskService.updateTask(taskId, "Updated Title", "Updated Description", 
                newFutureDate, "Updated Category", Priority.URGENT, Status.COMPLETED);
        
        assertNotNull("Updated task should not be null", updatedTask);
        assertEquals("Title should be updated", "Updated Title", updatedTask.getTitle());
        assertEquals("Description should be updated", "Updated Description", updatedTask.getDescription());
        assertEquals("Due date should be updated", newFutureDate, updatedTask.getDueDate());
        assertEquals("Category should be updated", "Updated Category", updatedTask.getCategory());
        assertEquals("Priority should be updated", Priority.URGENT, updatedTask.getPriority());
        assertEquals("Status should be updated", Status.COMPLETED, updatedTask.getStatus());
    }
    
    @Test
    public void testUpdateNonExistentTask() {
        UUID nonExistentId = UUID.randomUUID();
        String futureDate = LocalDate.now().plusDays(5).toString();
        
        Task result = taskService.updateTask(nonExistentId, "Title", "Description", futureDate);
        
        assertNull("Result should be null for non-existent task", result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateTaskWithInvalidTitle() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Original Title", "Description", futureDate);
        
        taskService.updateTask(createdTask.getId(), null, "Description", futureDate);
    }
    
    @Test
    public void testUpdateTaskStatus() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Task", "Description", futureDate);
        UUID taskId = createdTask.getId();
        
        Task updatedTask = taskService.updateTaskStatus(taskId, Status.COMPLETED);
        
        assertNotNull("Updated task should not be null", updatedTask);
        assertEquals("Status should be updated", Status.COMPLETED, updatedTask.getStatus());
    }
    
    @Test
    public void testUpdateTaskPriority() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Task", "Description", futureDate);
        UUID taskId = createdTask.getId();
        
        Task updatedTask = taskService.updateTaskPriority(taskId, Priority.URGENT);
        
        assertNotNull("Updated task should not be null", updatedTask);
        assertEquals("Priority should be updated", Priority.URGENT, updatedTask.getPriority());
    }
    
    @Test
    public void testUpdateTaskCategory() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Task", "Description", futureDate);
        UUID taskId = createdTask.getId();
        
        Task updatedTask = taskService.updateTaskCategory(taskId, "New Category");
        
        assertNotNull("Updated task should not be null", updatedTask);
        assertEquals("Category should be updated", "New Category", updatedTask.getCategory());
    }
    
    @Test
    public void testUpdateNonExistentTaskSpecificFields() {
        UUID nonExistentId = UUID.randomUUID();
        
        assertNull("Status update should return null", taskService.updateTaskStatus(nonExistentId, Status.COMPLETED));
        assertNull("Priority update should return null", taskService.updateTaskPriority(nonExistentId, Priority.URGENT));
        assertNull("Category update should return null", taskService.updateTaskCategory(nonExistentId, "Category"));
    }
    
    // ============= DELETE TESTS =============
    
    @Test
    public void testDeleteTask() {
        // Create a task first
        String futureDate = LocalDate.now().plusDays(5).toString();
        Task createdTask = taskService.createTask("Task to Delete", "Description", futureDate);
        UUID taskId = createdTask.getId();
        
        // Verify task exists
        assertNotNull("Task should exist before deletion", taskService.getTaskById(taskId));
        
        // Delete the task
        boolean deleted = taskService.deleteTask(taskId);
        
        assertTrue("Delete should return true", deleted);
        assertNull("Task should not exist after deletion", taskService.getTaskById(taskId));
    }
    
    @Test
    public void testDeleteNonExistentTask() {
        UUID nonExistentId = UUID.randomUUID();
        
        boolean deleted = taskService.deleteTask(nonExistentId);
        
        assertFalse("Delete should return false for non-existent task", deleted);
    }
    
    @Test
    public void testDeleteTaskWithNullId() {
        boolean deleted = taskService.deleteTask(null);
        
        assertFalse("Delete should return false for null ID", deleted);
    }
    
    // ============= FILTERING TESTS =============
    
    @Test
    public void testGetTasksByCategory() {
        // Create tasks with different categories
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Work Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Personal Task", "Description", futureDate, "Personal", Priority.MEDIUM, Status.PENDING);
        taskService.createTask("Work Task 2", "Description", futureDate, "Work", Priority.LOW, Status.PENDING);
        
        List<Task> workTasks = taskService.getTasksByCategory("Work");
        List<Task> personalTasks = taskService.getTasksByCategory("Personal");
        List<Task> nonExistentTasks = taskService.getTasksByCategory("NonExistent");
        
        assertEquals("Should have 2 work tasks", 2, workTasks.size());
        assertEquals("Should have 1 personal task", 1, personalTasks.size());
        assertEquals("Should have 0 non-existent category tasks", 0, nonExistentTasks.size());
        
        // Test case insensitive filtering
        List<Task> workTasksLowerCase = taskService.getTasksByCategory("work");
        assertEquals("Case insensitive filtering should work", 2, workTasksLowerCase.size());
    }
    
    @Test
    public void testGetTasksByCategoryWithNull() {
        List<Task> result = taskService.getTasksByCategory(null);
        assertEquals("Null category should return empty list", 0, result.size());
    }
    
    @Test
    public void testGetTasksByPriority() {
        // Create tasks with different priorities
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("High Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Medium Task", "Description", futureDate, "Work", Priority.MEDIUM, Status.PENDING);
        taskService.createTask("High Task 2", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        
        List<Task> highTasks = taskService.getTasksByPriority(Priority.HIGH);
        List<Task> mediumTasks = taskService.getTasksByPriority(Priority.MEDIUM);
        List<Task> lowTasks = taskService.getTasksByPriority(Priority.LOW);
        
        assertEquals("Should have 2 high priority tasks", 2, highTasks.size());
        assertEquals("Should have 1 medium priority task", 1, mediumTasks.size());
        assertEquals("Should have 0 low priority tasks", 0, lowTasks.size());
    }
    
    @Test
    public void testGetTasksByPriorityWithNull() {
        List<Task> result = taskService.getTasksByPriority(null);
        assertEquals("Null priority should return empty list", 0, result.size());
    }
    
    @Test
    public void testGetTasksByStatus() {
        // Create tasks with different statuses
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Pending Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("In Progress Task", "Description", futureDate, "Work", Priority.MEDIUM, Status.IN_PROGRESS);
        taskService.createTask("Pending Task 2", "Description", futureDate, "Work", Priority.LOW, Status.PENDING);
        
        List<Task> pendingTasks = taskService.getTasksByStatus(Status.PENDING);
        List<Task> inProgressTasks = taskService.getTasksByStatus(Status.IN_PROGRESS);
        List<Task> completedTasks = taskService.getTasksByStatus(Status.COMPLETED);
        
        assertEquals("Should have 2 pending tasks", 2, pendingTasks.size());
        assertEquals("Should have 1 in progress task", 1, inProgressTasks.size());
        assertEquals("Should have 0 completed tasks", 0, completedTasks.size());
    }
    
    @Test
    public void testGetTasksByStatusWithNull() {
        List<Task> result = taskService.getTasksByStatus(null);
        assertEquals("Null status should return empty list", 0, result.size());
    }
    
    // ============= GROUPING TESTS =============
    
    @Test
    public void testGetTasksGroupedByCategory() {
        // Create tasks with different categories
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Work Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Personal Task", "Description", futureDate, "Personal", Priority.MEDIUM, Status.PENDING);
        taskService.createTask("Work Task 2", "Description", futureDate, "Work", Priority.LOW, Status.PENDING);
        taskService.createTask("Study Task", "Description", futureDate, "Study", Priority.MEDIUM, Status.PENDING);
        
        Map<String, List<Task>> groupedTasks = taskService.getTasksGroupedByCategory();
        
        assertEquals("Should have 3 categories", 3, groupedTasks.size());
        assertEquals("Work category should have 2 tasks", 2, groupedTasks.get("Work").size());
        assertEquals("Personal category should have 1 task", 1, groupedTasks.get("Personal").size());
        assertEquals("Study category should have 1 task", 1, groupedTasks.get("Study").size());
    }
    
    @Test
    public void testGetTasksGroupedByPriority() {
        // Create tasks with different priorities
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("High Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Medium Task", "Description", futureDate, "Work", Priority.MEDIUM, Status.PENDING);
        taskService.createTask("High Task 2", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Low Task", "Description", futureDate, "Work", Priority.LOW, Status.PENDING);
        
        Map<Priority, List<Task>> groupedTasks = taskService.getTasksGroupedByPriority();
        
        assertEquals("Should have 3 priority levels", 3, groupedTasks.size());
        assertEquals("High priority should have 2 tasks", 2, groupedTasks.get(Priority.HIGH).size());
        assertEquals("Medium priority should have 1 task", 1, groupedTasks.get(Priority.MEDIUM).size());
        assertEquals("Low priority should have 1 task", 1, groupedTasks.get(Priority.LOW).size());
    }
    
    @Test
    public void testGetTasksGroupedByStatus() {
        // Create tasks with different statuses
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Pending Task 1", "Description", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("In Progress Task", "Description", futureDate, "Work", Priority.MEDIUM, Status.IN_PROGRESS);
        taskService.createTask("Pending Task 2", "Description", futureDate, "Work", Priority.LOW, Status.PENDING);
        taskService.createTask("Completed Task", "Description", futureDate, "Work", Priority.MEDIUM, Status.COMPLETED);
        
        Map<Status, List<Task>> groupedTasks = taskService.getTasksGroupedByStatus();
        
        assertEquals("Should have 3 status types", 3, groupedTasks.size());
        assertEquals("Pending status should have 2 tasks", 2, groupedTasks.get(Status.PENDING).size());
        assertEquals("In Progress status should have 1 task", 1, groupedTasks.get(Status.IN_PROGRESS).size());
        assertEquals("Completed status should have 1 task", 1, groupedTasks.get(Status.COMPLETED).size());
    }
    
    // ============= BACKUP AND REPOSITORY TESTS =============
    
    @Test
    public void testCreateBackup() {
        // Add a task first
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Test Task", "Description", futureDate);
        
        boolean result = taskService.createBackup(".backup");
        
        assertTrue("Backup should be successful", result);
        
        // Verify backup file was created
        File backupFile = new File(testFilePath + ".backup");
        assertTrue("Backup file should exist", backupFile.exists());
    }
    
    @Test
    public void testCreateBackupWithEmptyService() {
        // No tasks added
        boolean result = taskService.createBackup(".backup");
        
        assertFalse("Backup should fail when no file exists", result);
    }
    
    @Test
    public void testGetRepositoryInfo() {
        String info = taskService.getRepositoryInfo();
        
        assertTrue("Info should contain file path", info.contains(testFilePath));
        assertTrue("Info should contain exists status", info.contains("exists:"));
        assertTrue("Info should contain file size", info.contains("size:"));
    }
    
    // ============= EDGE CASES AND INTEGRATION TESTS =============
    
    @Test
    public void testMultipleOperationsOnSameTask() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        
        // Create task
        Task task = taskService.createTask("Original Task", "Description", futureDate);
        UUID taskId = task.getId();
        
        // Update status
        taskService.updateTaskStatus(taskId, Status.IN_PROGRESS);
        
        // Update priority
        taskService.updateTaskPriority(taskId, Priority.URGENT);
        
        // Update category
        taskService.updateTaskCategory(taskId, "Critical");
        
        // Get final task state
        Task finalTask = taskService.getTaskById(taskId);
        
        assertEquals("Status should be updated", Status.IN_PROGRESS, finalTask.getStatus());
        assertEquals("Priority should be updated", Priority.URGENT, finalTask.getPriority());
        assertEquals("Category should be updated", "Critical", finalTask.getCategory());
        assertEquals("Original title should remain", "Original Task", finalTask.getTitle());
    }
    
    @Test
    public void testEmptyRepositoryOperations() {
        // Test operations on empty repository
        assertEquals("Empty repository should have no tasks", 0, taskService.getAllTasks().size());
        
        UUID randomId = UUID.randomUUID();
        assertNull("Non-existent task retrieval should return null", taskService.getTaskById(randomId));
        assertFalse("Deleting non-existent task should return false", taskService.deleteTask(randomId));
        
        assertEquals("Filter by category should return empty", 0, taskService.getTasksByCategory("Work").size());
        assertEquals("Filter by priority should return empty", 0, taskService.getTasksByPriority(Priority.HIGH).size());
        assertEquals("Filter by status should return empty", 0, taskService.getTasksByStatus(Status.PENDING).size());
        
        assertTrue("Group by category should be empty", taskService.getTasksGroupedByCategory().isEmpty());
        assertTrue("Group by priority should be empty", taskService.getTasksGroupedByPriority().isEmpty());
        assertTrue("Group by status should be empty", taskService.getTasksGroupedByStatus().isEmpty());
    }
    
    @Test
    public void testPersistenceAcrossServiceInstances() {
        // Create tasks in first service instance
        String futureDate = LocalDate.now().plusDays(5).toString();
        taskService.createTask("Persistent Task 1", "Description 1", futureDate, "Work", Priority.HIGH, Status.PENDING);
        taskService.createTask("Persistent Task 2", "Description 2", futureDate, "Personal", Priority.MEDIUM, Status.IN_PROGRESS);
        
        // Create new service instance with same repository
        TaskService newService = new TaskService(new TaskRepository(testFilePath));
        
        List<Task> loadedTasks = newService.getAllTasks();
        assertEquals("Should load 2 tasks from file", 2, loadedTasks.size());
        
        // Verify task details
        boolean found1 = false, found2 = false;
        for (Task task : loadedTasks) {
            if ("Persistent Task 1".equals(task.getTitle())) {
                assertEquals("Priority should be preserved", Priority.HIGH, task.getPriority());
                assertEquals("Category should be preserved", "Work", task.getCategory());
                found1 = true;
            } else if ("Persistent Task 2".equals(task.getTitle())) {
                assertEquals("Status should be preserved", Status.IN_PROGRESS, task.getStatus());
                assertEquals("Category should be preserved", "Personal", task.getCategory());
                found2 = true;
            }
        }
        
        assertTrue("First task should be found", found1);
        assertTrue("Second task should be found", found2);
    }
    
    @Test
    public void testValidationEdgeCases() {
        String futureDate = LocalDate.now().plusDays(5).toString();
        
        // Test with null description (should be allowed)
        Task taskWithNullDesc = taskService.createTask("Task", null, futureDate);
        assertNull("Description should be null", taskWithNullDesc.getDescription());
        
        // Test with whitespace-only title (should fail)
        try {
            taskService.createTask("   ", "Description", futureDate);
            fail("Should throw exception for whitespace-only title");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception message should mention title", e.getMessage().contains("Title"));
        }
        
        // Test with very long title (should fail)
        String longTitle = "A".repeat(101); // Exceeds MAX_TITLE_LENGTH
        try {
            taskService.createTask(longTitle, "Description", futureDate);
            fail("Should throw exception for overly long title");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception message should mention title length", e.getMessage().contains("Title"));
        }
        
        // Test with very long description (should fail)
        String longDescription = "A".repeat(501); // Exceeds MAX_DESCRIPTION_LENGTH
        try {
            taskService.createTask("Task", longDescription, futureDate);
            fail("Should throw exception for overly long description");
        } catch (IllegalArgumentException e) {
            assertTrue("Exception message should mention description length", e.getMessage().contains("Description"));
        }
    }
}