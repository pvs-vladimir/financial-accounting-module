package finance.services;

import finance.common.Money;
import finance.domain.BankAccount;
import finance.domain.Operation;
import finance.domain.OperationType;
import finance.services.repository.Repository;
import finance.services.tasks.AutoUpdateTask;
import finance.services.tasks.BankAccountTask;

public class BankAccountService extends Service {
    private final Repository repository;
    private Money totalBalance;

    public BankAccountService(ServiceType type, Repository repository) {
        super(type);
        this.repository = repository;
        this.totalBalance = Money.Zero();
    }

    public Money getTotalBalance() { return totalBalance; }

    @Override
    public void update(Manager manager) {
        if (!tasks.isEmpty()) {
            if (tasks.contains(BankAccountTask.BALANCE)) {
                if (manager.getLastAction() == Action.ADD_OPERATION) {
                    updateBalanceWithOperation(manager.getChangedItemId());
                } else if (manager.getLastAction() == Action.REMOVE_OPERATION) {
                    reCalculateBalance();
                }
            }
        }
    }

    public void reCalculateBalance() {
        resetBalance();
        for (Operation operation : repository.getAllOperations().values()) {
            updateBalanceWithOperation(operation.getId());
        }
    }

    @Override
    protected boolean isSuitableTask(AutoUpdateTask task) {
        return task.getOwner() == ServiceType.BANK_ACCOUNT;
    }

    private void updateBalanceWithOperation(Integer operationId) {
        if (operationId == null) return;

        Operation operation = repository.getOperation(operationId);
        BankAccount account = repository.getAccount(operation.getBankAccountId());
        if (operation.getType() == OperationType.INCOME) {
            account.deposit(operation.getAmount());
            totalBalance.add(operation.getAmount());
        } else if (operation.getType() == OperationType.EXPENSE) {
            account.withdraw(operation.getAmount());
            totalBalance.subtract(operation.getAmount());
        }
    }

    private void resetBalance() {
        for (BankAccount account : repository.getAllAccounts().values()) {
            account.resetBalance();
        }
        totalBalance = Money.Zero();
    }
}
