package finance.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import finance.common.Money;
import finance.domain.Category;
import finance.domain.CategoryType;
import finance.domain.Operation;
import finance.domain.OperationType;
import finance.services.repository.Repository;
import finance.services.tasks.AnalyticTask;
import finance.services.tasks.AutoUpdateTask;

public class AnalyticService extends Service {
    public class CalculationPeriod {
        public LocalDate start;
        public LocalDate end;

        public CalculationPeriod() {
            this.start = LocalDate.now();
            this.end = LocalDate.now();
        }

        public CalculationPeriod(LocalDate start) {
            this.start = start;
            this.end = LocalDate.now();
        }

        public CalculationPeriod(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    public class MoneyFlows {
        public Money income;
        public Money expense;
        public Money profit;

        public MoneyFlows() {
            this.income = Money.Zero();
            this.expense = Money.Zero();
            this.profit = Money.Zero();
        }

        public MoneyFlows(Money income, Money expense, Money profit) {
            this.income = income;
            this.expense = expense;
            this.profit = profit;
        }
    }

    private final Repository repository;
    private CalculationPeriod period;
    private List<Integer> operationsForPeriod;
    private Map<Integer, List<Integer>> incomeOperationsForPeriodByCategory;
    private Map<Integer, List<Integer>> expenseOperationsForPeriodByCategory;
    private Money incomeForPeriod;
    private Money expenseForPeriod;
    private Money profitForPeriod;
    private Map<Integer, Money> incomeForPeriodByCategory;
    private Map<Integer, Money> expenseForPeriodByCategory;

    public AnalyticService(ServiceType type, Repository repository) {
        super(type);
        this.repository = repository;
        this.period = new CalculationPeriod();
        this.operationsForPeriod = new ArrayList<>();
        this.incomeOperationsForPeriodByCategory = new HashMap<>();
        this.expenseOperationsForPeriodByCategory = new HashMap<>();
        this.incomeForPeriod = Money.Zero();
        this.expenseForPeriod = Money.Zero();
        this.profitForPeriod = Money.Zero();
        this.incomeForPeriodByCategory = new HashMap<>();
        this.expenseForPeriodByCategory = new HashMap<>();
    }

    public CalculationPeriod getCalculationPeriod() { return period; }
    public List<Integer> getOperationsForPeriod() { return operationsForPeriod; }
    public Map<Integer, List<Integer>> getIncomeOperationsForPeriodByCategory() { 
        return incomeOperationsForPeriodByCategory;
    }
    public Map<Integer, List<Integer>> getExpenseOperationsForPeriodByCategory() {
        return expenseOperationsForPeriodByCategory;
    }
    public Money getIncomeForPeriod() { return incomeForPeriod; }
    public Money getExpenseForPeriod() { return expenseForPeriod; }
    public Money getProfitForPeriod() { return profitForPeriod; }
    public Map<Integer, Money> getIncomeForPeriodByCategory() {
        return incomeForPeriodByCategory;
    }
    public Map<Integer, Money> getExpenseForPeriodByCategory() {
        return expenseForPeriodByCategory;
    }

    public void setCalculationPeriod(CalculationPeriod period) {
        this.period = period;
    }

    @Override
    public void update(Manager manager) {
        if (!tasks.isEmpty()) {
            updateOperationsForPeriod();

            if (tasks.contains(AnalyticTask.MONEY_FLOWS_FOR_PERIOD)) {
                updateMoneyFlowsForPeriod();
            }

            if (tasks.contains(AnalyticTask.INCOME_FOR_PERIOD_BY_CATEGORY)) {
                updateIncomeOperationsForPeriodByCategory();
                updateIncomeForPeriodByCategory();
            }

            if (tasks.contains(AnalyticTask.EXPENSE_FOR_PERIOD_BY_CATEGORY)) {
                updateExpenseOperationsForPeriodByCategory();
                updateExpenseForPeriodByCategory();
            }
        }
    }

    public MoneyFlows calculateMoneyFlowsForPeriod(CalculationPeriod period) {
        CalculationPeriod savedPeriod = this.period;

        setCalculationPeriod(period);
        updateOperationsForPeriod();
        updateMoneyFlowsForPeriod();

        MoneyFlows result = new MoneyFlows(incomeForPeriod, expenseForPeriod, profitForPeriod);

        setCalculationPeriod(savedPeriod);
        update(null);

        return result;
    }

    public Map<Integer, Money> calculateIncomeForPeriodByCategory(CalculationPeriod period) {
        CalculationPeriod savedPeriod = this.period;

        setCalculationPeriod(period);
        updateOperationsForPeriod();
        updateIncomeOperationsForPeriodByCategory();
        updateIncomeForPeriodByCategory();

        Map<Integer, Money> result = incomeForPeriodByCategory;

        setCalculationPeriod(savedPeriod);
        update(null);

        return result;
    }

    public Map<Integer, Money> calculateExpenseForPeriodByCategory(CalculationPeriod period) {
        CalculationPeriod savedPeriod = this.period;

        setCalculationPeriod(period);
        updateOperationsForPeriod();
        updateExpenseOperationsForPeriodByCategory();
        updateExpenseForPeriodByCategory();

        Map<Integer, Money> result = expenseForPeriodByCategory;

        setCalculationPeriod(savedPeriod);
        update(null);

        return result;
    }

    @Override
    protected boolean isSuitableTask(AutoUpdateTask task) {
        return task.getOwner() == ServiceType.ANALYTIC;
    }

    private void updateOperationsForPeriod() {
        Collection<Operation> operations = repository.getAllOperations().values();
        operationsForPeriod = operations.stream()
            .filter(op -> !op.getDate().isBefore(period.start))
            .filter(op -> !op.getDate().isAfter(period.end))
            .map(Operation::getId)
            .collect(Collectors.toList());
    }

    private void updateIncomeOperationsForPeriodByCategory() {
        List<Integer> categories = getIncomeCategories();

        incomeOperationsForPeriodByCategory = new HashMap<>();
        for (int categoryId : categories) {
            incomeOperationsForPeriodByCategory.put(categoryId, new ArrayList<>());
        }

        for (int operationId : operationsForPeriod) {
            Operation operation = repository.getOperation(operationId);
            if (operation.getType() == OperationType.INCOME) {
                incomeOperationsForPeriodByCategory.get(operation.getCategoryId()).add(operationId);
            }
        }
    }

    private void updateExpenseOperationsForPeriodByCategory() {
        List<Integer> categories = getExpenseCategories();

        expenseOperationsForPeriodByCategory = new HashMap<>();
        for (int categoryId : categories) {
            expenseOperationsForPeriodByCategory.put(categoryId, new ArrayList<>());
        }

        for (int operationId : operationsForPeriod) {
            Operation operation = repository.getOperation(operationId);
            if (operation.getType() == OperationType.EXPENSE) {
                expenseOperationsForPeriodByCategory.get(operation.getCategoryId()).add(operationId);
            }
        }
    }

    private void updateMoneyFlowsForPeriod() {
        incomeForPeriod = Money.Zero();
        expenseForPeriod = Money.Zero();

        for (int operationId : operationsForPeriod) {
            Operation operation = repository.getOperation(operationId);
            if (operation.getType() == OperationType.INCOME) {
                incomeForPeriod.add(operation.getAmount());
            } else if (operation.getType() == OperationType.EXPENSE) {
                expenseForPeriod.add(operation.getAmount());
            }
        }

        profitForPeriod = incomeForPeriod;
        profitForPeriod.subtract(expenseForPeriod);
    }

    private void updateIncomeForPeriodByCategory() {
        incomeForPeriodByCategory = incomeOperationsForPeriodByCategory
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(id -> repository.getOperation(id).getAmount())
                    .reduce(Money.Zero(), Money::add)  
            ));
    }

    private void updateExpenseForPeriodByCategory() {
        expenseForPeriodByCategory = expenseOperationsForPeriodByCategory
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(id -> repository.getOperation(id).getAmount())
                    .reduce(Money.Zero(), Money::add)  
            ));
    }

    private List<Integer> getIncomeCategories() {
        Collection<Category> categories = repository.getAllCategories().values();
        return categories.stream()
            .filter(cat -> (cat.getType() == CategoryType.INCOME))
            .map(Category::getId)
            .collect(Collectors.toList());
    }

    private List<Integer> getExpenseCategories() {
        Collection<Category> categories = repository.getAllCategories().values();
        return categories.stream()
            .filter(cat -> (cat.getType() == CategoryType.EXPENSE))
            .map(Category::getId)
            .collect(Collectors.toList());
    }
}
