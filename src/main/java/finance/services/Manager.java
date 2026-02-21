package finance.services;

import java.util.HashMap;
import java.util.Map;

import finance.domain.BankAccount;
import finance.domain.Category;
import finance.domain.Operation;
import finance.services.repository.Repository;
import finance.services.repository.RepositoryManipulator;

public class Manager {
    private final Map<ServiceType, Service> services = new HashMap<>();
    private final RepositoryManipulator repositoryManipulator;
    private Action lastAction;
    private Integer changedItemId;

    public Manager(Repository repository, RepositoryManipulator manipulator) {
        this.repositoryManipulator = manipulator;
        this.lastAction = Action.NONE;
        this.changedItemId = null;
    }

    public Map<ServiceType, Service> getAllServices() { return services; }
    public Action getLastAction() { return lastAction; }
    public Integer getChangedItemId() { return changedItemId; }

    public void addService(Service service) {
        if ((service != null) && (!services.containsKey(service.getType()))) {
            services.put(service.getType(), service);
        }
    }

    public boolean isActiveService(ServiceType type) {
        return services.containsKey(type);
    }

    public void removeService(ServiceType type) {
        if (services.containsKey(type)) {
            services.remove(type);
        }
    }

    public void addAccount(BankAccount account) {
        if (repositoryManipulator.addAccount(account)) {
            lastAction = Action.ADD_ACCOUNT;
            changedItemId = account.getId();
            notifyAllServices();
        }
    }

    public void removeAccount(int accountId) {
        if (repositoryManipulator.removeAccount(accountId)) {
            lastAction = Action.REMOVE_ACCOUNT;
            changedItemId = accountId;
            notifyAllServices();
        }
    }

    public void addCategory(Category category) {
        if (repositoryManipulator.addCategory(category)) {
            lastAction = Action.ADD_CATEGORY;
            changedItemId = category.getId();
            notifyAllServices();
        }
    }

    public void removeCategory(int categoryId) {
        if (repositoryManipulator.removeCategory(categoryId)) {
            lastAction = Action.REMOVE_CATEGORY;
            changedItemId = categoryId;
            notifyAllServices();
        }
    }

    public void addOperation(Operation operation) {
        if (repositoryManipulator.addOperation(operation)) {
            lastAction = Action.ADD_OPERATION;
            changedItemId = operation.getId();
            notifyAllServices();
        }
    }

    public void removeOperation(int operationId) {
        if (repositoryManipulator.removeOperation(operationId)) {
            lastAction = Action.REMOVE_OPERATION;
            changedItemId = operationId;
            notifyAllServices();
        }
    }

    private void notifyAllServices() {
        for (Service service : services.values()) {
            service.update(this);
        }
    }
}
