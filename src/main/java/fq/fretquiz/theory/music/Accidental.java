package fq.fretquiz.theory.music;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public enum Accidental {
    DOUBLE_FLAT("bb"),
    FLAT("b"),
    NONE(""),
    SHARP("#"),
    DOUBLE_SHARP("##");

    private static final Accidental[] VALUES = values();

    public final String value;

    Accidental(String value) {
        this.value = value;
    }

    public static Accidental from(String name) {
        return switch (name) {
            case "bb" -> DOUBLE_FLAT;
            case "b" -> FLAT;
            case "" -> NONE;
            case "#" -> SHARP;
            case "##" -> DOUBLE_SHARP;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }

    public int halfStepOffset() {
        return switch (this) {
            case DOUBLE_FLAT -> -2;
            case FLAT -> -1;
            case NONE -> 0;
            case SHARP -> 1;
            case DOUBLE_SHARP -> 2;
        };
    }

    public Accidental next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public Accidental previous() {
        return VALUES[((this.ordinal() + VALUES.length) - 1) % VALUES.length];
    }

    public static class Serializer extends JsonSerializer<Accidental> {

        @Override
        public void serialize(Accidental acc, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(acc.name());
        }
    }
}
