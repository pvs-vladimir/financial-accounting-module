package finance.services.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import finance.common.Money;
import finance.domain.BankAccount;
import finance.domain.Category;
import finance.domain.CategoryType;
import finance.domain.Operation;
import finance.domain.OperationBuilder;
import finance.domain.OperationType;

@DisplayName("Тесты для RepositoryManipulator")
class RepositoryManipulatorTest {
    private static final String ACCOUNT_1 = "Account1";
    private static final String ACCOUNT_2 = "Account2";

    private static final String CATEGORY_1 = "Category1";
    private static final String CATEGORY_2 = "Category2";
    private static final CategoryType CATEGORY_TYPE = CategoryType.EXPENSE;
    private static final CategoryType RECATEGORY_TYPE = 
                                      (CATEGORY_TYPE == CategoryType.INCOME)
                                      ? CategoryType.OTHER_INCOME
                                      : CategoryType.OTHER_EXPENSE;

    private static final OperationType OPERATION_TYPE = OperationType.EXPENSE;
    private static final Money AMOUNT = new Money().setValue(new BigDecimal("1000.00"));

    private Repository repository;
    private RepositoryManipulator repositoryManipulator;

    private BankAccount account1;
    private BankAccount account2;

    private Category category1;
    private Category category2;

    private Operation operation11;
    private Operation operation12;
    private Operation operation21;

    @BeforeEach
    void setup() {
        repository = new Repository();
        repositoryManipulator = new RepositoryManipulator(repository);

        account1 = new BankAccount(ACCOUNT_1);
        account2 = new BankAccount(ACCOUNT_2);

        category1 = new Category(CATEGORY_TYPE, CATEGORY_1);
        category2 = new Category(CATEGORY_TYPE, CATEGORY_2);

        try {
            OperationBuilder ob = new OperationBuilder();
            ob.addType(OPERATION_TYPE).addAmount(AMOUNT);

            operation11 = ob.addBankAccountId(account1.getId())
                            .addCategoryId(category1.getId())
                            .create();
            operation12 = ob.addBankAccountId(account1.getId())
                            .addCategoryId(category2.getId())
                            .create();
            operation21 = ob.addBankAccountId(account2.getId())
                            .addCategoryId(category1.getId())
                            .create();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Добавление счетов должно сохранять все уникальные, ненулевые счета")
    void testAddAccount() {
        assertFalse(repository.hasAccount(account1.getId()));
        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repository.hasAccount(account1.getId()));

        assertEquals(1, repository.getAllAccounts().size());
        assertTrue(repositoryManipulator.addAccount(account2));
        assertEquals(2, repository.getAllAccounts().size());

        assertFalse(repositoryManipulator.addAccount(account1));
        assertEquals(2, repository.getAllAccounts().size());

        assertFalse(repositoryManipulator.addAccount(null));
        assertEquals(2, repository.getAllAccounts().size());
    }

    @Test
    @DisplayName("Добавление категорий должно сохранять все уникальные, ненулевые категории")
    void testAddCategory() {
        assertFalse(repository.hasCategory(category1.getId()));
        assertTrue(repositoryManipulator.addCategory(category1));
        assertTrue(repository.hasCategory(category1.getId()));

        assertEquals(1, repository.getAllCategories().size());
        assertTrue(repositoryManipulator.addCategory(category2));
        assertEquals(2, repository.getAllCategories().size());

        assertFalse(repositoryManipulator.addCategory(category1));
        assertEquals(2, repository.getAllCategories().size());

        assertFalse(repositoryManipulator.addCategory(null));
        assertEquals(2, repository.getAllCategories().size());
    }

    @Test
    @DisplayName("Добавление операций должно сохранять все уникальные, ненулевые операции для существующих счетов")
    void testAddOperation() {
        assertFalse(repository.hasOperation(operation11.getId()));
        assertFalse(repositoryManipulator.addOperation(operation11));
        assertFalse(repository.hasOperation(operation11.getId()));

        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repositoryManipulator.addOperation(operation11));
        assertTrue(repository.hasOperation(operation11.getId()));

        assertEquals(1, repository.getAllOperations().size());
        assertTrue(repositoryManipulator.addOperation(operation12));
        assertEquals(2, repository.getAllOperations().size());

        assertFalse(repositoryManipulator.addOperation(operation11));
        assertEquals(2, repository.getAllOperations().size());

        assertFalse(repositoryManipulator.addOperation(null));
        assertEquals(2, repository.getAllOperations().size());
    }

