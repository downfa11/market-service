package com.ns.marketservice.Repository;

import com.ns.marketservice.Domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Membership,Long> {

    Optional<Membership> findByEmail(String email);
    Optional<Membership> findByNicknameAndType(String nickname,String type);
    Optional<Membership> findByNickname(String nickname);


    Optional<Membership> findById(Long id);
}