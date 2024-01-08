package fq.fretquiz.theory.music;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

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
        return VALUES[((this.ordinal() + VALUES.length) - 1) % VALUES.length];
    }

    public static class Serializer extends JsonSerializer<WhiteKey> {

        @Override
        public void serialize(WhiteKey whiteKey, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(whiteKey.value);
        }
    }
}
