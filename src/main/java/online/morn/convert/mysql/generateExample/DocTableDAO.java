package online.morn.convert.mysql.generateExample;

import java.util.List;

/**
 * 第一个表格表
 * @author HornerJ 2018-08-19 13:15:56
 */
public interface DocTableDAO {
	/**
	 * 添加
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param docTable
	 * @return
	 * @throws Exception
	 */
	public long insert(DocTableDO docTable) throws Exception;

	/**
	 * 根据ID删除
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public long deleteById(Long id) throws Exception;

	/**
	 * 根据ID修改
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param docTable
	 * @return
	 * @throws Exception
	 */
	public int updateById(DocTableDO docTable) throws Exception;

	/**
	 * 根据条件查询
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param queryObj
	 * @return
	 * @throws Exception
	 */
	public List<DocTableDO> selectByCondition(DocTableQueryObj queryObj) throws Exception;

	/**
	 * 根据条件查数量
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param queryObj
	 * @return
	 * @throws Exception
	 */
	public Integer selectCountByCondition(DocTableQueryObj queryObj) throws Exception;

	/**
	 * 根据ID查单条
	 * @author HornerJ 2018-08-19 13:15:56
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DocTableDO selectById(Long id) throws Exception;

}