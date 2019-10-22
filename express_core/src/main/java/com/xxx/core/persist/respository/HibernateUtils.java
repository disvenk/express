package com.xxx.core.persist.respository;

import com.xxx.core.entity.GenericEntity;
import com.xxx.core.spring.SpringContext;
import org.springframework.orm.hibernate5.HibernateTemplate;

import java.util.Iterator;
import java.util.List;

public class HibernateUtils {
    public static HibernateTemplate hibernateTemplate;
    public static HibernateTemplate hibernateReadonlyTemplate;

    static {
        try {
            hibernateTemplate = (HibernateTemplate) SpringContext.getApplicationContext().getBean("hibernateTemplate");
        } catch (Throwable e) {
            throw new RuntimeException("无法获取Bean：hibernateTemplate");
        }

        try {
            hibernateReadonlyTemplate = (HibernateTemplate) SpringContext.getApplicationContext().getBean("hibernateReadonlyTemplate");
        } catch (Throwable e) {
            throw new RuntimeException("无法获取Bean：hibernateReadonlyTemplate");
        }
    }

    /**
     * 移除ID重复的实体
     * @param list
     * @param <G>
     * @param <T>
     * @return
     */
    public static  <G extends GenericEntity,T extends List<G>>  T removeRepeatElement(T list) {
        Iterator<G> iter = list.iterator();
        while (iter.hasNext()) {
            GenericEntity genericEntity = iter.next();
            int a = 0; //记录相同元素出现次数
            for (GenericEntity g : list) {
                if (genericEntity.getId().equals(g.getId())) {
                    a++;
                    if(a == 2) {
                        iter.remove();
                        break;
                    }
                }
            }
        }
        return list;
    }
}
