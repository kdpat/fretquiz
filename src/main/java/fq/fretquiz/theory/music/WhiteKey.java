package fq.fretquiz.theory.music;

public enum WhiteKey {
    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    A("A"),
    B("B");

    private static final WhiteKey[] VALUES = values();

    public final String value;

    WhiteKey(String value) {
        this.value = value;
    }

    public int halfStepsFromC() {
        return switch (this) {
            case C -> 0;
            case D -> 2;
            case E -> 4;
            case F -> 5;
            case G -> 7;
            case A -> 9;
            case B -> 11;
        };
    }

    public WhiteKey next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public WhiteKey previous() {
        if (this == C) {
            return B;
        }
        return VALUES[(this.ordinal() - 1) % VALUES.length];
    }


    @Override
    public String toString() {
        return value;
    }
}
