package com.vlad.app.utils;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

public class QueryBuilder<T> {

	public QueryBuilder() {
	}

	public TypedQuery<T> buildTypedQuery(String sql, Map<String, Object> parameters, List<String> wherePredicates,
			EntityManager entityManager, Class<T> entity) {
		var whereClause = (wherePredicates == null || wherePredicates.isEmpty()) ?
				"" :
				" where " + StringUtils.join(wherePredicates, " and ");
		var query = entityManager.createQuery(sql + whereClause, entity);
		parameters.forEach(query::setParameter);
		return query;
	}

	public Query buildQuery(String sql, Map<String, Object> parameters, List<String> wherePredicates,
			EntityManager entityManager) {
		var query = entityManager.createQuery(sql + " where " + StringUtils.join(wherePredicates, " and "));
		parameters.forEach(query::setParameter);
		return query;
	}
}
