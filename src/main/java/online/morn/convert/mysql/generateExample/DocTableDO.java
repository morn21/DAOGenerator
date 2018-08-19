package online.morn.convert.mysql.generateExample;

import java.util.Date;

/**
 * 第一个表格表
 * @author HornerJ 2018-08-19 13:15:56
 */
public class DocTableDO {
	private static final long serialVersionUID = 741231858441822688L;

	/**ID*/
	private Integer id;
	/**创建时间*/
	private Date createTime;
	/**修改时间*/
	private Date updateTime;
	/**标题*/
	private String docTitle;
	/**内容*/
	private String docContent;
}