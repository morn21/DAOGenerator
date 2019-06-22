package online.morn.convert.mysql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import online.morn.convert.mysql.vo.ColumnInfoVO;
import org.apache.maven.shared.utils.StringUtils;

/**
 * 主服务
 */
public class MainService {
    /**用于存储生成文件的目录*/
    private final String filePath = "c:/daoGenerator/";
    /**作者*/
    private final String author = "HornerJ";
    /**DO包路径*/
    private final String doPackagePath = "online.morn.convert.mysql.generateExample";
    /**QueryObj包路径*/
    private final String queryObjPackagePath = "online.morn.convert.mysql.generateExample";
    /**DAO包路径*/
    private final String daoInterfacePackagePath = "online.morn.convert.mysql.generateExample";
    /**DAO实现包路径*/
    private final String daoImplPackagePath = "online.morn.convert.mysql.generateExample";
    /**时间字段名列表*/
    private final List<String> timeFieldNameList = Arrays.asList(new String[]{"createTime","updateTime"});

    /**表格对象*/
    private TableObject tableObject;
    /**当前时间字符串*/
    private String nowTimeStr;

    /**
     * 构造方法
     * @param tableName
     */
    public MainService(String tableName){
        this.tableObject = new TableObject(DBManager.getConn(),tableName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.nowTimeStr = sdf.format(new Date());
    }

    /**
     * 获得表markdown结构
     * @return
     */
    public String getMarkdown(){
        StringBuffer returnBuffer = new StringBuffer();
        //表名
        returnBuffer.append(this.tableObject.getTableName());
        if(StringUtils.isNotBlank(this.tableObject.getTableComment())){
            returnBuffer.append("（").append(this.tableObject.getTableComment()).append("）");
        }
        //表头
        returnBuffer.append("| 字段名 | 类型 | 长度 | 含义 | 主外键 | 默认值 | 允许NULL |\n");
        returnBuffer.append("| ----- | ----- | ----- | ----- | ----- | ----- | ----- |\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            returnBuffer.append("| ");
            returnBuffer.append(columnInfoVO.getColumnName());//字段名
            returnBuffer.append(" | ");
            returnBuffer.append(columnInfoVO.getColumnType());//类型
            returnBuffer.append(" | ");
            returnBuffer.append(columnInfoVO.getDataSize());//长度
            returnBuffer.append(" | ");
            returnBuffer.append(columnInfoVO.getRemarks());//含义
            returnBuffer.append(" | ");
            if(columnInfoVO.getColumnName().equals("id")){
                returnBuffer.append("主键");//主外键
            } else if(columnInfoVO.getColumnName().endsWith("_id")){
                returnBuffer.append("外键");//主外键
            }
            returnBuffer.append(" | ");
            returnBuffer.append(" | ");
            if(columnInfoVO.getNullable() == 0){
                returnBuffer.append("不允许");//允许NULL
            } else {
                returnBuffer.append("允许");//允许NULL
            }
            returnBuffer.append(" |\n");
        }
        return returnBuffer.toString();
    }

    /**
     * 获得其它配置信息
     * @return
     */
    public String getOtherConfigInfo(){
        String xmlName = this.tableObject.getJavaClassName() + "-sqlmap-mapping.xml";
        String javaClassName =  this.tableObject.getJavaClassName();
        String javaClassNameLower = PublicUtil.lowerCaseFirst(javaClassName);
        String daoImplName = "Ibatis" + javaClassName + "DAO";

        StringBuffer returnBuffer = new StringBuffer();
        returnBuffer.append("<sqlMap resource=\"sqlmap/auto/" + xmlName + "\"/>");
        returnBuffer.append("<!-- " + this.nowTimeStr + " -->\n");
        returnBuffer.append("<bean id=\"" + javaClassNameLower + "DAO\" class=\"" + this.daoImplPackagePath + "." + daoImplName + "\" parent=\"baseSqlMapClientDAO\"/>");
        returnBuffer.append("<!-- " + this.nowTimeStr + " -->\n");
        return returnBuffer.toString();
    }

    /**
     * 【私有】获得类注释
     * @return
     */
    private String getClassAnnotation(){
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("/**\n");
        strBuffer.append(" * ").append(this.tableObject.getTableComment()).append("\n");
        strBuffer.append(" * @author ").append(this.author).append(" ").append(this.nowTimeStr).append("\n");
        strBuffer.append(" */\n");
        return strBuffer.toString();
    }

    /**
     * 生成DO模型
     */
    public void generateDOModel(){
        String doName = this.tableObject.getJavaClassName() + "DO";
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("package " + this.doPackagePath + ";\n\n");
        strBuffer.append(this.getClassAnnotation());
        strBuffer.append("public class " + doName + " {\n");
        strBuffer.append("\tprivate static final long serialVersionUID = 741231858441822688L;\n\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            strBuffer.append("\t/**" + columnInfoVO.getRemarks() + "*/\n");
            strBuffer.append("\tprivate " + columnInfoVO.getSimpleJavaType() + " " + columnInfoVO.getJavaFieldName() + ";\n");
        }
        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + doName + ".java",strBuffer.toString());
    }

