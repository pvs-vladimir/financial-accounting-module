package finance.services;

import java.util.ArrayList;
import java.util.List;

import finance.services.tasks.AutoUpdateTask;

public abstract class Service {
    private final ServiceType type;
    protected final List<AutoUpdateTask> tasks = new ArrayList<>();

    public Service(ServiceType type) {
        this.type = type;
    }

    public ServiceType getType() { return type; }
    public List<AutoUpdateTask> getAutoUpdateTasks() { return tasks; }

    public void addAutoUpdateTask(AutoUpdateTask task) {
        if (isSuitableTask(task) && !tasks.contains(task)) {
            tasks.add(task);
        }
    }

    public boolean hasAutoUpdateTask(AutoUpdateTask task) {
        return tasks.contains(task);
    }

    public void removeAutoUpdateTask(AutoUpdateTask task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
        }
    }

    public abstract void update(Manager manager);

    protected abstract boolean isSuitableTask(AutoUpdateTask task);
}
