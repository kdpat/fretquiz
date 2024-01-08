package fq.fretquiz.theory.music;

public enum Octave {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8);

    private static final Octave[] VALUES = values();
    public final int value;

    Octave(int value) {
        this.value = value;
    }

    public static Octave from(String num) {
        return switch (num) {
            case "0" -> ZERO;
            case "1" -> ONE;
            case "2" -> TWO;
            case "3" -> THREE;
            case "4" -> FOUR;
            case "5" -> FIVE;
            case "6" -> SIX;
            case "7" -> SEVEN;
            case "8" -> EIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + num);
        };
    }

    public Octave next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public Octave previous() {
        if (this == ZERO) {
            throw new IllegalStateException("No octave below ONE");
        }
        return VALUES[(this.ordinal() - 1) % VALUES.length];
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
