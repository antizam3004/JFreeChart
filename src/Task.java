
import java.time.LocalDate;
import java.util.Date;

public class Task {
    private String description;
    private boolean completed;
    private LocalDate date;

    public Task(String description, boolean completed, LocalDate date) {
        this.description = description;
        this.completed = completed;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


}
