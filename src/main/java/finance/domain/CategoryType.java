package finance.domain;

public enum CategoryType {
    INCOME("ДОХОД"),
    EXPENSE("РАСХОД"),
    OTHER_INCOME("ПРОЧИЕ ДОХОДЫ"),
    OTHER_EXPENSE("ПРОЧИЕ РАСХОДЫ");

    private final String showType;

    CategoryType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return showType;
    }
}
