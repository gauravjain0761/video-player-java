package com.app.videoplayer.db;


import com.app.videoplayer.entity.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * 类功能描述
 *
 * @author weiwen
 * @created 2022/2/11 6:03 PM
 */
public class CommonDaoUtils<T> {

    private DaoSession mDaoSession;
    private Class<T> entityClass;
    private AbstractDao<T, Long> entityDao;

    public CommonDaoUtils(Class<T> pEntityClass, AbstractDao<T, Long> pEntityDao) {
        mDaoSession = DbController.getInstance().getDaoSession();
        entityClass = pEntityClass;
        entityDao = pEntityDao;
    }

    /**
     * Insert records, creating tables first if they are not already created
     */
    public boolean insert(T pEntity) {
        return entityDao.insert(pEntity) != -1;
    }

    /**
     * Insert or update a record
     */
    public long insertOrReplace(T pEntity) {
        return mDaoSession.insertOrReplace(pEntity);
    }

    /**
     * Insert multiple pieces of data in the child thread operation
     */
    public boolean insertMultiple(final List<T> pEntityList) {
        try {
            mDaoSession.runInTx(() -> {
                for (T entity : pEntityList) {
                    mDaoSession.insertOrReplace(entity);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Modifying a piece of data
     */
    public boolean update(T entity) {
        try {
            mDaoSession.update(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Modifying multiple data
     *
     * @param pEntityList
     * @return
     */
    public boolean updateMultiple(List<T> pEntityList) {
        try {
            mDaoSession.runInTx(() -> {
                for (T entity : pEntityList) {
                    mDaoSession.update(entity);
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Example Delete a single record
     */
    public boolean delete(T entity) {
        try {
            //按照id删除
            mDaoSession.delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByKey(Long key) {
        try {
            entityDao.deleteByKey(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all records
     */
    public boolean deleteAll() {
        try {
            //按照id删除
            mDaoSession.deleteAll(entityClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete list of records
     *
     * @param entries
     * @return
     */
    public boolean deleteItems(T... entries) {
        try {
            entityDao.deleteInTx(entries);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Query all records
     */
    public List<T> queryAll() {
        return mDaoSession.loadAll(entityClass);
    }

    /**
     * Query records by primary key id
     */
    public T queryById(long key) {
        return mDaoSession.load(entityClass, key);
    }


    /**
     * The query operation is performed using native sql
     */
    public List<T> queryByNativeSql(String sql, String[] conditions) {
        return mDaoSession.queryRaw(entityClass, sql, conditions);
    }

    /**
     * Use queryBuilder for queries
     */
    public List<T> queryByQueryBuilder(WhereCondition cond, WhereCondition... condMore) {
        QueryBuilder<T> queryBuilder = mDaoSession.queryBuilder(entityClass);
        return queryBuilder.where(cond, condMore).list();
    }


    /**
     * Queries using queryBuilder (including ascending and descending order) limit the number of queries
     *
     * @param orderBy    0: ascending sequence 1: descending sequence
     * @param limit      Number of query items
     * @param properties
     * @return
     */
    public List<T> queryAllByOrderLimit(int orderBy, int limit, Property... properties) {
        QueryBuilder<T> queryBuilder = mDaoSession.queryBuilder(entityClass).limit(limit);
        if (orderBy == 0) {
            return queryBuilder.orderAsc(properties).list();
        } else {
            return queryBuilder.orderDesc(properties).list();
        }
    }

    /**
     * Queries using queryBuilder (both ascending and descending)
     *
     * @param orderBy    0: ascending sequence 1: descending sequence
     * @param properties field
     * @return
     */
    public List<T> queryAllByOrderByBuilder(int orderBy, WhereCondition cond, WhereCondition condMore, Property... properties) {
        QueryBuilder<T> queryBuilder = mDaoSession.queryBuilder(entityClass).where(cond, condMore);
        if (orderBy == 0) {
            return queryBuilder.orderAsc(properties).list();
        } else {
            return queryBuilder.orderDesc(properties).list();
        }
    }
}
