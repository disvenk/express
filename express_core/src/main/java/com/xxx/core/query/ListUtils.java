package com.xxx.core.query;

import java.util.*;

/**
 * Created by Administrator on 2017/3/25.
 */
public class ListUtils {

    /**
     * 去重
     *
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static <T> PageList<T> removeDuplicate(PageList<T> list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    /**
     * 保持顺序的去重
     * @param list
     * @param <T>
     * @return
     */
    public   static   <T> PageList<T>  removeDuplicateWithOrder(PageList<T>  list)   {
        Set set  =   new  HashSet();
        List newList  =   new ArrayList();
        for  (Iterator iter = list.iterator(); iter.hasNext();)   {
            Object element  =  iter.next();
            if  (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        return list;
//        System.out.println( " remove duplicate "   +  list);
    }

    public   static   <T> List<T>  removeDuplicateWithOrder(List<T>  list)   {
        Set set  =   new  HashSet();
        List newList  =   new ArrayList();
        for  (Iterator iter = list.iterator(); iter.hasNext();)   {
            Object element  =  iter.next();
            if  (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        return list;
//        System.out.println( " remove duplicate "   +  list);
    }
}
