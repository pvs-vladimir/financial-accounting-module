package finance.common;

import java.math.BigDecimal;

public class Money {
    public static final int UNIT_PRECISION = 2;

    private BigDecimal value;

    public Money() {
        this.value = BigDecimal.ZERO.setScale(UNIT_PRECISION);
    }

    public static Money Zero() {
        return new Money();
    }

    public BigDecimal getValue() { return value; }

    public Money setValue(BigDecimal value) {
        this.value = value.setScale(UNIT_PRECISION);
        return this;
    }

    public Money add(Money amount) {
        value.add(amount.getValue());
        return this;
    }

    public void subtract(Money amount) {
        value.subtract(amount.getValue());
    }

    public int compareTo(Money other) {
        return value.compareTo(other.getValue());
    }
}
