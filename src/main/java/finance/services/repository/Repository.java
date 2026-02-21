package finance.services.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import finance.domain.BankAccount;
import finance.domain.Category;
import finance.domain.Operation;

@Service
public class Repository {
    private final Map<Integer, BankAccount> accounts = new HashMap<>();
    private final Map<Integer, Category> categories = new HashMap<>();
    private final Map<Integer, Operation> operations = new HashMap<>();

    public Map<Integer, BankAccount> getAllAccounts() { return accounts; }
    public Map<Integer, Category> getAllCategories() { return categories; }
    public Map<Integer, Operation> getAllOperations() { return operations; }

    protected void addAccount(BankAccount account) {
        if (account != null && !accounts.containsKey(account.getId())) {
            accounts.put(account.getId(), account);
        }
    }

    public BankAccount getAccount(int accountId) {
        return accounts.get(accountId);
    }

    public boolean hasAccount(int accountId) {
        return accounts.containsKey(accountId);
    }

    protected void removeAccount(int accountId) {
        if (accounts.containsKey(accountId)) {
            accounts.remove(accountId);
        }
    }

    protected void addCategory(Category category) {
        if (category != null && !categories.containsKey(category.getId())) {
            categories.put(category.getId(), category);
        }
    }

    public Category getCategory(int categoryId) {
        return categories.get(categoryId);
    }

    public boolean hasCategory(int categoryId) {
        return categories.containsKey(categoryId);
    }

    protected void removeCategory(int categoryId) {
        if (categories.containsKey(categoryId)) {
            categories.remove(categoryId);
        }
    }

    protected void addOperation(Operation operation) {
        if (operation != null && !operations.containsKey(operation.getId())) {
            operations.put(operation.getId(), operation);
        }
    }

    public Operation getOperation(int operationId) {
        return operations.get(operationId);
    }

    public boolean hasOperation(int operationId) {
        return operations.containsKey(operationId);
    }

    protected void removeOperation(int operationId) {
        if (operations.containsKey(operationId)) {
            operations.remove(operationId);
        }
    }
}
