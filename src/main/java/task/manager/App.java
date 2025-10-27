package task.manager;

import task.manager.view.TaskManagerCLI;

/**
 * Task Manager Application
 * Interactive command-line interface for task management
 */
public class App 
{
    public static void main( String[] args )
    {
        // Launch the interactive CLI
        TaskManagerCLI cli = new TaskManagerCLI();
        cli.start();
    }
}
