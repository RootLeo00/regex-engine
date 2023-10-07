public enum RegExTokenType {
    CONCAT(0xC04CA7),
    STAR(0xE7011E),
    PLUS(0x002B),
    ALTERN(0xA17E54),
    PROTECTION(0xBADDAD),
    PARENTHESEOUVRANT(0x16641664),
    PARENTHESEFERMANT(0x51515151),
    DOT(0xD07);

    private final int value;

    RegExTokenType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
