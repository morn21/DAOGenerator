package online.morn.convert.mysql.vo;

/**
 * 列模型
 */
public class ColumnInfoVO {
    /**列名称 如：user_id*/
    private String columnName;
    /**列类型 如：VARCHAR*/
    private String columnType;
    /**java中的字段名 如：userId*/
    private String javaFieldName;
    /**java中的数据类型 如：java.lang.String*/
    private String javaType;
    /**简易java中的数据类型 如：String*/
    private String simpleJavaType;
    /**列大小*/
    private Integer dataSize;
    /**列备注*/
    private String remarks;
    /**是否允许为null*/
    private Integer nullable;
    /**数字*/
    private Integer digits;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public Integer getDataSize() {
        return dataSize;
    }

    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getNullable() {
        return nullable;
    }

    public void setNullable(Integer nullable) {
        this.nullable = nullable;
    }

    public Integer getDigits() {
        return digits;
    }

    public void setDigits(Integer digits) {
        this.digits = digits;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public void setJavaFieldName(String javaFieldName) {
        this.javaFieldName = javaFieldName;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getSimpleJavaType() {
        return simpleJavaType;
    }

    public void setSimpleJavaType(String simpleJavaType) {
        this.simpleJavaType = simpleJavaType;
    }
}
