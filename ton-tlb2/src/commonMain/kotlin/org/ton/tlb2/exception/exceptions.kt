package org.ton.tlb2.exception

class DuplicateColumnException(columnName: String, tableName: String) :
    Error("Duplicate column name \"$columnName\" in table \"$tableName\"")
