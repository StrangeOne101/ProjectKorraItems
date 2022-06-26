package com.projectkorra.items;

public enum DefinedBoolean {

    TRUE(true),
    FALSE(false),
    UNDEFINED(null);

    private Boolean value;

    DefinedBoolean(Boolean value) {
        this.value = value;
    }

    /**
     * Test a boolean against this defined boolean.
     * @param b The boolean
     * @return True if it matches
     */
    public boolean test(boolean b) {
        return value != null && value == b;
    }
}
