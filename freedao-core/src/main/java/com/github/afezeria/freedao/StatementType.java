package com.github.afezeria.freedao;

/**
 *
 */
public enum StatementType {
    INSERT(true),
    UPDATE(true),
    DELETE(true),
    SELECT(false);
    public final boolean isUpdateStatement;

    StatementType(boolean isUpdateStatement) {
        this.isUpdateStatement = isUpdateStatement;
    }
}
