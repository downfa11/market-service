package com.ns.marketservice.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMembership is a Querydsl query type for Membership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMembership extends EntityPathBase<Membership> {

    private static final long serialVersionUID = 997943023L;

    public static final QMembership membership = new QMembership("membership");

    public final StringPath address = createString("address");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath curProductRegion = createString("curProductRegion");

    public final StringPath email = createString("email");

    public final NumberPath<Integer> exp = createNumber("exp", Integer.class);

    public final BooleanPath isValid = createBoolean("isValid");

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final NumberPath<Long> membershipId = createNumber("membershipId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath region = createString("region");

    public final NumberPath<Integer> role = createNumber("role", Integer.class);

    public final StringPath type = createString("type");

    public QMembership(String variable) {
        super(Membership.class, forVariable(variable));
    }

    public QMembership(Path<? extends Membership> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMembership(PathMetadata metadata) {
        super(Membership.class, metadata);
    }

}