    /**
     * 生成查询对象模型
     */
    public void generateQueryObjModel(){
        String queryObjName = this.tableObject.getJavaClassName() + "QueryObj";
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("package " + this.queryObjPackagePath + ";\n\n");
        strBuffer.append(this.getClassAnnotation());
        strBuffer.append("public class " + queryObjName + " {\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t/**" + columnInfoVO.getRemarks() + "*/\n");
            strBuffer.append("\tprivate " + columnInfoVO.getSimpleJavaType() + " " + columnInfoVO.getJavaFieldName() + ";\n");
        }
        strBuffer.append("\t/**页号*/\n");
        strBuffer.append("\tprivate Integer currentPage;\n");
        strBuffer.append("\t/**页长*/\n");
        strBuffer.append("\tprivate Integer pageSize;\n");
        strBuffer.append("\n");
        strBuffer.append("\tpublic int getStart() {\n");
        strBuffer.append("\t\tint start = (currentPage - 1) * pageSize;\n");
        strBuffer.append("\t\treturn start;\n");
        strBuffer.append("\t}\n\n");
        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + queryObjName + ".java",strBuffer.toString());
    }

    /**
     * 生成DAO接口
     */
    public void generateDAOInterface(){
        String javaClassName =  this.tableObject.getJavaClassName();
        String javaClassNameLower = PublicUtil.lowerCaseFirst(javaClassName);
        String daoInterfaceName = javaClassName + "DAO";
        String atAuthor = "@author " + this.author + " " + this.nowTimeStr;

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("package " + this.daoInterfacePackagePath + ";\n\n");
        strBuffer.append(this.getClassAnnotation());
        strBuffer.append("public interface " + daoInterfaceName + " {\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 添加\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param " + javaClassNameLower + "\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic long insert(" + javaClassName + "DO " + javaClassNameLower + ") throws Exception;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID删除\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param id\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic long deleteById(Long id) throws Exception;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID修改\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param " + javaClassNameLower + "\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic int updateById(" + javaClassName + "DO " + javaClassNameLower + ") throws Exception;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据条件查询\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param queryObj\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic List<" + javaClassName + "DO> selectByCondition(" + javaClassName + "QueryObj queryObj) throws Exception;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据条件查数量\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param queryObj\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic Integer selectCountByCondition(" + javaClassName + "QueryObj queryObj) throws Exception;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID查单条\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param id\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t * @throws Exception\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tpublic " + javaClassName + "DO selectById(Long id) throws Exception;\n\n");

        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + daoInterfaceName + ".java",strBuffer.toString());
    }

    /**
     * 生成DAO接口实现
     */
    public void generateDAOInterfaceImpl(){
        String upperCaseTableName = this.tableObject.getTableName().toUpperCase().replaceAll("_","-");
        String javaClassName =  this.tableObject.getJavaClassName();
        String javaClassNameLower = PublicUtil.lowerCaseFirst(javaClassName);
        String daoImplName = "Ibatis" + javaClassName + "DAO";

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("package " + this.daoImplPackagePath + ";\n\n");
        strBuffer.append(this.getClassAnnotation());
        strBuffer.append("public class " + daoImplName + " extends SqlMapClientDaoSupport implements " + javaClassName + "DAO {\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic long insert(" + javaClassName + "DO " + javaClassNameLower + ") throws Exception {\n");
        strBuffer.append("\t\tif (" + javaClassNameLower + " == null) {\n");
        strBuffer.append("\t\t\tthrow new IllegalArgumentException(\"Can't insert a null data object into db.\");\n");
        strBuffer.append("\t\t}\n");
        strBuffer.append("\t\tgetSqlMapClientTemplate().insert(\"MS-" + upperCaseTableName + "-INSERT\", " + javaClassNameLower + ");\n");
        strBuffer.append("\t\treturn " + javaClassNameLower + ".getId();\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic long deleteById(Long id) throws Exception {\n");
        strBuffer.append("\t\tMap param = new HashMap();\n");
        strBuffer.append("\t\tparam.put(\"id\", id);\n");
        strBuffer.append("\t\treturn getSqlMapClientTemplate().delete(\"MS-" + upperCaseTableName + "-DELETE-BY-ID\", param);\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic int updateById(" + javaClassName + "DO " + javaClassNameLower + "Log) throws Exception {\n");
        strBuffer.append("\t\tif (" + javaClassNameLower + "Log == null) {\n");
        strBuffer.append("\t\t\tthrow new IllegalArgumentException(\"Can't update by a null data object.\");\n");
        strBuffer.append("\t\t}\n");
        strBuffer.append("\t\treturn getSqlMapClientTemplate().update(\"MS-" + upperCaseTableName + "-UPDATE-BY-ID\", " + javaClassNameLower + "Log);\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic List<" + javaClassName + "DO> selectByCondition(" + javaClassName + "QueryObj queryObj) throws Exception {\n");
        strBuffer.append("\t\tif (queryObj == null) {\n");
        strBuffer.append("\t\t\tthrow new IllegalArgumentException(\"Can't select a null data object.\");\n");
        strBuffer.append("\t\t}\n");
        strBuffer.append("\t\treturn getSqlMapClientTemplate().queryForList(\"MS-" + upperCaseTableName + "-SELECT-BY-CONDITION\", queryObj);\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic Integer selectCountByCondition(" + javaClassName + "QueryObj queryObj) throws Exception {\n");
        strBuffer.append("\t\tInteger retObj = (Integer) getSqlMapClientTemplate().queryForObject(\"MS-" + upperCaseTableName + "-SELECT-COUNT-BY-CONDITION\", queryObj);\n");
        strBuffer.append("\t\tif (retObj == null) {\n");
        strBuffer.append("\t\t\treturn 0;\n");
        strBuffer.append("\t\t} else {\n");
        strBuffer.append("\t\t\treturn retObj.intValue();\n");
        strBuffer.append("\t\t}\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("\t@Override\n");
        strBuffer.append("\tpublic " + javaClassName + "DO selectById(Long id) throws Exception {\n");
        strBuffer.append("\t\t" + javaClassName + "QueryObj queryObj = new " + javaClassName + "QueryObj();\n");
        strBuffer.append("\t\tqueryObj.setId(id);\n");
        strBuffer.append("\t\tList<" + javaClassName + "DO> list = this.selectByCondition(queryObj);\n");
        strBuffer.append("\t\tif(!CollectionUtils.isEmpty(list)){\n");
        strBuffer.append("\t\t\treturn list.get(0);\n");
        strBuffer.append("\t\t}\n");
        strBuffer.append("\t\treturn null;\n");
        strBuffer.append("\t}\n\n");

        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + daoImplName + ".java",strBuffer.toString());
    }

    /**
     * 生成MyBatisXml
     */
    public void generateMyBatisXml(){
        String upperCaseTableName = this.tableObject.getTableName().toUpperCase().replaceAll("_","-");
        String doPath = this.doPackagePath + "." + this.tableObject.getJavaClassName() + "DO";
        String daoImplName = "Ibatis" + this.tableObject.getJavaClassName() + "DAO";

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>\n");
        strBuffer.append("<!DOCTYPE sqlMap PUBLIC \"-//iBATIS.com//DTD SQL Map 2.0//EN\" \"http://www.ibatis.com/dtd/sql-map-2.dtd\">\n\n");

        //SQLMap
        strBuffer.append("<sqlMap namespace=\"iwallet\">\n\n");
        strBuffer.append("\t<!-- result maps for database table " + this.tableObject.getTableName() + " -->\n");
        strBuffer.append("\t<resultMap id=\"RM-" + upperCaseTableName + "\" class=\"" + doPath + "\">\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            strBuffer.append("\t\t<result "
                + "property=\"" + columnInfoVO.getJavaFieldName() +"\" "
                + "column=\"" + columnInfoVO.getColumnName() + "\" "
                + "javaType=\"" + columnInfoVO.getJavaType() + "\" "
                + "jdbcType=\"" + columnInfoVO.getColumnType() + "\" />");
            strBuffer.append("<!-- " + columnInfoVO.getRemarks() +" -->\n");
        }
        strBuffer.append("\t</resultMap>\n\n");

        //insert
        List<String> columnList = new ArrayList<String>();
        List<String> valuesList = new ArrayList<String>();
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            columnList.add(columnInfoVO.getColumnName());
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                valuesList.add("CURRENT_TIMESTAMP");
            } else {
                valuesList.add("#" + columnInfoVO.getJavaFieldName() + "#");
            }
        }
        strBuffer.append("\t<!-- mapped statement for " + daoImplName + ".insert -->\n");
        strBuffer.append("\t<insert id=\"MS-" + upperCaseTableName + "-INSERT\">\n");
        strBuffer.append("\t\tinsert /*MS-" + upperCaseTableName + "-INSERT*/ into " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t(" + Joiner.on(",").join(columnList) + ")\n");
        strBuffer.append("\t\tvalues\n");
        strBuffer.append("\t\t(" + Joiner.on(",").join(valuesList) + ")\n");
        strBuffer.append("\t\t<selectKey resultClass=\"long\" keyProperty=\"id\">SELECT LAST_INSERT_ID() AS ID</selectKey>\n");
        strBuffer.append("\t\t<selectKey resultClass=\"long\" keyProperty=\"id\">\n");
        strBuffer.append("\t\tselect last_insert_id() as id\n");
        strBuffer.append("\t\t</selectKey>\n");
        strBuffer.append("\t</insert>\n\n");

        //deleteById
        strBuffer.append("\t<!-- mapped statement for " + daoImplName + ".deleteById -->\n");
        strBuffer.append("\t<delete id=\"MS-" + upperCaseTableName + "-DELETE-BY-ID\" >\n");
        strBuffer.append("\t\tdelete /*MS-" + upperCaseTableName + "-DELETE-BY-ID*/\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\twhere id = #id#\n");
        strBuffer.append("\t</delete>\n\n");

        //updateById
        strBuffer.append("\t<!-- mapped statement for " + daoImplName + ".updateById -->\n");
        strBuffer.append("\t<update id=\"MS-" + upperCaseTableName + "-UPDATE-BY-ID\">\n");
        strBuffer.append("\t\tupdate /*MS-" + upperCaseTableName + "-UPDATE-BY-ID*/ " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\tset gmt_modified = CURRENT_TIMESTAMP\n");
        strBuffer.append("\t\t<dynamic>\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<isNotEmpty prepend=\",\" property=\"" + columnInfoVO.getJavaFieldName() + "\">\n");
            strBuffer.append("\t\t\t\t" + columnInfoVO.getColumnName() + " = #" + columnInfoVO.getJavaFieldName() + "#\n");
            strBuffer.append("\t\t\t</isNotEmpty>\n");
        }
        strBuffer.append("\t\t</dynamic>\n");
        strBuffer.append("\t\twhere id = #id#\n");
        strBuffer.append("\t</update>\n\n");

        //selectByCondition
        strBuffer.append("\t<!-- mapped statement for " + daoImplName + ".selectByCondition -->\n");
        strBuffer.append("\t<select id=\"MS-" + upperCaseTableName + "-SELECT-BY-CONDITION\" resultMap=\"RM-" + upperCaseTableName + "\">\n");
        strBuffer.append("\t\tselect /*MS-" + upperCaseTableName + "-SELECT-BY-CONDITION*/\n");
        strBuffer.append("\t\t\t" + Joiner.on(",").join(columnList) + "\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t<dynamic prepend=\"where\">\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<isNotEmpty prepend=\"AND\" property=\"" + columnInfoVO.getJavaFieldName() + "\">\n");
            strBuffer.append("\t\t\t\t" + columnInfoVO.getColumnName() + " = #" + columnInfoVO.getJavaFieldName() + "#\n");
            strBuffer.append("\t\t\t</isNotEmpty>\n");
        }
        strBuffer.append("\t\t</dynamic>\n");
        strBuffer.append("\t\torder by id desc\n");
        strBuffer.append("\t\t<dynamic>\n");
        strBuffer.append("\t\t\t<isNotEmpty property=\"currentPage\">\n");
        strBuffer.append("\t\t\t\tlimit #start#,#pageSize#\n");
        strBuffer.append("\t\t\t</isNotEmpty>\n");
        strBuffer.append("\t\t</dynamic>\n");
        strBuffer.append("\t</select>\n\n");

        //selectCountByCondition
        strBuffer.append("\t<!-- mapped statement for " + daoImplName + ".selectCountByCondition -->\n");
        strBuffer.append("\t<select id=\"MS-" + upperCaseTableName + "-SELECT-COUNT-BY-CONDITION\" resultClass=\"int\">\n");
        strBuffer.append("\t\tselect /*MS-" + upperCaseTableName + "-SELECT-COUNT-BY-CONDITION*/\n");
        strBuffer.append("\t\t\tcount(1)\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t<dynamic prepend=\"where\">\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<isNotEmpty prepend=\"AND\" property=\"" + columnInfoVO.getJavaFieldName() + "\">\n");
            strBuffer.append("\t\t\t\t" + columnInfoVO.getColumnName() + " = #" + columnInfoVO.getJavaFieldName() + "#\n");
            strBuffer.append("\t\t\t</isNotEmpty>\n");
        }
        strBuffer.append("\t\t</dynamic>\n");
        strBuffer.append("\t</select>\n\n");

        strBuffer.append("</sqlMap>");

        PublicUtil.writeFile(this.filePath + this.tableObject.getJavaClassName() + "-sqlmap-mapping.xml",strBuffer.toString());
    }

    public static void main(String[] args){
        MainService mainService = new MainService("doc_table");

        PublicUtil.println("----------------------------------------");
        //获得表markdown结构
        PublicUtil.println(mainService.getMarkdown());
        PublicUtil.println("----------------------------------------");
        //获得其它配置信息
        PublicUtil.println(mainService.getOtherConfigInfo());
        PublicUtil.println("----------------------------------------");
        //生成DO模型
        mainService.generateDOModel();

        //生成查询对象模型
        mainService.generateQueryObjModel();

        //生成DAO接口
        mainService.generateDAOInterface();

        //生成DAO接口实现
        mainService.generateDAOInterfaceImpl();

        //生成MyBatisXml
        mainService.generateMyBatisXml();

        PublicUtil.println("----------------------------------------");
    }
}
