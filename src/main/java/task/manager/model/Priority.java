package task.manager.model;

/**
 * Enum representing task priority levels
 */
public enum Priority {
    LOW("Low"),
    MEDIUM("Medium"), 
    HIGH("High"),
    URGENT("Urgent");
    
    private final String displayName;
    
    Priority(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}