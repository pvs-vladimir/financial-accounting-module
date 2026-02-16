package finance.domain;

public enum OperationType {
    INCOME("ДОХОД"),
    EXPENSE("РАСХОД");

    private final String showType;

    OperationType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return showType;
    }
}
