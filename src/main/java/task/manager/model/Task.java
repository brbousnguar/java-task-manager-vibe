package task.manager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class Task {
    // Update the Task class with title, description
    // and due date fields. Include proper encapsulation
    // getters and setters, a constructor, and basic validation.

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_CATEGORY_LENGTH = 50;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UUID id;
    private String title;
    private String description;
    private String dueDate; // Format: YYYY-MM-DD
    private String category;
    private Priority priority;
    private Status status;

    public Task(String title, String description, String dueDate, String category, Priority priority, Status status) {
        this.id = UUID.randomUUID();
        setTitle(title);
        setDescription(description);
        setDueDate(dueDate);
        setCategory(category);
        setPriority(priority);
        setStatus(status);
    }

    // Constructor with default values for backward compatibility
    public Task(String title, String description, String dueDate) {
        this(title, description, dueDate, "General", Priority.MEDIUM, Status.PENDING);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Title cannot exceed " + MAX_TITLE_LENGTH + " characters");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            if (description.trim().length() > MAX_DESCRIPTION_LENGTH) {
                throw new IllegalArgumentException("Description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
            }
            this.description = description.trim();
        } else {
            this.description = null;
        }
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        if (dueDate == null || dueDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Due date cannot be null or empty");
        }
        
        // Validate date format and that it's a valid date
        try {
            LocalDate parsedDate = LocalDate.parse(dueDate.trim(), DATE_FORMATTER);
            
            // Check if the date is not in the past
            if (parsedDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Due date cannot be in the past");
            }
            
            this.dueDate = dueDate.trim();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Due date must be in the format YYYY-MM-DD and be a valid date");
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (category.trim().length() > MAX_CATEGORY_LENGTH) {
            throw new IllegalArgumentException("Category cannot exceed " + MAX_CATEGORY_LENGTH + " characters");
        }
        this.category = category.trim();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }
}
