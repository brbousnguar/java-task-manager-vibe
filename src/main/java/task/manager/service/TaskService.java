package task.manager.service;

import task.manager.model.Task;
import task.manager.model.Priority;
import task.manager.model.Status;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing Task operations
 * Provides CRUD (Create, Read, Update, Delete) functionality for tasks
 * Uses in-memory storage with a List
 */
public class TaskService {
    
    private final List<Task> tasks;
    
    public TaskService() {
        this.tasks = new ArrayList<>();
    }
    
    /**
     * Creates a new task and adds it to the storage
     * @param title the task title
     * @param description the task description
     * @param dueDate the task due date in YYYY-MM-DD format
     * @return the created task with generated UUID
     * @throws IllegalArgumentException if validation fails
     */
    public Task createTask(String title, String description, String dueDate) {
        Task task = new Task(title, description, dueDate);
        tasks.add(task);
        return task;
    }
    
    /**
     * Creates a new task with all fields and adds it to the storage
     * @param title the task title
     * @param description the task description
     * @param dueDate the task due date in YYYY-MM-DD format
     * @param category the task category
     * @param priority the task priority
     * @param status the task status
     * @return the created task with generated UUID
     * @throws IllegalArgumentException if validation fails
     */
    public Task createTask(String title, String description, String dueDate, String category, Priority priority, Status status) {
        Task task = new Task(title, description, dueDate, category, priority, status);
        tasks.add(task);
        return task;
    }
    
    /**
     * Retrieves a task by its ID
     * @param id the UUID of the task
     * @return the task if found, null otherwise
     */
    public Task getTaskById(UUID id) {
        if (id == null) {
            return null;
        }
        
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Retrieves all tasks
     * @return a new list containing all tasks (to prevent external modification)
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    /**
     * Updates an existing task
     * @param id the UUID of the task to update
     * @param title the new title (cannot be null)
     * @param description the new description (can be null)
     * @param dueDate the new due date in YYYY-MM-DD format
     * @return the updated task if found and updated successfully, null if task not found
     * @throws IllegalArgumentException if validation fails
     */
    public Task updateTask(UUID id, String title, String description, String dueDate) {
        Task task = getTaskById(id);
        if (task == null) {
            return null;
        }
        
        // Update fields (validation happens in setters)
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        
        return task;
    }
    
    /**
     * Updates an existing task with all fields
     * @param id the UUID of the task to update
     * @param title the new title (cannot be null)
     * @param description the new description (can be null)
     * @param dueDate the new due date in YYYY-MM-DD format
     * @param category the new category
     * @param priority the new priority
     * @param status the new status
     * @return the updated task if found and updated successfully, null if task not found
     * @throws IllegalArgumentException if validation fails
     */
    public Task updateTask(UUID id, String title, String description, String dueDate, String category, Priority priority, Status status) {
        Task task = getTaskById(id);
        if (task == null) {
            return null;
        }
        
        // Update fields (validation happens in setters)
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setCategory(category);
        task.setPriority(priority);
        task.setStatus(status);
        
        return task;
    }
    
    /**
     * Deletes a task by its ID
     * @param id the UUID of the task to delete
     * @return true if task was found and deleted, false if task not found
     */
    public boolean deleteTask(UUID id) {
        if (id == null) {
            return false;
        }
        
        return tasks.removeIf(task -> task.getId().equals(id));
    }
    
    // FILTERING METHODS
    
    /**
     * Filters tasks by category
     * @param category the category to filter by
     * @return list of tasks matching the category
     */
    public List<Task> getTasksByCategory(String category) {
        if (category == null) {
            return new ArrayList<>();
        }
        
        return tasks.stream()
                .filter(task -> category.equalsIgnoreCase(task.getCategory()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filters tasks by priority
     * @param priority the priority to filter by
     * @return list of tasks matching the priority
     */
    public List<Task> getTasksByPriority(Priority priority) {
        if (priority == null) {
            return new ArrayList<>();
        }
        
        return tasks.stream()
                .filter(task -> priority.equals(task.getPriority()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filters tasks by status
     * @param status the status to filter by
     * @return list of tasks matching the status
     */
    public List<Task> getTasksByStatus(Status status) {
        if (status == null) {
            return new ArrayList<>();
        }
        
        return tasks.stream()
                .filter(task -> status.equals(task.getStatus()))
                .collect(Collectors.toList());
    }
    
    // GROUPING METHODS
    
    /**
     * Groups tasks by category
     * @return map with category as key and list of tasks as value
     */
    public Map<String, List<Task>> getTasksGroupedByCategory() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getCategory));
    }
    
    /**
     * Groups tasks by priority
     * @return map with priority as key and list of tasks as value
     */
    public Map<Priority, List<Task>> getTasksGroupedByPriority() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority));
    }
    
    /**
     * Groups tasks by status
     * @return map with status as key and list of tasks as value
     */
    public Map<Status, List<Task>> getTasksGroupedByStatus() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));
    }
    
    // SPECIFIC UPDATE METHODS
    
    /**
     * Updates only the status of a task
     * @param id the UUID of the task to update
     * @param status the new status
     * @return the updated task if found and updated successfully, null if task not found
     * @throws IllegalArgumentException if validation fails
     */
    public Task updateTaskStatus(UUID id, Status status) {
        Task task = getTaskById(id);
        if (task == null) {
            return null;
        }
        
        task.setStatus(status);
        return task;
    }
    
    /**
     * Updates only the priority of a task
     * @param id the UUID of the task to update
     * @param priority the new priority
     * @return the updated task if found and updated successfully, null if task not found
     * @throws IllegalArgumentException if validation fails
     */
    public Task updateTaskPriority(UUID id, Priority priority) {
        Task task = getTaskById(id);
        if (task == null) {
            return null;
        }
        
        task.setPriority(priority);
        return task;
    }
    
    /**
     * Updates only the category of a task
     * @param id the UUID of the task to update
     * @param category the new category
     * @return the updated task if found and updated successfully, null if task not found
     * @throws IllegalArgumentException if validation fails
     */
    public Task updateTaskCategory(UUID id, String category) {
        Task task = getTaskById(id);
        if (task == null) {
            return null;
        }
        
        task.setCategory(category);
        return task;
    }
    
}