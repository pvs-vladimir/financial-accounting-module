package finance.services.tasks;

import finance.services.ServiceType;

public interface AutoUpdateTask {
    ServiceType getOwner();
    String getDescription();
}
