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
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final QBoard qBoard;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.qBoard=QBoard.board;
    }

    @Override
    public List<Board> findByNickname(String nickName, Long offset,BoardFilter filter) {
        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions)
                        ,qBoard.membership.nickname.contains(nickName))
                .orderBy(qBoard.createdAt.desc())
                .offset(offset)
                .limit(10).fetch();
    }

    @Override
    public List<Board> findBoardByCategory(String categoryName,Long offset,BoardFilter filter) {

        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions)
                        ,qBoard.category.categoryName.eq(categoryName))
                .orderBy(qBoard.createdAt.desc())
                .offset(offset)
                .limit(10).fetch();
    }

    @Override
    public List<Board> findByTitle(String title,Long offset,BoardFilter filter) {

        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions)
                        ,qBoard.title.contains(title))
                .orderBy(qBoard.createdAt.desc())
                .offset(offset)
                .limit(10).fetch();
    }

    @Override
    public List<Board> findByContents(String keyword, Long offset,BoardFilter filter) {

        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions)
                ,qBoard.contents.contains(keyword))
                .orderBy(qBoard.createdAt.desc())
                .offset(offset)
                .limit(10).fetch();
    }

    @Override
    public List<Board> findBoardAll(Long offset, BoardFilter filter){

        List<Category> categories = filter.getCategories();
        List<String> regions = filter.getRegions();
        List<Board.SortStatus> sortStatuses = filter.getSortStatus();

        return jpaQueryFactory.selectFrom(qBoard)
                .leftJoin(qBoard.membership).fetchJoin()
                .leftJoin(qBoard.category).fetchJoin()
                .where(inSortStatus(sortStatuses)
                        ,inCategory(categories)
                        ,inRegion(regions))
                        //,qBoard.boardId.goe(lastBoardId))
                .orderBy(qBoard.createdAt.desc())
                        .offset(offset)
                        .limit(10).fetch();
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