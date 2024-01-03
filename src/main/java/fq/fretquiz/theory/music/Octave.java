package fq.fretquiz.theory.music;

public enum Octave {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9);

    public final int value;
    private static final Octave[] VALUES = values();

    Octave(int value) {
        this.value = value;
    }

    public static Octave from(String num) {
        return switch (num) {
            case "1" -> ONE;
            case "2" -> TWO;
            case "3" -> THREE;
            case "4" -> FOUR;
            case "5" -> FIVE;
            case "6" -> SIX;
            case "7" -> SEVEN;
            case "8" -> EIGHT;
            case "9" -> NINE;
            default -> throw new IllegalStateException("Unexpected value: " + num);
        };
    }

    public Octave next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}