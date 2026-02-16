package finance.domain;

import java.time.LocalDate;
import java.util.Optional;

import finance.common.Money;

public class OperationBuilder {
    private OperationType type;
    private int bankAccountId;
    private Money amount;
    private LocalDate date;
    private String description;
    private int categoryId;

    public OperationBuilder() {
        this.type = null;
        this.bankAccountId = -1;
        this.amount = Money.Zero();
        this.date = LocalDate.now();
        this.description = "НЕ УКАЗАНО";
        this.categoryId = -1;
    }

    public OperationBuilder addType(OperationType type) {
        this.type = type;
        return this;
    }

    public OperationBuilder addBankAccountId(int bankAccountId) {
        this.bankAccountId = bankAccountId;
        return this;
    }

    public OperationBuilder addAmount(Money amount) {
        this.amount = amount;
        return this;
    }

    public OperationBuilder addDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public OperationBuilder addDescription(String description) {
        this.description = description;
        return this;
    }

    public OperationBuilder addCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Operation create() throws IllegalStateException {
        Optional<String> err = hasCreationError();
        if (err.isPresent()) {
            throw new IllegalStateException("Ошибка создания операции: " + err.get());
        }
        return new Operation(type, bankAccountId, amount, date, description, categoryId);
    }

    private Optional<String> hasCreationError() {
        if (type == null) {
            return Optional.of("Не указан тип операции");
        }
        if (bankAccountId < 0) {
            return Optional.of("Не привязан банковский аккаунт");
        }
        if (amount.compareTo(Money.Zero()) == 0) {
            return Optional.of("Операция должна иметь ненулевую сумму");
        }
        if (categoryId < 0) {
            return Optional.of("Не добавлена категория");
        }
        return Optional.empty();
    }
}
