package com.finalprj.doldolseo.service.impl;

import com.finalprj.doldolseo.domain.crew.Crew;
import com.finalprj.doldolseo.domain.review.Review;
import com.finalprj.doldolseo.domain.review.ReviewComment;
import com.finalprj.doldolseo.dto.MemberDTO;
import com.finalprj.doldolseo.domain.Member;
import com.finalprj.doldolseo.dto.crew.CrewDTO;
import com.finalprj.doldolseo.dto.review.ReviewCommentDTO;
import com.finalprj.doldolseo.dto.review.ReviewDTO;
import com.finalprj.doldolseo.repository.MemberRepository;
import com.finalprj.doldolseo.repository.crew.CrewMemberRepository;
import com.finalprj.doldolseo.repository.crew.CrewRepository;
import com.finalprj.doldolseo.repository.review.ReviewCommentRepository;
import com.finalprj.doldolseo.repository.review.ReviewRepository;
import com.finalprj.doldolseo.security.SecurityDetails;
import com.finalprj.doldolseo.service.MemberService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
 * 멤버 Service 구현 클래스
 *
 * @Author 백정연
 * @Date 2021/08/03
 */

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository repository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReviewCommentRepository commentRepository;

    // 추가 코드
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // MemberDTO 객체를 Member 객체로 변환해서 반환해주는 메소드
    public Member selectMemberEntity(Member entity){
        Member member = repository.findById(entity.getId()).get();
        return member;
    }

    // 스프링 시큐리티 세션 변경 처리 메소드
    @Override
    public void updateMemberSecurity(MemberDTO dto, HttpSession session){
        // 세션 초기화
        SecurityContextHolder.clearContext();
        UserDetails updateUserDetails = new SecurityDetails(dto);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(updateUserDetails, null, updateUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", newAuth);
    }

    @Override
    public List<CrewDTO> getCrewList(String id) {

        return null;
    }

    @Override
    public MemberDTO save(MemberDTO memberDTO) throws IOException {
        String rawPassword = memberDTO.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        memberDTO.setPassword(encPassword);

        Member member = dtoToEntity(memberDTO);
        Member validUser = repository.save(member);
        MemberDTO dto = entityToDto(validUser);
        return  dto;
    }

    //temp BY gyeong
    public MemberDTO updateMmeber(MemberDTO memberDTO) throws IOException {

        Member memberEntity = dtoToEntity(memberDTO);
        Member member = repository.save(memberEntity);
        MemberDTO dto = entityToDto(member);
        return  dto;
    }

    @Override
    public MemberDTO selectMember(String id) {
        Optional<Member> member = repository.findById(id);
        return member.isPresent()? entityToDto(member.get()) : null;
    }

    @Override
    public Page<ReviewDTO> getReviewListByUser(String id,Pageable pageable) {
        Page<Review> entityPage = reviewRepository.findAllById(id, pageable);
        Page<ReviewDTO> reviewList = modelMapper.map(entityPage,
                new TypeToken<Page<ReviewDTO>>(){}.getType());

        return reviewList;
    }

    @Override
    public Page<ReviewCommentDTO> getReviewCommentListByUser(String id, Pageable pageable) {
        Page<ReviewComment> entityPage = commentRepository.findAllById(id, pageable);
        Page<ReviewCommentDTO> commentList = modelMapper.map(entityPage,
                new TypeToken<Page<ReviewCommentDTO>>(){}.getType());
        return commentList;
    }

    @Override
    public List<ReviewDTO> getReviewListByMember(String id) {
        List<Review> reviews = reviewRepository.findAllById(id);
        List<ReviewDTO> reviewList = modelMapper.map(reviews, new TypeToken<List<ReviewDTO>>(){}.getType());
        return reviewList;
    }

    @Override
    public void deleteCommentListByUser(String id) {
        commentRepository.deleteAllById(id);
    }

    @Override
    public void deleteCommentListByReviewNo(Long reviewNo) {
        commentRepository.deleteAllByReviewNo(reviewNo);
    }

    @Override
    public int deleteMember(String id) {
        repository.deleteById(id);
        Optional<Member> result = repository.findById(id);
        return !(result.isPresent())? 0 : 1;
        // 계정이 삭제되었다면 0, 그렇지 않다면 1
    }

    @Override
    public int checkId(String id) {
        Optional<Member> result = repository.findById(id);
        return result.isPresent()? 0 : 1;
        // 중복된 아이디가 있으면 0, 없다면 1
    }

    @Override
    public int checkNickname(String nickname) {
        Optional<Member> result = repository.findByNickname(nickname);
        return result.isPresent()? 0 : 1;
        // 중복된 닉네임이 있으면 0, 없다면 1
    }
}