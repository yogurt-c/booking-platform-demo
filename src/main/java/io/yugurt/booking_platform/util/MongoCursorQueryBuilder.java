package io.yugurt.booking_platform.util;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoCursorQueryBuilder {

    private MongoCursorQueryBuilder() {
    }

    public static Query buildCursorQuery(String cursor, int size, String sortField, Sort.Direction direction) {
        Query query = new Query();

        if (cursor != null && !cursor.isEmpty()) {
            Criteria criteria = direction == Sort.Direction.DESC
                ? Criteria.where(sortField).lt(new ObjectId(cursor))
                : Criteria.where(sortField).gt(new ObjectId(cursor));
            query.addCriteria(criteria);
        }

        query.limit(size + 1).with(Sort.by(direction, sortField));
        return query;
    }

    public static Query buildDescCursorQuery(String cursor, int size) {

        return buildCursorQuery(cursor, size, "_id", Sort.Direction.DESC);
    }
}
