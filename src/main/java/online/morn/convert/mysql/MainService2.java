package online.morn.convert.mysql;

import com.google.common.base.Joiner;
import online.morn.convert.mysql.vo.ColumnInfoVO;
import org.apache.maven.shared.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 主服务
 */
public class MainService2 {
    /**用于存储生成文件的目录*/
    private final String filePath = "c:/daoGenerator/";
    /**作者*/
    private final String author = "HornerJ";
    /**DO包路径*/
    private final String doPackagePath = "online.morn.timingTasks.dao.DO";
    /**QueryObj包路径*/
    private final String queryObjPackagePath = "online.morn.timingTasks.dao.queryObj";
    /**DAO包路径*/
    private final String daoInterfacePackagePath = "online.morn.timingTasks.dao.mapper";

    /**时间字段名列表*/
    private final List<String> timeFieldNameList = Arrays.asList(new String[]{"create_time","modify_time"});

    /**表格对象*/
    private TableObject tableObject;
    /**当前时间字符串*/
    private String nowTimeStr;

    /**
     * 构造方法
     * @param tableName
     */
    public MainService2(String tableName){
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
        strBuffer.append("\n\t//Getter & Setter\n");
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
        strBuffer.append("\t//Getter & Setter\n");
        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + queryObjName + ".java",strBuffer.toString());
    }

    /**
     * 生成DAO接口
     */
    public void generateDAOInterface(){
        String javaClassName =  this.tableObject.getJavaClassName();
        String javaClassNameLower = PublicUtil.lowerCaseFirst(javaClassName);
        String daoInterfaceName = javaClassName + "Mapper";
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
        strBuffer.append("\t */\n");
        strBuffer.append("\tInteger insert(" + javaClassName + "DO " + javaClassNameLower + ");\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID删除\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param id\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tInteger deleteById(Long id);\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID修改\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param " + javaClassNameLower + "\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tInteger updateById(" + javaClassName + "DO " + javaClassNameLower + ") ;\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据条件查询\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param queryObj\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tList<" + javaClassName + "DO> selectByQueryObj(" + javaClassName + "QueryObj queryObj);\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据条件查数量\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param queryObj\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\tInteger selectCountByQueryObj(" + javaClassName + "QueryObj queryObj);\n\n");

        strBuffer.append("\t/**\n");
        strBuffer.append("\t * 根据ID查单条\n");
        strBuffer.append("\t * " + atAuthor + "\n");
        strBuffer.append("\t * @param id\n");
        strBuffer.append("\t * @return\n");
        strBuffer.append("\t */\n");
        strBuffer.append("\t" + javaClassName + "DO selectById(Long id);\n\n");

        strBuffer.append("}");
        PublicUtil.writeFile(this.filePath + daoInterfaceName + ".java",strBuffer.toString());
    }

    /**
     * 生成MyBatisXml
     */
    public void generateMyBatisXml(){
        String upperCaseTableName = this.tableObject.getTableName().toUpperCase().replaceAll("_","-");
        String doPath = this.doPackagePath + "." + this.tableObject.getJavaClassName() + "DO";
        String mapperPath = this.daoInterfacePackagePath + "." + this.tableObject.getJavaClassName() + "Mapper";
        //String daoImplName = "Ibatis" + this.tableObject.getJavaClassName() + "DAO";

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        strBuffer.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");

        //SQLMap
        strBuffer.append("<mapper namespace=\"" + mapperPath + "\">\n\n");

        strBuffer.append("\t<!--resultMap-->\n");
        strBuffer.append("\t<resultMap id=\"RESULT-MAP\" type=\"" + doPath + "\">\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if("id".equalsIgnoreCase(columnInfoVO.getJavaFieldName())) {
                strBuffer.append("\t\t<id property=\"" + columnInfoVO.getJavaFieldName() + "\" column=\"" + columnInfoVO.getColumnName() + "\"/>\n");
            } else {
                strBuffer.append("\t\t<result property=\"" + columnInfoVO.getJavaFieldName() + "\" column=\"" + columnInfoVO.getColumnName() + "\"/>\n");
            }
            strBuffer.append("<!-- " + columnInfoVO.getRemarks() +" -->\n");
        }
        strBuffer.append("\t</resultMap>\n\n");

        //insert
        List<String> columnList = new ArrayList<String>();
        List<String> valuesList = new ArrayList<String>();
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            columnList.add(columnInfoVO.getColumnName());
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                valuesList.add("now()");
            } else {
                valuesList.add("#{" + columnInfoVO.getJavaFieldName() + "}");
            }
        }
        strBuffer.append("\t<!--insert-->\n");
        strBuffer.append("\t<insert id=\"insert\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n");
        strBuffer.append("\t\tinsert into " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t(" + Joiner.on(",").join(columnList) + ")\n");
        strBuffer.append("\t\tvalues\n");
        strBuffer.append("\t\t(" + Joiner.on(",").join(valuesList) + ")\n");
        strBuffer.append("\t</insert>\n\n");

        //deleteById
        strBuffer.append("\t<!--deleteById-->\n");
        strBuffer.append("\t<delete id=\"deleteById\">\n");
        strBuffer.append("\t\tdelete from " + this.tableObject.getTableName() + "where id = #{id}\n");
        strBuffer.append("\t</delete>\n\n");

        //updateById
        strBuffer.append("\t<!--updateById-->\n");
        strBuffer.append("\t<update id=\"updateById\">\n");
        strBuffer.append("\t\tupdate " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t<set>\n");
        strBuffer.append("\t\t\t<if test=\"true\">modify_time = now(),</if>\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<if test=\"" + columnInfoVO.getJavaFieldName() + " != null\">" +
                    columnInfoVO.getColumnName() + " = #{" + columnInfoVO.getJavaFieldName() + "},</if>\n");
        }
        strBuffer.append("\t\t</set>\n");
        strBuffer.append("\t\twhere id = #{id}\n");
        strBuffer.append("\t</update>\n\n");

        //selectByQueryObj
        strBuffer.append("\t<!--selectByQueryObj-->\n");
        strBuffer.append("\t<select id=\"selectByQueryObj\" resultMap=\"RESULT-MAP\">\n");
        strBuffer.append("\t\tselect " + Joiner.on(",").join(columnList) + "\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t<where>\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<if test=\"" + columnInfoVO.getJavaFieldName() + " != null\">" +
                    "and " + columnInfoVO.getColumnName() + " = #{" + columnInfoVO.getJavaFieldName()  + "}</if>\n");
        }
        strBuffer.append("\t\t</where>\n");
        strBuffer.append("\t\torder by id desc\n");
        strBuffer.append("\t\t<if test=\"currentPage != null\">limit #{start},#{pageSize}</if>\n");
        strBuffer.append("\t</select>\n\n");

        //selectCountByQueryObj
        strBuffer.append("\t<!--selectCountByQueryObj-->\n");
        strBuffer.append("\t<select id=\"selectCountByQueryObj\" resultType=\"int\">\n");
        strBuffer.append("\t\tselect count(1)\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\t<where>\n");
        for(ColumnInfoVO columnInfoVO : this.tableObject.getColumnInfoVOList()){
            if(this.timeFieldNameList.contains(columnInfoVO.getColumnName())){
                continue;
            }
            strBuffer.append("\t\t\t<if test=\"" + columnInfoVO.getJavaFieldName() + " != null\">" +
                    "and " + columnInfoVO.getColumnName() + " = #{" + columnInfoVO.getJavaFieldName()  + "}</if>\n");
        }
        strBuffer.append("\t\t</where>\n");
        strBuffer.append("\t</select>\n\n");

        //selectById
        strBuffer.append("\t<!--selectById-->\n");
        strBuffer.append("\t<select id=\"selectById\" resultMap=\"RESULT-MAP\">\n");
        strBuffer.append("\t\tselect " + Joiner.on(",").join(columnList) + "\n");
        strBuffer.append("\t\tfrom " + this.tableObject.getTableName() + "\n");
        strBuffer.append("\t\twhere id=#{id}\n");
        strBuffer.append("\t</select>\n\n");

        strBuffer.append("</sqlMap>");

        PublicUtil.writeFile(this.filePath + this.tableObject.getJavaClassName() + "-sqlmap-mapping.xml",strBuffer.toString());
    }

    public static void main(String[] args){
        MainService2 mainService = new MainService2("doc_table");

        PublicUtil.println("----------------------------------------");
        //获得表markdown结构
        PublicUtil.println(mainService.getMarkdown());
        PublicUtil.println("----------------------------------------");
        //生成DO模型
        mainService.generateDOModel();

        //生成查询对象模型
        mainService.generateQueryObjModel();

        //生成DAO接口
        mainService.generateDAOInterface();

        //生成MyBatisXml
        mainService.generateMyBatisXml();

        PublicUtil.println("----------------------------------------");
    }
}
