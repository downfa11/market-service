package com.ns.marketservice.Repository;


import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.DTO.BoardFilter;
import com.ns.marketservice.Domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardCustomRepositoryImpl extends QuerydslRepositorySupport implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Board.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }
    @Override
    public Page<Board> findByNickname(String nickName, Long lastBoardId, BoardFilter filter,Pageable pageable) {
        QBoard qBoard = QBoard.board;
        BooleanBuilder builder = filterBoard(qBoard,filter);
        builder.and(qBoard.membership.nickname.contains(nickName))
                .and(qBoard.boardId.goe(lastBoardId));

        JPAQuery<Board> query = new JPAQuery<>(getEntityManager());
        query.from(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .leftJoin(qBoard.review).fetchJoin()
                .where(builder)
                .orderBy(qBoard.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return getPage(query, pageable);
    }

    @Override
    public Page<Board> findBoardByCategory(String categoryName, Long lastBoardId,BoardFilter filter, Pageable pageable) {
        QBoard qBoard = QBoard.board;
        BooleanBuilder builder = filterBoard(qBoard,filter);
        builder.and(qBoard.category.categoryName.eq(categoryName))
                .and(qBoard.boardId.goe(lastBoardId));

        JPAQuery<Board> query = new JPAQuery<>(getEntityManager());
        query.from(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .leftJoin(qBoard.review).fetchJoin()
                .where(builder)
                .orderBy(qBoard.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return getPage(query, pageable);
    }

    @Override
    public Page<Board> findByTitle(String title, Long lastBoardId,BoardFilter filter, Pageable pageable) {
        QBoard qBoard = QBoard.board;
        BooleanBuilder builder = filterBoard(qBoard,filter);
        builder.and(qBoard.title.contains(title))
                .and(qBoard.boardId.goe(lastBoardId));

        JPAQuery<Board> query = new JPAQuery<>(getEntityManager());
        query.from(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .leftJoin(qBoard.review).fetchJoin()
                .where(builder)
                .orderBy(qBoard.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return getPage(query, pageable);
    }

    @Override
    public Page<Board> findByContents(String keyword, Long lastBoardId, BoardFilter filter, Pageable pageable) {
        QBoard qBoard = QBoard.board;
        BooleanBuilder builder = filterBoard(qBoard,filter);
        builder.and(qBoard.contents.contains(keyword))
                .and(qBoard.boardId.goe(lastBoardId));

        JPAQuery<Board> query = new JPAQuery<>(getEntityManager());
        query.from(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .leftJoin(qBoard.review).fetchJoin()
                .where(builder)
                .orderBy(qBoard.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return getPage(query, pageable);
    }

    @Override
    public Page<Board> findBoardAll(Long lastBoardId, BoardFilter filter, Pageable pageable){
        QBoard qBoard = QBoard.board;
        BooleanBuilder builder = filterBoard(qBoard,filter);
        builder.and(qBoard.boardId.goe(lastBoardId));

        JPAQuery<Board> query = new JPAQuery<>(getEntityManager());
        query.from(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .leftJoin(qBoard.review).fetchJoin()
                        .where(builder)
                .orderBy(qBoard.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        return getPage(query, pageable);
    }

    private BooleanBuilder filterBoard(QBoard qBoard,BoardFilter filter){
        BooleanBuilder builder = new BooleanBuilder();

        if (filter != null) {
            if (filter.getSortStatus() != null && !filter.getSortStatus().isEmpty())
                builder.and(qBoard.sortStatus.in(filter.getSortStatus()));

            if (filter.getRegions() != null && !filter.getRegions().isEmpty())
                builder.and(qBoard.region.in(filter.getRegions()));

            if (filter.getCategories() != null && !filter.getCategories().isEmpty())
                builder.and(qBoard.category.in(filter.getCategories()));

        }

        return builder;
    }

    private Page<Board> getPage(JPAQuery<Board> query, Pageable pageable) {
        long totalCount = query.fetchCount();
        List<Board> content = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(content, pageable, totalCount);
    }
}