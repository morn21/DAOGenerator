DAO生成：
1.主要用于生成DAO常用的 DO、QueryObj、DAO、DAOImpl、mapping.xml 五个文件
2.main方法在MainService中，可根据自己需求修改 MainService中的生成字符串
3.MainService中一次只针对一个数据表做生成，需要在MainService传入不同的构成参数，如：“doc_table”
4.daoGenerator.sql为样例数据库
5.生成的DO、QueryObj不带有Getter/Setter方法需要自己用开发工具生成
6.生成类不含导包