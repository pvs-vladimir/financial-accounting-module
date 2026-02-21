package finance.domain;

import java.time.LocalDate;

import finance.common.Money;

public class Operation {
    private static int nextId = 0;

    private final int id;
    private final OperationType type;
    private final int bankAccountId;
    private final Money amount;
    private final LocalDate date;
    private final String description;
    private int categoryId;

    protected Operation(OperationType type, int bankAccountId, Money amount,
                        LocalDate date, String description, int categoryId) {
        this.id = nextId++;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }
    public OperationType getType() { return type; }
    public int getBankAccountId() { return bankAccountId; }
    public Money getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public int getCategoryId() { return categoryId; }

    public void recategorize(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return new String();
    }
}
