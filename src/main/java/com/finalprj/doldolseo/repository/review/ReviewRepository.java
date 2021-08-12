package com.finalprj.doldolseo.repository.review;

import com.finalprj.doldolseo.domain.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * 후기게시판 Repository
 *
 * @Author 김경일
 * @Date 2021/08/05
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    //전체 글 목록 조회
    Page<Review> findAll(Pageable pageable);

    //지역별 글 목록 조회
    Page<Review> findAllByAreaNo(Integer areaNo, Pageable pageable);

    //글 상세 조회
    Review findByReviewNo(Long reviewNo);



}