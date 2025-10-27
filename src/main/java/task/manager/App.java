package task.manager;

import task.manager.service.TaskService;
import task.manager.model.Task;
import task.manager.model.Priority;
import task.manager.model.Status;
import java.util.List;
import java.util.Map;

/**
 * Task Manager Application
 * Demonstrates enhanced CRUD operations, filtering, and grouping using TaskService
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("=== Enhanced Task Manager Application ===\n");
        
        // Create TaskService instance
        TaskService taskService = new TaskService();
        
        // Demonstrate CREATE operations with enhanced features
        System.out.println("1. Creating Tasks with Categories, Priorities, and Statuses:");
        System.out.println("-----------------------------------------------------------");
        
        // Create tasks with different attributes
        Task task1 = taskService.createTask("Complete Java Project", 
            "Finish the task manager application", "2025-11-01", 
            "Development", Priority.HIGH, Status.IN_PROGRESS);
        System.out.println("Created: " + task1.getTitle() + " [" + task1.getCategory() + 
            ", " + task1.getPriority() + ", " + task1.getStatus() + "]");
        
        Task task2 = taskService.createTask("Review Code", 
            "Review and refactor existing codebase", "2025-10-30", 
            "Development", Priority.MEDIUM, Status.PENDING);
        System.out.println("Created: " + task2.getTitle() + " [" + task2.getCategory() + 
            ", " + task2.getPriority() + ", " + task2.getStatus() + "]");
        
        Task task3 = taskService.createTask("Write Documentation", 
            "Create user manual and API docs", "2025-11-05", 
            "Documentation", Priority.LOW, Status.PENDING);
        System.out.println("Created: " + task3.getTitle() + " [" + task3.getCategory() + 
            ", " + task3.getPriority() + ", " + task3.getStatus() + "]");
        
        Task task4 = taskService.createTask("Team Meeting", 
            "Weekly standup meeting", "2025-10-28", 
            "Management", Priority.MEDIUM, Status.PENDING);
        System.out.println("Created: " + task4.getTitle() + " [" + task4.getCategory() + 
            ", " + task4.getPriority() + ", " + task4.getStatus() + "]");
        
        Task task5 = taskService.createTask("Fix Critical Bug", 
            "Resolve production issue ASAP", "2025-10-27", 
            "Development", Priority.URGENT, Status.IN_PROGRESS);
        System.out.println("Created: " + task5.getTitle() + " [" + task5.getCategory() + 
            ", " + task5.getPriority() + ", " + task5.getStatus() + "]");
        
        // Create task using backward compatible constructor
        Task task6 = taskService.createTask("Buy Office Supplies", 
            "Get pens, paper, and coffee", "2025-10-29");
        System.out.println("Created (with defaults): " + task6.getTitle() + " [" + task6.getCategory() + 
            ", " + task6.getPriority() + ", " + task6.getStatus() + "]");
        
        System.out.println("\nTotal tasks created: " + taskService.getAllTasks().size());
        System.out.println();
        
        // Demonstrate FILTERING capabilities
        System.out.println("2. Filtering Tasks:");
        System.out.println("-------------------");
        
        // Filter by category
        System.out.println("Development Tasks:");
        List<Task> devTasks = taskService.getTasksByCategory("Development");
        for (Task task : devTasks) {
            System.out.println("  - " + task.getTitle() + " (" + task.getPriority() + ", " + task.getStatus() + ")");
        }
        
        // Filter by priority
        System.out.println("\nHigh/Urgent Priority Tasks:");
        List<Task> highPriorityTasks = taskService.getTasksByPriority(Priority.HIGH);
        List<Task> urgentTasks = taskService.getTasksByPriority(Priority.URGENT);
        for (Task task : highPriorityTasks) {
            System.out.println("  - " + task.getTitle() + " [HIGH] (" + task.getCategory() + ")");
        }
        for (Task task : urgentTasks) {
            System.out.println("  - " + task.getTitle() + " [URGENT] (" + task.getCategory() + ")");
        }
        
        // Filter by status
        System.out.println("\nIn Progress Tasks:");
        List<Task> inProgressTasks = taskService.getTasksByStatus(Status.IN_PROGRESS);
        for (Task task : inProgressTasks) {
            System.out.println("  - " + task.getTitle() + " (" + task.getPriority() + ", " + task.getCategory() + ")");
        }
        
        System.out.println();
        
        // Demonstrate GROUPING capabilities
        System.out.println("3. Grouping Tasks:");
        System.out.println("------------------");
        
        // Group by category
        System.out.println("Tasks Grouped by Category:");
        Map<String, List<Task>> tasksByCategory = taskService.getTasksGroupedByCategory();
        for (Map.Entry<String, List<Task>> entry : tasksByCategory.entrySet()) {
            System.out.println("  " + entry.getKey() + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("    - " + task.getTitle());
            }
        }
        
        // Group by priority
        System.out.println("\nTasks Grouped by Priority:");
        Map<Priority, List<Task>> tasksByPriority = taskService.getTasksGroupedByPriority();
        for (Map.Entry<Priority, List<Task>> entry : tasksByPriority.entrySet()) {
            System.out.println("  " + entry.getKey() + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("    - " + task.getTitle() + " [" + task.getCategory() + "]");
            }
        }
        
        // Group by status
        System.out.println("\nTasks Grouped by Status:");
        Map<Status, List<Task>> tasksByStatus = taskService.getTasksGroupedByStatus();
        for (Map.Entry<Status, List<Task>> entry : tasksByStatus.entrySet()) {
            System.out.println("  " + entry.getKey() + " (" + entry.getValue().size() + " tasks):");
            for (Task task : entry.getValue()) {
                System.out.println("    - " + task.getTitle() + " [" + task.getPriority() + "]");
            }
        }
        
        System.out.println();
        
        // Demonstrate SPECIFIC UPDATE operations
        System.out.println("4. Specific Field Updates:");
        System.out.println("---------------------------");
        
        // Update task status
        System.out.println("Completing task: " + task2.getTitle());
        taskService.updateTaskStatus(task2.getId(), Status.COMPLETED);
        System.out.println("Status updated to: " + task2.getStatus());
        
        // Update task priority
        System.out.println("\nChanging priority of: " + task3.getTitle());
        System.out.println("Priority before: " + task3.getPriority());
        taskService.updateTaskPriority(task3.getId(), Priority.HIGH);
        System.out.println("Priority after: " + task3.getPriority());
        
        // Update task category
        System.out.println("\nRecategorizing: " + task4.getTitle());
        System.out.println("Category before: " + task4.getCategory());
        taskService.updateTaskCategory(task4.getId(), "Communication");
        System.out.println("Category after: " + task4.getCategory());
        
        System.out.println();
        
        // Show final summary with updated groupings
        System.out.println("5. Final Status Summary:");
        System.out.println("------------------------");
        
        Map<Status, List<Task>> finalStatusGroups = taskService.getTasksGroupedByStatus();
        for (Map.Entry<Status, List<Task>> entry : finalStatusGroups.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().size() + " tasks");
            for (Task task : entry.getValue()) {
                System.out.println("  - " + task.getTitle() + " [" + task.getCategory() + 
                    ", " + task.getPriority() + "]");
            }
            System.out.println();
        }
        
        System.out.println("=== Enhanced Task Manager Demo Complete ===");
    }
}
