package com.finalprj.doldolseo.repository;

import com.finalprj.doldolseo.vo.AreaVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * 지역게시판 Resository
 *
 * @Author 김경일
 * @Date 2021/08/04
 */

@Repository
public interface AreaRepository extends JpaRepository<AreaVO, String> {

    //지역명으로 지역정보 조회
    AreaVO findFirstByName(String name);

    //지역명으로 지역정보 조회
//    List<AreaVO> findBySigungu(Integer sigungu, Pageable pageable);
    Page<AreaVO> findBySigungu(Integer sigungu, Pageable pageable);



}
