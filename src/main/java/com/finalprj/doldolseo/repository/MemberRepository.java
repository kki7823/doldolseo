package com.finalprj.doldolseo.repository;

import com.finalprj.doldolseo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/*
 * 멤버 관련 Repository
 *
 * @Author 백정연
 * @Date 2021/08/03
 */

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findById(String id);
}