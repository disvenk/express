package com.xxx.utils;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wanghua on 17/1/19.
 */
public class ReflectUtils {

    /**
     * 根据类名称创建类实例
     *
     * @param className
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class cls = Class.forName(className);
        return cls.newInstance();
    }

    /**
     * 获取指定类的所有可见属性，包括基类属性
     *
     * @param superClass
     * @return
     */
    public static List<Field> getDeclaredFields(Class superClass) {
        List<Field> fieldList = new ArrayList();
        fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));  //getDeclaredField可以获得类中任何可见性的属性不包括基类，而getField只能获得public属性包括基类的public属性。如果需要获取基类某个非public属性的值，则只能通过反射来调用方法了。
        while ((superClass = superClass.getSuperclass()) != null)
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
        return fieldList;
    }

    /**
     * 将value转换为实际类型值
     *
     * @param field
     * @param value
     * @param isEnumConvertToString
     * @return
     * @throws Exception
     */
    public static Object parseActualTypeValue(Field field, Object value, boolean isEnumConvertToString) throws Exception {
        Object v = null;
        Type tp = field.getGenericType();

        //泛型类型。假如我们要想获得 Vector<Date> v = new Vector<Date>(); v的实际类型参数。我们不能通过v.getClass()这样的代码得到Vector<>里的参数化，只能通过其他途径得到。
        if (tp.getClass() == TypeVariableImpl.class) {
            //TODO 通过反射方式获取泛型值类型，还存在一定问题，先暂用 com.xxx.core.entity.GenericEntity 固定死！
            ParameterizedTypeImpl pti = (ParameterizedTypeImpl) Class.forName("com.xxx.core.entity.GenericEntity").getGenericSuperclass();
            if (pti == null) throw new Exception("无法获取泛型父类，com.xxx.core.entity.GenericEntity 可能已变更");
            Type[] actualTypeArguments = pti.getActualTypeArguments();
            if ("id,createdBy,updateBy".contains(field.getName())) {
                tp = actualTypeArguments[0];
            }
        }

        if (tp == String.class)
            v = (String) value;
        else if (tp == byte.class || tp == Byte.class)
            v = Byte.parseByte((String) value);
        else if (tp == short.class || tp == Short.class)
            v = Short.parseShort((String) value);
        else if (tp == int.class || tp == Integer.class)
            v = Integer.parseInt((String) value);
        else if (tp == long.class || tp == Long.class)
            v = Long.parseLong((String) value);
        else if (tp == float.class || tp == Float.class)
            v = Float.parseFloat((String) value);
        else if (tp == double.class || tp == Double.class)
            v = Double.parseDouble((String) value);
        else if (tp == boolean.class || tp == Boolean.class)
            v = Boolean.parseBoolean((String) value);
        else if (tp == java.util.Date.class)
            try {
                v = DateTimeUtils.convertToDate(((String) value).replace("\n", "").replace("\r", ""));
            } catch (Throwable ex) {
                throw new Exception("错误的日期格式：" + value);
            }
        else if (tp == java.sql.Date.class)
            try {
                v = (java.sql.Date) DateTimeUtils.convertToDate(((String) value).replace("\n", "").replace("\r", ""));
            } catch (Throwable ex) {
                throw new Exception("错误的日期格式：" + value);
            }
        else if (((Class) tp).isEnum()) {  //如果是 TypeVariableImpl 类型，tp 转 Class 会报错。
            if (isEnumConvertToString)  //枚举在数据库也有肯能用 int 存储的。
                v = (String) value;  //直接返回字符串
            else {
                if (value == null) v = null;
                else v = Enum.valueOf((Class) tp, value.toString());
            }
        } else {
            throw new Exception(MessageFormat.format("无法解析 {0} 属性类型", field.getName()));
        }
        return v;
    }

    /**
     * 寻找最靠近的带有泛型的父类
     *
     * @param cls
     * @return
     */
    public static ParameterizedTypeImpl closestGenericSuperclass(Class cls) {
        Type type = cls.getGenericSuperclass();
        while (!(type instanceof ParameterizedTypeImpl)) {
            cls = cls.getSuperclass();
            if (cls == null) return null;
            type = cls.getGenericSuperclass();
        }
        return (ParameterizedTypeImpl) type;
    }
}
