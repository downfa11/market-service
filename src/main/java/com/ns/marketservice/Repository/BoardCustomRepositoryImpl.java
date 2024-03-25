package com.ns.marketservice.Repository;


import com.ns.marketservice.Domain.Board;
import com.ns.marketservice.Domain.Category;
import com.ns.marketservice.Domain.DTO.BoardFilter;
import com.ns.marketservice.Domain.DTO.BoardResponse;
import com.ns.marketservice.Domain.QBoard;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
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
    private final QBoard qBoard;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(Board.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.qBoard=QBoard.board;
    }
    @Override
    public Page<Board> findByNickname(String nickName, Long lastBoardId, BoardFilter filter,Pageable pageable) {

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
    public List<Board> findBoardAll(Long lastBoardId,Long offset, BoardFilter filter){

        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin() //Todo
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions)
                        ,qBoard.boardId.goe(lastBoardId))
                .orderBy(qBoard.createdAt.desc())
                        .offset(offset)
                        .limit(10).fetch();

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

    private BooleanExpression inSortStatus(List<Board.SortStatus> sortStatuses){
        if(sortStatuses.isEmpty())
            return null;

        return qBoard.sortStatus.in(sortStatuses);
    }

    private BooleanExpression inCategory(List<Category> category){
        if(category.isEmpty())
            return null;

        return qBoard.category.in(category);
    }

    private BooleanExpression inRegion(List<String> region){
        if(region.isEmpty())
            return null;

        return qBoard.region.in(region);
    }

    public Boolean exist(Long boardId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(qBoard)
                .where(qBoard.boardId.eq(boardId))
                .fetchFirst();
        return fetchOne !=null;
    }
}