    @Test
    @DisplayName("Отсутствие категории сохраняет, но перекатегоризирует операции, а ее добавление возвращает исходное состояние")
    void testAddOperationWithRecategorization() {
        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repositoryManipulator.addAccount(account2));

        assertEquals(CATEGORY_TYPE, category1.getType());
        assertEquals(category1.getId(), operation11.getCategoryId());

        assertTrue(repositoryManipulator.addOperation(operation11));

        Operation recategorizedOperation = repository.getOperation(operation11.getId());
        Category reCategory = repository.getCategory(recategorizedOperation.getCategoryId());
        assertNotEquals(category1.getId(), recategorizedOperation.getCategoryId());
        assertEquals(RECATEGORY_TYPE, reCategory.getType());

        assertTrue(repositoryManipulator.addCategory(category1));

        Operation returnedOperation = repository.getOperation(operation11.getId());
        Category returnedCategory = repository.getCategory(returnedOperation.getCategoryId());

        assertEquals(category1.getId(), returnedOperation.getCategoryId());
        assertEquals(CATEGORY_TYPE, returnedCategory.getType());

        assertEquals(category1.getId(), operation21.getCategoryId());

        assertTrue(repositoryManipulator.addOperation(operation21));

        Operation nonRecategorizedOperation = repository.getOperation(operation21.getId());
        Category nonReCategory = repository.getCategory(nonRecategorizedOperation.getCategoryId());
        assertEquals(category1.getId(), nonRecategorizedOperation.getCategoryId());
        assertEquals(CATEGORY_TYPE, nonReCategory.getType());
    }

    @Test
    @DisplayName("Удаление операции убирает только ее и не затрагивает счета и категории")
    void testRemoveOperation() {
        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repositoryManipulator.addCategory(category1));
        assertTrue(repositoryManipulator.addOperation(operation11));

        assertTrue(repository.hasOperation(operation11.getId()));
        assertTrue(repositoryManipulator.removeOperation(operation11.getId()));
        assertFalse(repository.hasOperation(operation11.getId()));

        assertTrue(repository.hasAccount(account1.getId()));
        assertTrue(repository.hasCategory(category1.getId()));
    }

    @Test
    @DisplayName("Удаление счета убирает только счет и его операции")
    void testRemoveAccount() {
        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repositoryManipulator.addAccount(account2));
        assertTrue(repositoryManipulator.addCategory(category1));
        assertTrue(repositoryManipulator.addOperation(operation11));
        assertTrue(repositoryManipulator.addOperation(operation21));

        assertTrue(repository.hasAccount(account1.getId()));
        assertTrue(repository.hasAccount(account2.getId()));
        assertTrue(repository.hasOperation(operation11.getId()));
        assertTrue(repository.hasOperation(operation21.getId()));

        assertTrue(repositoryManipulator.removeAccount(account1.getId()));

        assertFalse(repository.hasAccount(account1.getId()));
        assertTrue(repository.hasAccount(account2.getId()));
        assertFalse(repository.hasOperation(operation11.getId()));
        assertTrue(repository.hasOperation(operation21.getId()));
    }

    @Test
    @DisplayName("Удаление категории убирает только ее и сохраняет, перекатегоризируя, операции")
    void testRemoveCategory() {
        assertTrue(repositoryManipulator.addAccount(account1));
        assertTrue(repositoryManipulator.addCategory(category1));
        assertTrue(repositoryManipulator.addOperation(operation11));

        assertTrue(repository.hasCategory(category1.getId()));
        assertTrue(repository.hasOperation(operation11.getId()));
        assertEquals(CATEGORY_TYPE, category1.getType());
        assertEquals(category1.getId(), operation11.getCategoryId());

        assertTrue(repositoryManipulator.removeCategory(category1.getId()));

        Operation recategorizedOperation = repository.getOperation(operation11.getId());
        Category reCategory = repository.getCategory(recategorizedOperation.getCategoryId());

        assertFalse(repository.hasCategory(category1.getId()));
        assertTrue(repository.hasOperation(operation11.getId()));
        assertNotEquals(category1.getId(), recategorizedOperation.getCategoryId());
        assertEquals(RECATEGORY_TYPE, reCategory.getType());
    }
}
