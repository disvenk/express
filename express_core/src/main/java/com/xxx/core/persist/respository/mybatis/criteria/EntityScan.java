package com.xxx.core.persist.respository.mybatis.criteria;

import com.xxx.core.spring.SpringContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EntityScan {

    private String packageName;

    private static Map<Class<?>, ClassMapper> classMappers;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static Map<Class<?>, ClassMapper> getClassMappers() {
        return classMappers;
    }

    /**
     * 扫描指定包下的所有 Class
     * 提示：如果含有 @Entity、@Table 注解，则转化成 ClassMapper，并存储 classMappers 集合。
     */
    public void scan() {
        classMappers = new HashMap<>();
        Assert.notNull(packageName, "包名不能为空");
        List<Class<?>> list = getClasses(packageName);
        for (Class clazz : list) {
            Entity entity = AnnotationUtils.findAnnotation(clazz, Entity.class);   //获取 Entity 注解
            Table table = AnnotationUtils.findAnnotation(clazz, Table.class);      //获取 Table 注解
            if (entity != null && table != null) {
                final ClassMapper classMapper = new ClassMapper();
                String tableName = table.name();
                classMapper.setTableName(tableName);
                classMapper.setClassName(clazz.getName());
                classMapper.setPackageName(clazz.getPackage().getName());
                classMapper.setFieldMappers(new ArrayList<FieldMapper>());
                classMapper.setQueryMappers(new ArrayList<FieldMapper>());
                classMapper.setColumnMap(new HashMap<String, String>());
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {  //遍历 Class 下的所有 Field 执行相关操作
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        Annotation[] anns = field.getDeclaredAnnotations();
                        if (anns != null) {
                            for (Annotation ann : anns) {
                                if (ann instanceof Column) {
                                    FieldMapper fieldMapper = new FieldMapper();
                                    Column c = (Column) ann;
                                    String column;
                                    if (StringUtils.isNotBlank(c.name())) {
                                        column = c.name();
                                    } else {
                                        column = field.getName();
                                    }
                                    fieldMapper.setColumn(column);
                                    fieldMapper.setProperty(field.getName());
                                    fieldMapper.setType(field.getType());
                                    classMapper.getFieldMappers().add(fieldMapper);
                                    classMapper.getColumnMap().put(fieldMapper.getProperty(), fieldMapper.getColumn());
                                } else if (ann instanceof Id) {
                                    Column cl = field.getAnnotation(Column.class);
                                    if (StringUtils.isNotBlank(cl.name())) {
                                        classMapper.setIdColumnName(cl.name());
                                    } else {
                                        classMapper.setIdColumnName(field.getName());
                                    }
                                    classMapper.setIdName(field.getName());

                                } else if (ann instanceof QueryMapper) {
                                    QueryMapper qm = (QueryMapper) ann;
                                    FieldMapper fieldMapper = new FieldMapper();
                                    fieldMapper.setColumn(qm.mappedBy());
                                    fieldMapper.setProperty(field.getName());
                                    if (qm.targetEntity() != void.class) {
                                        fieldMapper.setType(qm.targetEntity());
                                    } else {
                                        fieldMapper.setType(field.getType());
                                    }
                                    if (Collection.class.isAssignableFrom(field.getType())) {
                                        fieldMapper.setCollection(true);
                                    }
                                    classMapper.getQueryMappers().add(fieldMapper);
                                }
                            }
                        }
                    }
                });
                classMappers.put(clazz, classMapper);
            }
        }
        addResultMapper();
    }

    //将搜集好的 classMapper 都转换 成 ResultMapping，以便添加到 MapperBuilderAssistant。MapperBuilderAssistant 用于缓存、sql参数、查询返回的结果集处理
    private void addResultMapper() {
        Configuration c = sqlSessionTemplate.getConfiguration();
        MapperBuilderAssistant ass = new MapperBuilderAssistant(c, null);
        if (!CollectionUtils.isEmpty(classMappers)) {
            for (Class clazz : classMappers.keySet()) {
                ClassMapper cm = classMappers.get(clazz);
                ass.setCurrentNamespace(cm.getPackageName());
                List<ResultMapping> list1 = new ArrayList<>();
                for (FieldMapper fm : cm.getFieldMappers()) {
                    if (fm.getProperty().equals(cm.getIdName())) {
                        List<ResultFlag> rf = new ArrayList<>();
                        rf.add(ResultFlag.ID);
                        ResultMapping r = new ResultMapping.Builder(c, fm.getProperty(), fm.getColumn(), fm.getType())
                                .flags(rf).build();
                        list1.add(r);
                    } else {
                        ResultMapping r = new ResultMapping.Builder(c, fm.getProperty(), fm.getColumn(), fm.getType()).build();
                        list1.add(r);
                    }
                }
                int i = 0;
                for (FieldMapper fm : cm.getQueryMappers()) {
                    i++;
                    ClassMapper clm = classMappers.get(fm.getType());
                    if (clm != null) {
                        List<ResultMapping> list2 = new ArrayList<>();
                        for (FieldMapper fim : clm.getFieldMappers()) {
                            if (fim.getProperty().equals(clm.getIdName())) {
                                List<ResultFlag> rf = new ArrayList<>();
                                rf.add(ResultFlag.ID);
                                ResultMapping r = new ResultMapping.Builder(c, fim.getProperty(), fm.getProperty() + "___" + fim.getColumn(), fim.getType())
                                        .flags(rf).build();
                                list2.add(r);
                            } else {
                                ResultMapping r = new ResultMapping.Builder(c, fim.getProperty(), fm.getProperty() + "___" + fim.getColumn(), fim.getType()).build();
                                list2.add(r);
                            }
                        }
                        String resultId = cm.getClassName() + "[" + clm.getClassName() + i + "]";
                        ass.addResultMap(resultId, fm.getType(), null, null, list2, null);
                        if (fm.isCollection()) {
                            ResultMapping r = new ResultMapping.Builder(c, fm.getProperty(), null, List.class).nestedResultMapId(resultId).build();
                            list1.add(r);
                        } else {
                            ResultMapping r = new ResultMapping.Builder(c, fm.getProperty(), null, fm.getType()).nestedResultMapId(resultId).build();
                            list1.add(r);
                        }
                    }

                }
                ass.addResultMap(cm.getClassName(), clazz, null, null, list1, null);
            }
        }
        Iterator<XMLStatementBuilder> it = c.getIncompleteStatements().iterator();
        while (it.hasNext()) {
            it.next().parseStatementNode();
            it.remove();
        }
    }

    /**
     * 初始化 CRUDProvider 的实现
     */
    public void mybatisCurdInit() {
        Configuration c = sqlSessionTemplate.getConfiguration();
        Map<String, BaseMapper> map = SpringContext.getApplicationContext().getBeansOfType(BaseMapper.class);  //获取继承 BaseMapper 类的所有 Mapper
        for (String key : map.keySet()) {
            Class[] clazz = map.get(key).getClass().getInterfaces();
            if (ArrayUtils.isNotEmpty(clazz)) {
                Type[] t = clazz[0].getGenericInterfaces();
                if (ParameterizedType.class.isAssignableFrom(t[0].getClass())) {
                    for (Type t1 : ((ParameterizedType) t[0]).getActualTypeArguments()) {
                        ResultMap rm = c.getResultMap(((Class) t1).getName());
                        List<ResultMap> rmList = new ArrayList<>();
                        rmList.add(rm);

                        // get 方法初始化
                        MappedStatement ms = c.getMappedStatement(clazz[0].getName() + ".get");
                        Field field = ReflectionUtils.findField(MappedStatement.class, "resultMaps");
                        field.setAccessible(true);
                        ReflectionUtils.setField(field, ms, rmList);  //根据属性字段和对象，设置对象的值
                        field.setAccessible(false);
                        setHasNestedResultMaps(rm, ms);

                        // list 方法初始化
                        MappedStatement ms1 = c.getMappedStatement(clazz[0].getName() + ".list");
                        Field field1 = ReflectionUtils.findField(MappedStatement.class, "resultMaps");
                        field1.setAccessible(true);
                        ReflectionUtils.setField(field1, ms1, rmList);
                        field1.setAccessible(false);
                        setHasNestedResultMaps(rm, ms1);

                        // getBy 方法初始化
                        MappedStatement ms2 = c.getMappedStatement(clazz[0].getName() + ".getBy");
                        Field field2 = ReflectionUtils.findField(MappedStatement.class, "resultMaps");
                        field2.setAccessible(true);
                        ReflectionUtils.setField(field2, ms2, rmList);
                        field2.setAccessible(false);
                        setHasNestedResultMaps(rm, ms2);

                        // insert 方法初始化
                        MappedStatement ms3 = c.getMappedStatement(clazz[0].getName() + ".insert");
                        setKeyProperties(rm, ms3);

                        // insertExclude 方法初始化
                        MappedStatement ms4 = c.getMappedStatement(clazz[0].getName() + ".insertExclude");
                        setKeyProperties(rm, ms4);
                    }
                }
            }
        }
    }

    private void setHasNestedResultMaps(ResultMap rm, MappedStatement ms) {
        for (ResultMapping rmg : rm.getResultMappings()) {
            if (StringUtils.isNotBlank(rmg.getNestedResultMapId())) {
                Field field = ReflectionUtils.findField(MappedStatement.class, "hasNestedResultMaps");
                field.setAccessible(true);
                ReflectionUtils.setField(field, ms, true);
                field.setAccessible(false);
            }
        }
    }

    private void setKeyProperties(ResultMap rm, MappedStatement ms) {
        if (ArrayUtils.isNotEmpty(ms.getKeyProperties())) {
            Field field = ReflectionUtils.findField(MappedStatement.class, "keyProperties");
            field.setAccessible(true);
            List<ResultMapping> rms = rm.getIdResultMappings();
            String[] keyProperties = new String[rms.size()];
            for (int i = 0; i < rms.size(); i++) {
                keyProperties[i] = "entity." + rms.get(i).getProperty();
            }
            ReflectionUtils.setField(field, ms, keyProperties);
            field.setAccessible(false);
        }
    }

    /**
     * 取得某一类所在包的所有类名 不含迭代
     *
     * @param classLocation
     * @param packageName
     * @return
     */
    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        //将packageName分解
        String[] packagePathSplit = packageName.split("[.]");
        String realClassLocation = classLocation;
        int packageLength = packagePathSplit.length;
        for (int i = 0; i < packageLength; i++) {
            realClassLocation = realClassLocation + File.separator + packagePathSplit[i];
        }
        File packeageDir = new File(realClassLocation);
        if (packeageDir.isDirectory()) {
            String[] allClassName = packeageDir.list();
            return allClassName;
        }
        return null;
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {

        //第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        //是否循环迭代
        boolean recursive = true;
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代下去
            while (dirs.hasMoreElements()) {
                //获取下一个元素
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    //如果是jar包文件
                    //定义一个JarFile
                    JarFile jar;
                    try {
                        //获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        //从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        //同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //如果是以/开头的
                            if (name.charAt(0) == '/') {
                                //获取后面的字符串
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    //获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    //如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            //添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ClassMapper {

        private Logger logger = Logger.getLogger(ClassMapper.class);

        private String tableName;
        private String packageName;
        private String className;
        private String idName;
        private String idColumnName;
        private List<FieldMapper> FieldMappers;
        private List<FieldMapper> queryMappers;
        private Map<String, String> columnMap;

        public String insertSql(String[] includeProperty, String[] excludeProperty, String prefix) {
            SQL sql = new SQL();
            sql.INSERT_INTO(getTableName());
            StringBuilder db = new StringBuilder();
            StringBuilder p = new StringBuilder();
            for (FieldMapper fm : FieldMappers) {
                if (!ArrayUtils.isEmpty(includeProperty)) {
                    if (ArrayUtils.contains(includeProperty, fm.getProperty())) {
                        if (db.length() > 0) {
                            db.append(',');
                            p.append(",");
                        }
                        db.append(fm.getColumn());
                        p.append("#{").append(prefix).append(fm.getProperty()).append('}');
                    }
                } else if (!ArrayUtils.isEmpty(excludeProperty)) {
                    if (ArrayUtils.contains(excludeProperty, fm.getProperty())) {
                        continue;
                    }
                    if (db.length() > 0) {
                        db.append(',');
                        p.append(",");
                    }
                    db.append(fm.getColumn());
                    p.append("#{").append(prefix).append(fm.getProperty()).append('}');
                } else {
                    if (db.length() > 0) {
                        db.append(',');
                        p.append(",");
                    }
                    db.append(fm.getColumn());
                    p.append("#{").append(prefix).append(fm.getProperty()).append('}');
                }
            }
            sql.VALUES(db.toString(), p.toString());
            return sql.toString();
        }

        public String updateSql(WhereCondition where, String[] includeProperty, String prefix) {
            if (where == null) {
                where = WhereBuilder.builder().and(getIdName()).build();
            }
            SQL sql = new SQL();
            sql.UPDATE(getTableName());
            where.appendWhere(this, null, prefix, null, null);
            StringBuilder sb = new StringBuilder();
            for (FieldMapper fm : FieldMappers) {
                if (ArrayUtils.isEmpty(includeProperty) || ArrayUtils.contains(includeProperty, fm.getProperty())) {
                    if (!where.getReplaceMap().containsKey(fm.getProperty())) {
                        if (sb.length() > 0) {
                            sb.append(',');
                        }
                        sb.append(fm.getColumn()).append("=#{").append(prefix).append(fm.getProperty()).append('}');
                    }
                }
            }
            sql.WHERE(where.getWhere());
            sql.SET(sb.toString());
            return sql.toString();
        }

        public String deleteSql(WhereCondition where, String prefix) {
            if (where == null) {
                where = WhereBuilder.builder().and(getIdName()).build();
            }
            SQL sql = new SQL();
            where.appendWhere(this, null, prefix, null, null);
            sql.DELETE_FROM(getTableName());
            sql.WHERE(where.getWhere());
            return sql.toString();
        }

        public String selectSql(WhereCondition where, String[] includeProperty, String[] excludeProperty, String[] orderBy,
                                Integer first, Integer max, String prefix, boolean cascade, Object data) {
            String alias = "ALIAS";
            String aliasDot = "ALIAS.";
            SQL sql = new SQL();
            StringBuilder sb = new StringBuilder();
            for (FieldMapper fm : FieldMappers) {
                if (!ArrayUtils.isEmpty(includeProperty)) {
                    if (ArrayUtils.contains(includeProperty, fm.getProperty())) {
                        if (sb.length() > 0) {
                            sb.append(',');
                        }
                        sb.append(aliasDot).append(fm.getColumn()).append(" AS ").append(fm.getColumn());
                    }
                } else if (!ArrayUtils.isEmpty(excludeProperty)) {
                    if (ArrayUtils.contains(excludeProperty, fm.getProperty())) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(aliasDot).append(fm.getColumn()).append(" AS ").append(fm.getColumn());
                } else {
                    if (sb.length() > 0) {
                        sb.append(',');
                    }
                    sb.append(aliasDot).append(fm.getColumn()).append(" AS ").append(fm.getColumn());
                }
                if (ArrayUtils.isNotEmpty(orderBy)) {
                    for (int i = 0; i < orderBy.length; i++) {
                        if (orderBy[i].contains(fm.getProperty())) {
                            orderBy[i] = aliasDot + orderBy[i].replace(fm.getProperty(), fm.getColumn());
                        }
                    }
                }
            }
            if (where != null) {
                where.appendWhere(this, null, prefix, alias, data);
            }
            if (cascade) {
                int i = 0;
                for (FieldMapper fm : queryMappers) {
                    i++;
                    String alias1 = "ALIAS" + i;
                    String aliasDot1 = "ALIAS" + i + ".";
                    String[] cc = fm.getColumn().split("=");
                    StringBuilder on = new StringBuilder();
                    ClassMapper cm = classMappers.get(fm.getType());
                    on.append(aliasDot).append(columnMap.get(cc[0])).append("=").append(aliasDot1).append(cm.getColumnMap().get(cc[1]));
                    for (FieldMapper fim : cm.getFieldMappers()) {
                        if (!ArrayUtils.isEmpty(includeProperty)) {
                            if (ArrayUtils.contains(includeProperty, fim.getProperty())) {
                                if (sb.length() > 0) {
                                    sb.append(',');
                                }
                                sb.append(aliasDot1).append(fim.getColumn()).append(" AS ").append(fm.getProperty() + "___" + fim.getColumn());
                            }
                        } else if (!ArrayUtils.isEmpty(excludeProperty)) {
                            if (ArrayUtils.contains(excludeProperty, fim.getProperty())) {
                                continue;
                            }
                            if (sb.length() > 0) {
                                sb.append(',');
                            }
                            sb.append(aliasDot1).append(fim.getColumn()).append(" AS ").append(fm.getProperty() + "___" + fim.getColumn());
                        } else {
                            if (sb.length() > 0) {
                                sb.append(',');
                            }
                            sb.append(aliasDot1).append(fim.getColumn()).append(" AS ").append(fm.getProperty() + "___" + fim.getColumn());
                        }
                        if (ArrayUtils.isNotEmpty(orderBy)) {
                            for (int j = 0; j < orderBy.length; j++) {
                                if (orderBy[j].contains(fm.getProperty() + "@" + fim.getProperty())) {
                                    orderBy[j] = aliasDot1 + orderBy[j].replace(fm.getProperty() + "@" + fim.getProperty(), fim.getColumn());
                                }
                            }
                        }
                    }
                    sql.LEFT_OUTER_JOIN(cm.getTableName() + " " + alias1 + " ON " + on.toString());
                    if (where != null) {
                        where.appendWhere(cm, fm.getProperty(), prefix, alias1, data);
                    }
                }
            }
            if (where != null) {
                sql.WHERE(where.getWhere());
            }
            if (ArrayUtils.isNotEmpty(orderBy)) {
                for (String str : orderBy) {
                    sql.ORDER_BY(str);
                }
            }
            sql.SELECT(sb.toString());
            sql.FROM(getTableName() + " " + alias);
            String sqlStr = sql.toString();
            if (max != null && first != null) {
                sqlStr = sqlStr + " LIMIT " + first + ", " + max;
            } else if (max != null) {
                first = 0;
                sqlStr = sqlStr + " LIMIT " + first + ", " + max;
            }
            return sqlStr;
        }

        public String selectCountSql(WhereCondition where, String prefix, boolean cascade, Object data) {
            String alias = "ALIAS";
            String aliasDot = "ALIAS.";
            SQL sql = new SQL();
            if (where != null) {
                where.appendWhere(this, null, prefix, alias, data);
            }
            if (cascade) {
                int i = 0;
                for (FieldMapper fm : queryMappers) {
                    i++;
                    String alias1 = "ALIAS" + i;
                    String aliasDot1 = "ALIAS" + i + ".";
                    String[] cc = fm.getColumn().split("=");
                    StringBuilder on = new StringBuilder();
                    ClassMapper cm = classMappers.get(fm.getType());
                    on.append(aliasDot).append(columnMap.get(cc[0])).append("=").append(aliasDot1).append(cm.getColumnMap().get(cc[1]));
                    sql.LEFT_OUTER_JOIN(cm.getTableName() + " " + alias1 + " ON " + on.toString());
                    if (where != null) {
                        where.appendWhere(cm, fm.getProperty(), prefix, alias1, data);
                    }
                }
            }
            if (where != null) {
                sql.WHERE(where.getWhere());
            }
            sql.SELECT("count(1)");
            sql.FROM(getTableName() + " " + alias);
            return sql.toString();
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getIdName() {
            return idName;
        }

        public void setIdName(String idName) {
            this.idName = idName;
        }

        public String getIdColumnName() {
            return idColumnName;
        }

        public void setIdColumnName(String idColumnName) {
            this.idColumnName = idColumnName;
        }

        public List<FieldMapper> getFieldMappers() {
            return FieldMappers;
        }

        public void setFieldMappers(List<FieldMapper> fieldMappers) {
            FieldMappers = fieldMappers;
        }

        public List<FieldMapper> getQueryMappers() {
            return queryMappers;
        }

        public void setQueryMappers(List<FieldMapper> queryMappers) {
            this.queryMappers = queryMappers;
        }

        public Map<String, String> getColumnMap() {
            return columnMap;
        }

        public void setColumnMap(Map<String, String> columnMap) {
            this.columnMap = columnMap;
        }
    }

    public static class FieldMapper {
        private String property;
        private String column;
        private Class<?> type;
        private String nestedQueryId;
        private boolean isCollection;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Class<?> getType() {
            return type;
        }

        public void setType(Class<?> type) {
            this.type = type;
        }

        public String getNestedQueryId() {
            return nestedQueryId;
        }

        public void setNestedQueryId(String nestedQueryId) {
            this.nestedQueryId = nestedQueryId;
        }

        public boolean isCollection() {
            return isCollection;
        }

        public void setCollection(boolean isCollection) {
            this.isCollection = isCollection;
        }
    }
}
