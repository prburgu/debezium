/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;

import java.util.Arrays;

import io.debezium.connector.postgresql.connection.AbstractReplicationMessageColumn;

/**
 * Represents a toasted column in a {@link io.debezium.connector.postgresql.connection.ReplicationStream}.
 *
 * Some decoder implementations may stream information about a column but provide an indicator that the field was not
 * changed and therefore toasted.  This implementation acts as an indicator for such fields that are contained within
 * a {@link io.debezium.connector.postgresql.connection.ReplicationMessage}.
 *
 * @author Chris Cranford
 */
public class UnchangedToastedReplicationMessageColumn extends AbstractReplicationMessageColumn {

    /**
     * Marker value indicating an unchanged TOAST column value.
     */
    public static final Object UNCHANGED_TOAST_VALUE = new Object();
    public static final Object UNCHANGED_INT_ARRAY_TOAST_VALUE = new Object();
    public static final Object UNCHANGED_BIGINT_ARRAY_TOAST_VALUE = new Object();

    private boolean isStringArrayColumn = false;
    private boolean isIntArrayColumn = false;
    private boolean isBigintArrayColumn = false;

    public UnchangedToastedReplicationMessageColumn(String columnName, PostgresType type, String typeWithModifiers, boolean optional) {
        super(columnName, type, typeWithModifiers, optional);
        if (typeWithModifiers.equals("text[]") || typeWithModifiers.equals("_text")) {
            isStringArrayColumn = true;
        }
        else if (typeWithModifiers.equals("integer[]") || typeWithModifiers.equals("_int4")) {
            isIntArrayColumn = true;
        }
        else if (typeWithModifiers.equals("bigint[]") || typeWithModifiers.equals("_int8")) {
            isBigintArrayColumn = true;
        }
    }

    @Override
    public boolean isToastedColumn() {
        return true;
    }

    @Override
    public Object getValue(PostgresStreamingChangeEventSource.PgConnectionSupplier connection, boolean includeUnknownDatatypes) {
        if (isStringArrayColumn) {
            return Arrays.asList(UNCHANGED_TOAST_VALUE);
        }
        if (isIntArrayColumn) {
            return UNCHANGED_INT_ARRAY_TOAST_VALUE;
        }
        if (isBigintArrayColumn) {
            return UNCHANGED_BIGINT_ARRAY_TOAST_VALUE;
        }
        return UNCHANGED_TOAST_VALUE;
    }
}
