package task.manager.view;

import task.manager.service.TaskService;
import task.manager.model.Task;
import task.manager.model.Priority;
import task.manager.model.Status;

import java.util.*;

/**
 * Interactive Command-Line Interface for Task Manager
 * Provides user-friendly menu system for task management operations
 */
public class TaskManagerCLI {
    
    private final TaskService taskService;
    private final Scanner scanner;
    
    public TaskManagerCLI() {
        this.taskService = new TaskService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Starts the interactive CLI application
     */
    public void start() {
        System.out.println("=".repeat(50));
        System.out.println("     WELCOME TO TASK MANAGER CLI");
        System.out.println("=".repeat(50));
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getValidMenuChoice(1, 7);
            
            switch (choice) {
                case 1 -> createTask();
                case 2 -> listAllTasks();
                case 3 -> updateTask();
                case 4 -> deleteTask();
                case 5 -> filterTasks();
                case 6 -> groupTasks();
                case 7 -> {
                    System.out.println("\nThank you for using Task Manager CLI!");
                    running = false;
                }
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Displays the main menu options
     */
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("         MAIN MENU");
        System.out.println("=".repeat(30));
        System.out.println("1. Create New Task");
        System.out.println("2. List All Tasks");
        System.out.println("3. Update Task");
        System.out.println("4. Delete Task");
        System.out.println("5. Filter Tasks");
        System.out.println("6. Group Tasks");
        System.out.println("7. Exit");
        System.out.println("-".repeat(30));
        System.out.print("Enter your choice (1-7): ");
    }
    
    /**
     * Gets a valid menu choice from user input
     */
    private int getValidMenuChoice(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print("Invalid choice. Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }
    
    /**
     * Creates a new task with user input
     */
    private void createTask() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("       CREATE NEW TASK");
        System.out.println("-".repeat(30));
        
        try {
            // Get task details from user
            String title = getValidStringInput("Enter task title: ", 1, 100);
            String description = getOptionalStringInput("Enter task description (optional): ", 500);
            String dueDate = getValidDateInput("Enter due date (YYYY-MM-DD): ");
            String category = getValidStringInput("Enter task category: ", 1, 50);
            Priority priority = getValidPriorityInput();
            Status status = getValidStatusInput();
            
            // Create the task
            Task task = taskService.createTask(title, description, dueDate, category, priority, status);
            
            System.out.println("\n✅ Task created successfully!");
            displayTaskDetails(task);
            
        } catch (Exception e) {
            System.out.println("\n❌ Error creating task: " + e.getMessage());
        }
    }
    
    /**
     * Lists all tasks in the system
     */
    private void listAllTasks() {
        System.out.println("\n" + "-".repeat(40));
        System.out.println("           ALL TASKS");
        System.out.println("-".repeat(40));
        
        List<Task> tasks = taskService.getAllTasks();
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found. Create your first task!");
            return;
        }
        
        System.out.println("Total tasks: " + tasks.size() + "\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ". " + task.getTitle());
            System.out.println("   Category: " + task.getCategory() + " | Priority: " + getPriorityWithEmoji(task.getPriority()) + 
                             " | Status: " + getStatusWithEmoji(task.getStatus()));
            System.out.println("   Due: " + task.getDueDate());
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                System.out.println("   Description: " + task.getDescription());
            }
            System.out.println("   ID: " + task.getId());
            System.out.println();
        }
    }
    
    /**
     * Updates an existing task
     */
    private void updateTask() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("        UPDATE TASK");
        System.out.println("-".repeat(30));
        
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to update.");
            return;
        }
        
