package finance.domain;

public enum FinanceElementType {
    ACCOUNT("СЧЕТ"),
    CATEGORY("КАТЕГОРИЯ"),
    OPERATION("ОПЕРАЦИЯ");

    private final String showType;

    FinanceElementType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return showType;
    }
}
