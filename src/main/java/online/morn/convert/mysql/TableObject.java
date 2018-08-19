package online.morn.convert.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import online.morn.convert.mysql.vo.ColumnInfoVO;
import org.apache.maven.shared.utils.StringUtils;

public class TableObject {

    /**数据连接*/
    private Connection conn;
    /**表名*/
    private String tableName;
    /**表注释*/
    private String tableComment;
    /**列信息数组*/
    private List<ColumnInfoVO> columnInfoVOList;

    /**
     * 构造方法
     * @param conn
     * @param tableName
     */
    public TableObject(Connection conn, String tableName){
        this.conn = conn;
        this.tableName = tableName;
        try {
            this.iniTableComment();
            this.ini();
        } catch (SQLException e) {
            PublicUtil.printException(e);
        }
    }

    /**
     * 【私有】获得表注释
     * @return
     */
    private void iniTableComment(){
        String comment = "";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SHOW CREATE TABLE `" + this.tableName + "`");
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                int index = createDDL.indexOf("COMMENT='");
                if (index < 0) {
                    comment = "";
                } else {
                    comment = createDDL.substring(index + 9, createDDL.length() - 1);
                }
            }
        } catch (SQLException e) {
            PublicUtil.printException(e);
        } finally {
            DBManager.close(null,stmt,rs);
        }
        this.tableComment = comment;
    }

    /**
     * 【私有】数据字段名转换为java类名名
     * @param columnName
     * @return
     */
    private String convertColumnNameToJavaClassName(String columnName){
        String returnStr = "";
        for(String str : columnName.split("_")){
            returnStr += PublicUtil.upperCaseFirst(str.toLowerCase());
        }
        return returnStr;
    }

    /**
     * 【私有】数据字段名转换为java字段名
     * @param columnName
     * @return
     */
    private String convertColumnNameToJavaFieldName(String columnName){
        return PublicUtil.lowerCaseFirst(this.convertColumnNameToJavaClassName(columnName));
    }

    /**
     * 【私有】数据库字段类型转为java字段类型
     * @param columnType
     * @return
     */
    private String convertColumnTypeToJavaType(String columnType){
        String returnStr = "";
        if("VARCHAR".equals(columnType) || "TEXT".equals(columnType) || "LONGTEXT".equals(columnType)){
            returnStr = "java.lang.String";
        } else if("SMALLINT".equals(columnType) || "INT".equals(columnType)){
            returnStr = "java.lang.Integer";
        } else if("BIGINT".equals(columnType)){
            returnStr = "java.lang.Long";
        } else if("DECIMAL".equals(columnType)){
            returnStr = "java.lang.Double";
        } else if("DATETIME".equals(columnType)){
            returnStr = "java.util.Date";
        }
        return returnStr;
    }

    /**
     * 【私有】初始化
     * @throws SQLException
     */
    private void ini() throws SQLException {
        DatabaseMetaData dbMetData = this.conn.getMetaData();
        // 根据表名提前表里面信息：
        ResultSet resultSet = dbMetData.getColumns(null, "%", this.tableName, "%");
        this.columnInfoVOList = new ArrayList<ColumnInfoVO>();
        while (resultSet.next()) {
            String columnName = resultSet.getString("COLUMN_NAME");
            String columnType = resultSet.getString("TYPE_NAME");
            int dataSize = resultSet.getInt("COLUMN_SIZE");
            String remarks = resultSet.getString("REMARKS");
            if(StringUtils.isNotBlank(remarks)){
                remarks = remarks.replaceAll("\n","<br/>").replaceAll("\\|","<br/>");
            }
            int nullable = resultSet.getInt("NULLABLE");
            int digits = resultSet.getInt("DECIMAL_DIGITS");

            ColumnInfoVO columnInfoVO = new ColumnInfoVO();
            columnInfoVO.setColumnName(columnName);
            columnInfoVO.setColumnType(columnType);
            columnInfoVO.setJavaFieldName(convertColumnNameToJavaFieldName(columnName));
            columnInfoVO.setJavaType(convertColumnTypeToJavaType(columnType));
            try {
                columnInfoVO.setSimpleJavaType(Class.forName(convertColumnTypeToJavaType(columnType)).getSimpleName());
            } catch (ClassNotFoundException e) {
                PublicUtil.printException(e);
            }
            columnInfoVO.setDataSize(dataSize);
            columnInfoVO.setRemarks(remarks);
            columnInfoVO.setNullable(nullable);
            columnInfoVO.setDigits(digits);
            this.columnInfoVOList.add(columnInfoVO);
        }
        resultSet.close();
    }

    /**
     * 获得Java类名
     * @return
     */
    public String getJavaClassName(){
        return convertColumnNameToJavaClassName(this.tableName);
    }

    //Getter
    public List<ColumnInfoVO> getColumnInfoVOList() {
        return columnInfoVOList;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableComment() {
        return tableComment;
    }
}
