package com.xxx.core.persist.respository.mybatis.criteria;

import java.util.ArrayList;
import java.util.List;

public class WhereBuilder {

	public static final String OR = " OR ";
	public static final String AND = " AND ";
	public static final String LEFT = "( ";
	public static final String RIGHT = " )";
	public static final String EQ = " = ";
	public static final String NOTEQ = " != ";
	public static final String LIKE = " LIKE ";
	public static final String IN = " IN ";
	public static final String NOTIN = "NOT IN ";

	List<String> list = new ArrayList<>();
	List<WhereColumn> properties = new ArrayList<>();

	private WhereBuilder() {
	}

	public static WhereBuilder builder() {
		return new WhereBuilder();
	}

	public WhereBuilder and(String name) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(name);
		properties.add(createWhereColumn(name, EQ));
		return this;
	}

    public WhereBuilder and(String name, Operator op){
        if (!list.isEmpty()) {
            list.add(AND);
        }
        list.add(name);
        properties.add(createWhereColumn(name, op.getOp()));
        return this;
    }

	public WhereBuilder andNot(String name) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(name);
		properties.add(createWhereColumn(name, NOTEQ));
		return this;
	}
	
	public WhereBuilder andNotIn(String name) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(name);
		properties.add(createWhereColumn(name, NOTIN));
		return this;
	}

	public WhereBuilder or(String name) {
        if (!list.isEmpty()) {
            list.add(OR);
        }
        list.add(name);
        properties.add(createWhereColumn(name, EQ));
        return this;
    }

    public WhereBuilder or(String name, Operator op) {
        if (!list.isEmpty()) {
            list.add(OR);
        }
        list.add(name);
        properties.add(createWhereColumn(name, op.getOp()));
        return this;
    }

	public WhereBuilder andLike(String name) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(name);
		properties.add(createWhereColumn(name, LIKE));
		return this;
	}

	public WhereBuilder orLike(String name) {
		if (!list.isEmpty()) {
			list.add(OR);
		}
		list.add(name);
		properties.add(createWhereColumn(name, LIKE));
		return this;
	}

	public WhereBuilder andIn(String name) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(name);
		properties.add(createWhereColumn(name, IN));
		return this;
	}

	public WhereBuilder orIn(String name) {
		if (!list.isEmpty()) {
			list.add(OR);
		}
		list.add(name);
		properties.add(createWhereColumn(name, IN));
		return this;
	}

	public WhereBuilder and(WhereCondition whereCondition) {
		if (!list.isEmpty()) {
			list.add(AND);
		}
		list.add(WhereBuilder.LEFT);
		list.addAll(whereCondition.getConditions());
		properties.addAll(whereCondition.getProperties());
		list.add(WhereBuilder.RIGHT);
		return this;
	}

	public WhereBuilder or(WhereCondition whereCondition) {
		if (!list.isEmpty()) {
			list.add(OR);
		}
		list.add(WhereBuilder.LEFT);
		list.addAll(whereCondition.getConditions());
		properties.addAll(whereCondition.getProperties());
		list.add(WhereBuilder.RIGHT);
		return this;
	}

	private WhereColumn createWhereColumn(String name, String op) {
		if (name.contains("@")) {
			String[] n = name.split("@");
			return new WhereColumn(n[1], op, n[0]);
		}
		return new WhereColumn(name, op);
	}

	public WhereCondition build() {
		return new WhereCondition(list, properties);
	}

	public static boolean isOperator(String str) {
		if (AND.equals(str) || OR.equals(str) || LEFT.equals(str)
				|| RIGHT.equals(str)) {
			return true;
		}
		return false;
	}

}
