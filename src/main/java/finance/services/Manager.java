package finance.services;

import java.util.HashMap;
import java.util.Map;

import finance.domain.BankAccount;
import finance.domain.Category;
import finance.domain.Operation;

public class Manager {
    private final Map<ServiceType, Service> services = new HashMap<>();
    private final Repository repository;

    public Manager(Repository repository) {
        this.repository = repository;
    }

    public Map<ServiceType, Service> getAllServices() { return services; }
    public Repository getRepository() { return repository; }

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
        if (!repository.hasAccount(account.getId())) {
            repository.addAccount(account);
        }
    }

    public void removeAccount(int accountId) {
        if (repository.hasAccount(accountId)) {
            repository.removeCategory(accountId);
        }
    }

    public void addCategory(Category category) {
        if (!repository.hasCategory(category.getId())) {
            repository.addCategory(category);
            notifyService(ServiceType.ANALYTIC);
        }
    }

    public void removeCategory(int categoryId) {
        if (repository.hasCategory(categoryId)) {
            repository.removeCategory(categoryId);
            notifyService(ServiceType.ANALYTIC);
        }
    }

    public void addOperation(Operation operation) {
        if (!repository.hasOperation(operation.getId())) {
            repository.addOperation(operation);
            if (isActiveService(ServiceType.BANK_ACCOUNT)) {
                BankAccountService accountService = (BankAccountService) services.get(ServiceType.BANK_ACCOUNT);
                accountService.markNewOperation(operation.getId());
            }
            notifyAllServices();
        }
    }

    public void removeOperation(int operationId) {
        if (repository.hasOperation(operationId)) {
            repository.removeOperation(operationId);
        }
    }

    private void notifyAllServices() {
        for (Service service : services.values()) {
            service.update();
        }
    }

    private void notifyService(ServiceType type) {
        if (isActiveService(type)) {
            services.get(type).update();
        }
    }
}
