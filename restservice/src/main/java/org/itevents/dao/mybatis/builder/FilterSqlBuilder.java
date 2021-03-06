package org.itevents.dao.mybatis.builder;

import org.itevents.dao.model.Filter;
import org.itevents.dao.model.Technology;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class FilterSqlBuilder {

    public String addFilterTechnology(Filter filter) {
        List<Technology> technologies = filter.getTechnologies();
        if (CollectionUtils.isEmpty(technologies)) {
            return "";
        } else {
            return makeSql(filter, technologies);
        }
    }

    private String makeSql(Filter filter, List<Technology> technologies) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO filter_technology (filter_id, technology_id) VALUES ");
        for (Technology technology : technologies) {
            sql.append("(")
                    .append(filter.getId()).append(", ")
                    .append(technology.getId()).append("), ");
        }
        int lastCommaPositionFromEnd = 2;
        sql.delete(sql.length() - lastCommaPositionFromEnd, sql.length());
        return sql.toString();
    }
}
