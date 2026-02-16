package finance.domain;

import finance.common.Money;

public class BankAccount {
    private static int nextId = 0;

    private final int id;
    private String name;
    private Money balance;

    public BankAccount(String name) {
        this.id = nextId++;
        this.name = name;
        this.balance = Money.Zero();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Money getBalance() { return balance; }

    public void rename(String name) {
        this.name = name;
    }

    public void resetBalance() {
        balance = Money.Zero();
    }

    public boolean deposit(Money amount) {
        if (amount.compareTo(Money.Zero()) > 0){
            balance.add(amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(Money amount) {
        if ((amount.compareTo(Money.Zero()) > 0) &&
            (balance.compareTo(amount) >= 0)) {
            balance.subtract(amount);
            return true;
        }
        return false;
    }
}
