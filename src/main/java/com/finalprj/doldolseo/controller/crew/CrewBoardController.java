package com.finalprj.doldolseo.controller.crew;

import com.finalprj.doldolseo.domain.crew.Crew;
import com.finalprj.doldolseo.domain.crew.CrewMember;
import com.finalprj.doldolseo.dto.crew.CrewDTO;
import com.finalprj.doldolseo.dto.crew.CrewMemberDTO;
import com.finalprj.doldolseo.dto.crew.CrewPostDTO;
import com.finalprj.doldolseo.dto.review.ReviewDTO;
import com.finalprj.doldolseo.service.impl.crew.CrewBoardServiceImpl;
import com.finalprj.doldolseo.service.impl.crew.CrewMemberServiceImpl;
import com.finalprj.doldolseo.service.impl.crew.CrewServiceImpl;
import com.finalprj.doldolseo.util.PagingUtil;
import com.finalprj.doldolseo.util.UploadFileUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CrewBoardController {

    @Autowired
    CrewBoardServiceImpl crewBoardService;
    @Autowired
    CrewMemberServiceImpl crewMemberService;
    @Autowired
    CrewServiceImpl crewService;


    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UploadFileUtil fileUtil;


    //크루활동게사글 목록
    @GetMapping("/crew/board")
    public String crewBoardList(Model model,
                                @RequestParam(name = "cat", required = false) String category,
                                @PageableDefault(size = 30, sort = "wDate", direction = Sort.Direction.DESC) Pageable pageable) throws Exception {

        Page<CrewPostDTO> crewPosts;

        if (category == null) {
            crewPosts = crewBoardService.getCrewPosts(pageable);
        } else {
            crewPosts = crewBoardService.getCrewPostsByCat(category, pageable);
        }


        PagingUtil pagingUtil = new PagingUtil(10, crewPosts);
        model.addAttribute("startBlockPage", pagingUtil.startBlockPage);
        model.addAttribute("endBlockPage", pagingUtil.endBlockPage);
        model.addAttribute("crewPosts", crewPosts);

        return "/crew/crewBoard/crewBoardList";
    }

    //크루활동게사글 상세
    @GetMapping("/crew/board/{postNo}")
    public String crewBoardDetail(Model model,
                                  @PathVariable("postNo") Long postNo) throws Exception {
        CrewPostDTO crewPost = crewBoardService.getCrewPost(postNo);

        String content = crewPost.getContent();
        if (content != null) {
            crewPost.setContent(content.replace("temp", "" + crewPost.getPostNo()));
        }

        model.addAttribute("crewPost", crewPost);

        return "/crew/crewBoard/crewBoardDetail";
    }

    //크루활동게사글 등록 폼
    @GetMapping("/crew/board/new")
    public String crewBoardInsertForm(Model model,
                                      @RequestParam String id) throws Exception {
        System.out.println(id);
        List<CrewMemberDTO> myCrewList = crewMemberService.getMyCrewList(id);
        model.addAttribute("myCrewList", myCrewList);
        return "/crew/crewBoard/crewBoardInsert";
    }

    //크루활동게사글 크루원 명단 -- 등록 폼 : 함께한 크루원 명단 출력 시
    @PostMapping("/crew/board/new")
    @ResponseBody
    public List<CrewMemberDTO> getCrewList(@RequestBody Map<String, Long> crewNo) throws Exception {
        return crewMemberService.getCrewMembers(crewNo.get("crewNo"));
    }

    //크루활동게사글 등록
    @PostMapping("/crew/board")
    @ResponseBody
    public void crewBoardInsert(@RequestParam(required = false) String[] uploadImgs,
                                @RequestParam(required = false) String[] memberName,
                                CrewPostDTO dto) {

        System.out.println("11111111111111 이미지 :"+ Arrays.toString(uploadImgs));
        System.out.println("222222222222 dto :"+ dto.toString());
        System.out.println("333333333 멤버이름 :"+ Arrays.toString(memberName));

        if (uploadImgs != null) {
            //String배열 문자열 치환 후 문자열로 변경
            String uploadImg = Arrays.stream(uploadImgs).map(s -> s = s.split("temp")[1].substring(1)).collect(Collectors.joining(","));
            dto.setUploadImg(uploadImg);
        }

        CrewDTO crewDTO = crewService.getCrew(dto.getCrew().getCrewNo());
        Crew crew = modelMapper.map(crewDTO, Crew.class);
        CrewPostDTO post = crewBoardService.insertPost(dto,crew);
        if (post.getUploadImg() != null) {
            fileUtil.moveImagesCrew(post.getPostNo(), post.getUploadImg());
        }
        System.out.println("등록 완료");
    }


    //크루활동게사글 삭제
    @DeleteMapping("/crew/board/{postNo}")
    public void crewBoardDelete(@PathVariable("postNo") Long boardNo) throws Exception {
    }

    //크루활동게사글 수정 폼
    @GetMapping("/crew/board/{postNo}/edit")
    public String crewBoardUpdateForm(@PathVariable("postNo") String boardNo) throws Exception {
        return "/crew/crewBoard/crewBoardUpdate";
    }


    //크루활동게사글 수정
    @PutMapping("/crew/board/{boardNo}/")
    public void crewBoardUpdate(@PathVariable("boardNo") String boardNo) throws Exception {

    }
}