        // Display tasks with numbers
        System.out.println("Select a task to update:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getTitle() + 
                             " [" + getStatusWithEmoji(tasks.get(i).getStatus()) + "]");
        }
        
        System.out.print("\nEnter task number (1-" + tasks.size() + "): ");
        int taskIndex = getValidMenuChoice(1, tasks.size()) - 1;
        Task selectedTask = tasks.get(taskIndex);
        
        System.out.println("\nSelected: " + selectedTask.getTitle());
        System.out.println("1. Update all fields");
        System.out.println("2. Update status only");
        System.out.println("3. Update priority only");
        System.out.println("4. Update category only");
        System.out.print("Choose update type (1-4): ");
        
        int updateType = getValidMenuChoice(1, 4);
        
        try {
            switch (updateType) {
                case 1 -> updateAllFields(selectedTask);
                case 2 -> updateTaskStatus(selectedTask);
                case 3 -> updateTaskPriority(selectedTask);
                case 4 -> updateTaskCategory(selectedTask);
            }
        } catch (Exception e) {
            System.out.println("\n❌ Error updating task: " + e.getMessage());
        }
    }
    
    /**
     * Updates all fields of a task
     */
    private void updateAllFields(Task task) {
        String title = getValidStringInput("Enter new title [" + task.getTitle() + "]: ", 1, 100);
        String description = getOptionalStringInput("Enter new description [" + 
            (task.getDescription() != null ? task.getDescription() : "none") + "]: ", 500);
        String dueDate = getValidDateInput("Enter new due date [" + task.getDueDate() + "]: ");
        String category = getValidStringInput("Enter new category [" + task.getCategory() + "]: ", 1, 50);
        Priority priority = getValidPriorityInput("Select new priority [" + task.getPriority() + "]: ");
        Status status = getValidStatusInput("Select new status [" + task.getStatus() + "]: ");
        
        Task updatedTask = taskService.updateTask(task.getId(), title, description, dueDate, category, priority, status);
        
        if (updatedTask != null) {
            System.out.println("\n✅ Task updated successfully!");
            displayTaskDetails(updatedTask);
        } else {
            System.out.println("\n❌ Failed to update task.");
        }
    }
    
    /**
     * Updates only the status of a task
     */
    private void updateTaskStatus(Task task) {
        Status newStatus = getValidStatusInput("Select new status [" + task.getStatus() + "]: ");
        Task updatedTask = taskService.updateTaskStatus(task.getId(), newStatus);
        
        if (updatedTask != null) {
            System.out.println("\n✅ Task status updated to: " + newStatus);
        } else {
            System.out.println("\n❌ Failed to update task status.");
        }
    }
    
    /**
     * Updates only the priority of a task
     */
    private void updateTaskPriority(Task task) {
        Priority newPriority = getValidPriorityInput("Select new priority [" + task.getPriority() + "]: ");
        Task updatedTask = taskService.updateTaskPriority(task.getId(), newPriority);
        
        if (updatedTask != null) {
            System.out.println("\n✅ Task priority updated to: " + newPriority);
        } else {
            System.out.println("\n❌ Failed to update task priority.");
        }
    }
    
    /**
     * Updates only the category of a task
     */
    private void updateTaskCategory(Task task) {
        String newCategory = getValidStringInput("Enter new category [" + task.getCategory() + "]: ", 1, 50);
        Task updatedTask = taskService.updateTaskCategory(task.getId(), newCategory);
        
        if (updatedTask != null) {
            System.out.println("\n✅ Task category updated to: " + newCategory);
        } else {
            System.out.println("\n❌ Failed to update task category.");
        }
    }
    
    /**
     * Deletes a task
     */
    private void deleteTask() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("        DELETE TASK");
        System.out.println("-".repeat(30));
        
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to delete.");
            return;
        }
        
        // Display tasks with numbers
        System.out.println("Select a task to delete:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getTitle() + 
                             " [" + getStatusWithEmoji(tasks.get(i).getStatus()) + "]");
        }
        
        System.out.print("\nEnter task number (1-" + tasks.size() + "): ");
        int taskIndex = getValidMenuChoice(1, tasks.size()) - 1;
        Task selectedTask = tasks.get(taskIndex);
        
        System.out.println("\nSelected task: " + selectedTask.getTitle());
        System.out.print("Are you sure you want to delete this task? (y/N): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("y") || confirmation.equals("yes")) {
            boolean deleted = taskService.deleteTask(selectedTask.getId());
            if (deleted) {
                System.out.println("\n✅ Task deleted successfully!");
            } else {
                System.out.println("\n❌ Failed to delete task.");
            }
        } else {
            System.out.println("\nTask deletion cancelled.");
        }
    }
    
    /**
     * Filters tasks based on user criteria
     */
    private void filterTasks() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("       FILTER TASKS");
        System.out.println("-".repeat(30));
        
        System.out.println("Filter by:");
        System.out.println("1. Category");
        System.out.println("2. Priority");
        System.out.println("3. Status");
        System.out.print("Choose filter type (1-3): ");
        
        int filterType = getValidMenuChoice(1, 3);
        List<Task> filteredTasks = new ArrayList<>();
        
        switch (filterType) {
            case 1 -> {
                String category = getValidStringInput("Enter category to filter by: ", 1, 50);
                filteredTasks = taskService.getTasksByCategory(category);
                System.out.println("\nTasks in category '" + category + "':");
            }
            case 2 -> {
                Priority priority = getValidPriorityInput("Select priority to filter by: ");
                filteredTasks = taskService.getTasksByPriority(priority);
                System.out.println("\nTasks with priority '" + priority + "':");
            }
            case 3 -> {
                Status status = getValidStatusInput("Select status to filter by: ");
                filteredTasks = taskService.getTasksByStatus(status);
                System.out.println("\nTasks with status '" + status + "':");
            }
        }
        
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found matching the filter criteria.");
        } else {
            System.out.println("Found " + filteredTasks.size() + " task(s):\n");
            for (int i = 0; i < filteredTasks.size(); i++) {
                Task task = filteredTasks.get(i);
                System.out.println((i + 1) + ". " + task.getTitle() + 
                                 " [" + task.getCategory() + ", " + getPriorityWithEmoji(task.getPriority()) + ", " + getStatusWithEmoji(task.getStatus()) + "]");
            }
        }
    }
    
    /**
     * Groups and displays tasks
     */
    private void groupTasks() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("        GROUP TASKS");
        System.out.println("-".repeat(30));
        
        System.out.println("Group by:");
        System.out.println("1. Category");
        System.out.println("2. Priority");
        System.out.println("3. Status");
        System.out.print("Choose grouping type (1-3): ");
        
        int groupType = getValidMenuChoice(1, 3);
        
        switch (groupType) {
            case 1 -> displayTasksByCategory();
            case 2 -> displayTasksByPriority();
            case 3 -> displayTasksByStatus();
        }
    }
    
    /**
     * Displays tasks grouped by category
     */
    private void displayTasksByCategory() {
        Map<String, List<Task>> groupedTasks = taskService.getTasksGroupedByCategory();
        System.out.println("\nTasks grouped by Category:");
        displayGroupedTasks(groupedTasks);
    }
    
    /**
     * Displays tasks grouped by priority
     */
    private void displayTasksByPriority() {
        Map<Priority, List<Task>> groupedTasks = taskService.getTasksGroupedByPriority();
        System.out.println("\nTasks grouped by Priority:");
        for (Map.Entry<Priority, List<Task>> entry : groupedTasks.entrySet()) {
            System.out.println("\n" + getPriorityWithEmoji(entry.getKey()) + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("  - " + task.getTitle() + " [" + getStatusWithEmoji(task.getStatus()) + "]");
            }
        }
    }
    
    /**
     * Displays tasks grouped by status
     */
    private void displayTasksByStatus() {
        Map<Status, List<Task>> groupedTasks = taskService.getTasksGroupedByStatus();
        System.out.println("\nTasks grouped by Status:");
        for (Map.Entry<Status, List<Task>> entry : groupedTasks.entrySet()) {
            System.out.println("\n" + getStatusWithEmoji(entry.getKey()) + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("  - " + task.getTitle() + " [" + getPriorityWithEmoji(task.getPriority()) + "]");
            }
        }
    }
    
    /**
     * Helper method to display grouped tasks (for String keys)
     */
    private void displayGroupedTasks(Map<String, List<Task>> groupedTasks) {
        for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
            System.out.println("\n" + entry.getKey() + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("  - " + task.getTitle() + " [" + getPriorityWithEmoji(task.getPriority()) + ", " + getStatusWithEmoji(task.getStatus()) + "]");
            }
        }
    }
    
    /**
     * Displays detailed information about a task
     */
    private void displayTaskDetails(Task task) {
        System.out.println("Title: " + task.getTitle());
        System.out.println("Description: " + (task.getDescription() != null ? task.getDescription() : "None"));
        System.out.println("Due Date: " + task.getDueDate());
        System.out.println("Category: " + task.getCategory());
        System.out.println("Priority: " + getPriorityWithEmoji(task.getPriority()));
        System.out.println("Status: " + getStatusWithEmoji(task.getStatus()));
        System.out.println("ID: " + task.getId());
    }
    
    // INPUT VALIDATION METHODS
    
    /**
     * Gets valid string input with length validation
     */
    private String getValidStringInput(String prompt, int minLength, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.length() >= minLength && input.length() <= maxLength) {
                return input;
            }
            
            System.out.println("Input must be between " + minLength + " and " + maxLength + " characters.");
        }
    }
    
    /**
     * Gets optional string input with length validation
     */
    private String getOptionalStringInput(String prompt, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty() || input.length() <= maxLength) {
                return input.isEmpty() ? null : input;
            }
            
            System.out.println("Input cannot exceed " + maxLength + " characters.");
        }
    }
    
    /**
     * Gets valid date input in YYYY-MM-DD format
     */
    private String getValidDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                try {
                    // Try to create a task with this date to validate it
                    new Task("temp", "temp", input);
                    return input;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date: " + e.getMessage());
                }
            } else {
                System.out.println("Date must be in YYYY-MM-DD format.");
            }
        }
    }
    
    /**
     * Gets valid priority input
     */
    private Priority getValidPriorityInput() {
        return getValidPriorityInput("Select priority: ");
    }
    
    /**
     * Gets valid priority input with custom prompt
     */
    private Priority getValidPriorityInput(String prompt) {
        while (true) {
            System.out.println("\nAvailable priorities:");
            Priority[] priorities = Priority.values();
            for (int i = 0; i < priorities.length; i++) {
                System.out.println((i + 1) + ". " + priorities[i]);
            }
            
            System.out.print(prompt + " (1-" + priorities.length + "): ");
            int choice = getValidMenuChoice(1, priorities.length);
            return priorities[choice - 1];
        }
    }
    
    /**
     * Gets valid status input
     */
    private Status getValidStatusInput() {
        return getValidStatusInput("Select status: ");
    }
    
    /**
     * Gets valid status input with custom prompt
     */
    private Status getValidStatusInput(String prompt) {
        while (true) {
            System.out.println("\nAvailable statuses:");
            Status[] statuses = Status.values();
            for (int i = 0; i < statuses.length; i++) {
                System.out.println((i + 1) + ". " + statuses[i]);
            }
            
            System.out.print(prompt + " (1-" + statuses.length + "): ");
            int choice = getValidMenuChoice(1, statuses.length);
            return statuses[choice - 1];
        }
    }
    
    /**
     * Returns status with appropriate emoji
     */
    private String getStatusWithEmoji(Status status) {
        return switch (status) {
            case PENDING -> "⏳ " + status;
            case IN_PROGRESS -> "⚡ " + status;
            case COMPLETED -> "✅ " + status;
        };
    }
    
    /**
     * Returns priority with appropriate emoji
     */
    private String getPriorityWithEmoji(Priority priority) {
        return switch (priority) {
            case LOW -> "🟢 " + priority;
            case MEDIUM -> "🟡 " + priority;
            case HIGH -> "🟠 " + priority;
            case URGENT -> "🔴 " + priority;
        };
    }
}