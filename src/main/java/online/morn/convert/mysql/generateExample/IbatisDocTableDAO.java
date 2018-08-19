package online.morn.convert.mysql.generateExample;

/**
 * 第一个表格表
 * @author HornerJ 2018-08-19 13:15:56
 */
public class IbatisDocTableDAO {//extends SqlMapClientDaoSupport implements DocTableDAO {

	/*@Override
	public long insert(DocTableDO docTable) throws Exception {
		if (docTable == null) {
			throw new IllegalArgumentException("Can't insert a null data object into db.");
		}
		getSqlMapClientTemplate().insert("MS-DOC-TABLE-INSERT", docTable);
		return docTable.getId();
	}

	@Override
	public long deleteById(Long id) throws Exception {
		Map param = new HashMap();
		param.put("id", id);
		return getSqlMapClientTemplate().delete("MS-DOC-TABLE-DELETE-BY-ID", param);
	}

	@Override
	public int updateById(DocTableDO docTableLog) throws Exception {
		if (docTableLog == null) {
			throw new IllegalArgumentException("Can't update by a null data object.");
		}
		return getSqlMapClientTemplate().update("MS-DOC-TABLE-UPDATE-BY-ID", docTableLog);
	}

	@Override
	public List<DocTableDO> selectByCondition(DocTableQueryObj queryObj) throws Exception {
		if (queryObj == null) {
			throw new IllegalArgumentException("Can't select a null data object.");
		}
		return getSqlMapClientTemplate().queryForList("MS-DOC-TABLE-SELECT-BY-CONDITION", queryObj);
	}

	@Override
	public Integer selectCountByCondition(DocTableQueryObj queryObj) throws Exception {
		Integer retObj = (Integer) getSqlMapClientTemplate().queryForObject("MS-DOC-TABLE-SELECT-COUNT-BY-CONDITION", queryObj);
		if (retObj == null) {
			return 0;
		} else {
			return retObj.intValue();
		}
	}

	@Override
	public DocTableDO selectById(Long id) throws Exception {
		DocTableQueryObj queryObj = new DocTableQueryObj();
		queryObj.setId(id);
		List<DocTableDO> list = this.selectByCondition(queryObj);
		if(!CollectionUtils.isEmpty(list)){
			return list.get(0);
		}
		return null;
	}*/

}