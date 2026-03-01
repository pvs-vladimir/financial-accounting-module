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

    public Manager(Repository repository, RepositoryManipulator manipulator) {
        this.repositoryManipulator = manipulator;
        this.lastAction = Action.NONE;
    }

    public Map<ServiceType, Service> getAllServices() { return services; }
    public Action getLastAction() { return lastAction; }

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
            notifyAllServices();
        }
    }

    public void removeAccount(int accountId) {
        if (repositoryManipulator.removeAccount(accountId)) {
            lastAction = Action.REMOVE_ACCOUNT;
            notifyAllServices();
        }
    }

    public void addCategory(Category category) {
        if (repositoryManipulator.addCategory(category)) {
            lastAction = Action.ADD_CATEGORY;
            notifyAllServices();
        }
    }

    public void removeCategory(int categoryId) {
        if (repositoryManipulator.removeCategory(categoryId)) {
            lastAction = Action.REMOVE_CATEGORY;
            notifyAllServices();
        }
    }

    public void addOperation(Operation operation) {
        if (repositoryManipulator.addOperation(operation)) {
            lastAction = Action.ADD_OPERATION;
            notifyAllServices();
        }
    }

    public void removeOperation(int operationId) {
        if (repositoryManipulator.removeOperation(operationId)) {
            lastAction = Action.REMOVE_OPERATION;
            notifyAllServices();
        }
    }

    public AnalyticService getAnalytic() {
        lastAction = Action.GET_ANALYTIC;
        if (isActiveService(ServiceType.ANALYTIC)) {
            return (AnalyticService) services.get(ServiceType.ANALYTIC);
        }
        return null;
    }

    public BankAccountService getBalance() {
        lastAction = Action.GET_BALANCE;
        if (isActiveService(ServiceType.BANK_ACCOUNT)) {
            return (BankAccountService) services.get(ServiceType.BANK_ACCOUNT);
        }
        return null;
    }

    private void notifyAllServices() {
        for (Service service : services.values()) {
            service.update(this);
        }
    }
}
