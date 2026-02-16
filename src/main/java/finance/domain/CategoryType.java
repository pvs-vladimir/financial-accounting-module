package finance.domain;

public enum CategoryType {
    INCOME("ДОХОД"),
    EXPENSE("РАСХОД");

    private final String showType;

    CategoryType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return showType;
    }
}
