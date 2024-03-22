package com.ns.marketservice.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = 1974486061L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final ListPath<BoardImage, QBoardImage> boardImage = this.<BoardImage, QBoardImage>createList("boardImage", BoardImage.class, QBoardImage.class, PathInits.DIRECT2);

    public final QCategory category;

    public final StringPath contents = createString("contents");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> hits = createNumber("hits", Integer.class);

    public final QMembership membership;

    public final NumberPath<Long> price = createNumber("price", Long.class);

    public final StringPath region = createString("region");

    public final ListPath<Review, QReview> review = this.<Review, QReview>createList("review", Review.class, QReview.class, PathInits.DIRECT2);

    public final NumberPath<Integer> reviewCnt = createNumber("reviewCnt", Integer.class);

    public final EnumPath<Board.SortStatus> sortStatus = createEnum("sortStatus", Board.SortStatus.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.membership = inits.isInitialized("membership") ? new QMembership(forProperty("membership")) : null;
    }

}

