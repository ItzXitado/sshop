package io.github.xitadoo.sshop.util;

public enum Sort {

    DATE,
    DATE_REVERSED,
    AMOUNT,
    AMOUNT_REVERSED();

    public Sort reverse() {
        switch (this) {
            case DATE:
                return DATE_REVERSED;
            case AMOUNT:
                return AMOUNT_REVERSED;
            case DATE_REVERSED:
                return DATE;
            case AMOUNT_REVERSED:
                return AMOUNT;
        }
        return null;
    }

    public Sort other() {
        switch (this) {
            case DATE:
            case DATE_REVERSED:
                return AMOUNT;
            case AMOUNT:
            case AMOUNT_REVERSED:
                return DATE;
        }
        return null;
    }
}
