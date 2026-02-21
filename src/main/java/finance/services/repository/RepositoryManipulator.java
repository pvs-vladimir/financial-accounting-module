package finance.services.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import finance.domain.BankAccount;
import finance.domain.Category;
import finance.domain.CategoryType;
import finance.domain.Operation;
import finance.domain.OperationType;

@Service
public class RepositoryManipulator {
    private final Repository repository;

    private Map<Integer, Integer> otherToArchivedCategories;
    private Map<Integer, Integer> archivedToOtherCategories;

    public RepositoryManipulator(Repository repository) {
        this.repository = repository;
        this.otherToArchivedCategories = new HashMap<>();
        this.archivedToOtherCategories = new HashMap<>();
    }

    public boolean addAccount(BankAccount account) {
        if ((account != null) && (!repository.hasAccount(account.getId()))) {
            repository.addAccount(account);
            return true;
        }
        return false;
    }

    public boolean removeAccount(int accountId) {
        if (repository.hasAccount(accountId)) {
            repository.removeAccount(accountId);         
            for (int operationId : getAccountOperations(accountId)) {
                repository.removeOperation(operationId);
            }
            return true;
        }
        return false;
    }

    public boolean addCategory(Category category) {
        if ((category != null ) && (!repository.hasCategory(category.getId()))) {
            repository.addCategory(category);

            if (archivedToOtherCategories.containsKey(category.getId())) {
                int otherCategoryId = archivedToOtherCategories.get(category.getId());
                recategorizeOperations(otherCategoryId, category.getId());
                repository.removeCategory(otherCategoryId);
                archivedToOtherCategories.remove(category.getId());
                otherToArchivedCategories.remove(otherCategoryId);
            }

            return true;
        }
        return false;
    }

    public boolean removeCategory(int categoryId) {
        if (repository.hasCategory(categoryId)) {
            Integer otherCategoryId = getOtherCategoryId(repository.getCategory(categoryId).getType(), categoryId);
            if (otherCategoryId != null) {
                recategorizeOperations(categoryId, otherCategoryId);
                repository.removeCategory(categoryId);
                return true;
            }
        }
        return false;
    }

    public boolean addOperation(Operation operation) {
        if ((operation != null) && 
            (repository.hasAccount(operation.getBankAccountId())) &&
            (!repository.hasOperation(operation.getId()))) {
            
            if (repository.hasCategory(operation.getCategoryId())) {
                repository.addOperation(operation);
                return true;
            } else {
                Integer otherCategoryId = getOtherCategoryId(operation);
                if (otherCategoryId != null) {
                    operation.recategorize(otherCategoryId);
                    repository.addOperation(operation);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeOperation(int operationId) {
        if (repository.hasOperation(operationId)) {
            repository.removeOperation(operationId);
            return true;
        }
        return false;
    }

    private List<Integer> getAccountOperations(int accountId) {
        List<Integer> res = new ArrayList<>();
        for (Operation operation : repository.getAllOperations().values()) {
            if (operation.getBankAccountId() == accountId) {
                res.add(operation.getId());
            }
        }
        return res;
    }

    private List<Integer> getCategoryOperations(int categoryId) {
        List<Integer> res = new ArrayList<>();
        for (Operation operation : repository.getAllOperations().values()) {
            if (operation.getCategoryId() == categoryId) {
                res.add(operation.getId());
            }
        }
        return res;
    }

    private void recategorizeOperations(int from, int to) {
        for (int operationId : getCategoryOperations(from)) {
            repository.getOperation(operationId).recategorize(to);
        }
    }

    private Integer getOtherCategoryId(Operation operation) {
        if (archivedToOtherCategories.containsKey(operation.getCategoryId())) {
            return archivedToOtherCategories.get(operation.getCategoryId());
        }
        return getOtherCategoryId(operationToCategoryType(operation.getType()), operation.getCategoryId());
    }

    private Integer getOtherCategoryId(CategoryType categoryType, int categoryId) {
        CategoryType otherCategoryType = getOtherCategoryType(categoryType);
        if (otherCategoryType != null) {
            Category otherCategory = new Category(otherCategoryType, otherCategoryType.toString());
            repository.addCategory(otherCategory);
            otherToArchivedCategories.put(otherCategory.getId(), categoryId);
            archivedToOtherCategories.put(categoryId, otherCategory.getId());
            return otherCategory.getId();
        }
        return null;
    }

    private CategoryType operationToCategoryType(OperationType type) {
        if (type == OperationType.INCOME) {
            return CategoryType.INCOME;
        }
        if (type == OperationType.EXPENSE) {
            return CategoryType.EXPENSE;
        }
        return null;
    }

    private CategoryType getOtherCategoryType(CategoryType type) {
        if (type == CategoryType.INCOME) {
            return CategoryType.OTHER_INCOME;
        }
        if (type == CategoryType.EXPENSE) {
            return CategoryType.OTHER_EXPENSE;
        }
        return null;
    }
}
