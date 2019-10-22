package com.xxx.core.persist.session;

import com.xxx.core.persist.respository.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 * Created by wanghua on 17/1/19.
 */
public class MybatisSessionSupport extends SqlSessionDaoSupport implements SessionSupport<SqlSession> {
    public static SqlSessionTemplate sqlSessionTemplate = MybatisUtils.sqlSessionTemplate;  //分装了部分 session 操作，springMvc中可直接使用它
    public static SqlSessionTemplate sqlSessionReadonlyTemplate = MybatisUtils.sqlSessionTemplate;
    private SqlSession sqlSession;  //适用于 web+其他线程，创建其他线程时请用 setManualSession()
    boolean isManual = false;


    /**
     * 创建一个线程（仅用于独立线程中）
     * @param readonly
     */
    @Override
    public void openThreadSession(boolean readonly) {
        if (readonly)
            this.sqlSession = SqlSessionUtils.getSqlSession(sqlSessionReadonlyTemplate.getSqlSessionFactory());
        else
            this.sqlSession = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory());
        isManual = true;
    }

    /**
     * 注意：Spring 框架下最好直接使用单例 MybatisUtils.sqlSessionTemplate
     *
     * @return SqlSession
     */
    @Override
    public SqlSession getCurrentSession() {
        if (!isManual)
            sqlSession = getSqlSession();
        return sqlSession;
    }

    @Override
    public void close() {
        if (sqlSession != null)
            sqlSession.close();
    }
}
