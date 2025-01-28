package com.donut.swaipe.domain.user.repository;

import com.donut.swaipe.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    @Override
    public int deleteByUsername(String username) {
        return (int) queryFactory
                .delete(user)
                .where(user.username.eq(username))
                .execute();
    }
}