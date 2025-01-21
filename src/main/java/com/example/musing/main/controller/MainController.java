package com.example.musing.main.controller;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.genre.dto.GenreDto;
import com.example.musing.genre.service.GenreService;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;
import com.example.musing.main.service.MainService;
import com.example.musing.mood.dto.MoodDto;
import com.example.musing.mood.service.MoodService;
import com.example.musing.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/musing")
@RequiredArgsConstructor
@Tag(name = "메인 페이지 관련 도메인", description = "메인 페이지 및 모달 창")
public class MainController {
    //http://localhost:8090/oauth2/authorization/google //구글 로그인
    //http://localhost:8090/swagger-ui/index.html //스웨거
    private final UserService userService;
    private final MainService mainService;
    private final GenreService genreService;
    private final MoodService moodService;
    
    @Operation(summary = "메인 페이지", description = "메인페이지로 이동할 때 접근한 사용자의 로그인 정보에 따라 전송하는 DTO 다르게 할 예정<br>" +
            "비로그인 : 음악 추천 게시판 및 공지사항 정보<br>" +
            "로그인 : 음악 추천 게시판 및 공지사항, 곡 추천, 좋아요한 음악, 자신의 태그 정보<br>" +
            "<b>반환 타입이 두가지 이니 Example Value 말고 schema도 확인해주세요</b>", responses = {
            @ApiResponse(responseCode = "200", description = "Success<br>" +
                    "<b>JSON 타입의 {}와 []를 구분하여 확인해주세요. []에 경우에는 여러개가 들어갈 부분입니다.</b>",
                    content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {NotLoginMainPageDto.class, LoginMainPageDto.class}))
            )
    })
    @GetMapping("main")
    public ResponseDto<Object> mainPage(Principal principal) {
        //메인페이지 로그인 전에 시큐리티 권한확인하여 로그인상태를 구분,[ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS]로 구분
        String modalCheck = "notLogIn";
        if (checkRole()) { //로그인 여부 체크
            String check = userService.checkInputTags(principal.getName());//유저가 분위기 및 장르, 좋아하는 아티스트를 넣었는지 확인하기
            modalCheck = check;

            if (check.equals("pass")) {// 최초 로그인 이후의 회원가입 단계에서 다 작성하였을 때
                LoginMainPageDto mainPageDto = mainService.LoginMainPage(principal.getName(), modalCheck);
                return ResponseDto.of(mainPageDto, "모달창 입력을 다 한 상태입니다.");
            }
            NotLoginMainPageDto mainPageDto = mainService.notLoginMainPage(modalCheck);
            return ResponseDto.of(mainPageDto, "모달창 입력을 완료 하지않았습니다.");
        }
        // 작성이 다 안끝났을 경우에는 비로그인 Dto를 띄우고 모달창 추가 호출해야함
        NotLoginMainPageDto mainPageDto = mainService.notLoginMainPage(modalCheck);
        return ResponseDto.of(mainPageDto, "로그인을 하지 않았습니다.");
    }

    @GetMapping("main/genre")
    @Operation(summary = "유저가 좋아하는 장르 클릭 시 해당 게시글 조회",
            description = "우선 최신순으로 해두고 데이터 형식 그대로 추천식으로 바꿀 예정")
    public ResponseDto<List<GenreBoardDto>> genreForm(@RequestParam(name = "genre") String genre) {
        return ResponseDto.of(mainService.selcetGenre(genre));
    }

    @GetMapping("/modal/like/genres")
    @Operation(summary = "유저가 좋아하는 장르를 고르는 모달창",
            description = "클라이언트에서 Post 요청을 보낼 때 장르의 ID를 List로 보내주면 됩니다.")
    public ResponseDto<List<GenreDto>> selectGenreForm() {
        return ResponseDto.of(genreService.getGenreDtos(), "");
    }

    @PostMapping("/modal/like/genres")
    @Operation(summary = "유저가 좋아하는 장르 선택 Post요청",
            description = "장르의 ID를 List로 보내주면 됩니다.<br>" +
                    "적어도 1개 이상의 버튼을 클릭하여 저장하여야 합니다." +
                    "해당 도메인 다음 musing/modal/like/moods로 이동하면 됩니다.")
    public ResponseDto<String> selectGenre(@RequestBody List<Long> selectGenres) { //로그인 접속한 유저의 장르선택 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveGenres(userId, selectGenres);
        return ResponseDto.of("", "장르 선택 모달창 완료");
    }

    @GetMapping("/modal/like/moods")
    @Operation(summary = "유저가 좋아하는 분위기를 고르는 모달창",
            description = "클라이언트에서 Post요청으로 분위기의 Id를 List로 보내주면 됩니다.")
    public ResponseDto<List<MoodDto>> selectMoodForm() {
        return ResponseDto.of(moodService.getMoodDtos(), "");
    }

    @PostMapping("/modal/like/moods")
    @Operation(summary = "유저가 좋아하는 분위기 선택 Post요청",
            description = "분위기의 ID를 List로 보내주면 됩니다.<br>" +
                    "적어도 1개 이상의 버튼을 클릭하여 저장하여야 합니다." +
                    "해당 도메인 다음 musing/modal/like/artists로 이동하면 됩니다.")
    public ResponseDto<String> selectMood(@RequestBody List<Long> selectMoods) { //로그인 접속한 유저의 장르선택 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveMoods(userId, selectMoods);
        return ResponseDto.of("", "장르 선택 모달창 완료");
    }

    @PostMapping("/modal/like/artists")
    @Operation(summary = "유저가 좋아하는 아티스트 입력",
            description = "클라이언트에서 입력하는 부분을 ex:['블랙핑크','AC/DC','Eric Clapton']같이 리스트로 저장하여 보내주면 됩니다.<br>" +
                    "아무것도 입력하지 않았을 때에는 null 값으로 보내주어야하며, 하나의 단어로 리스트 형태의 문자열로 저장하여 보내주면 됩니다.<br>" +
                    "해당 도메인 다음 musing/main을 다시 이동하면 됩니다.")
    public ResponseDto<String> selectArtists(@RequestBody(required = false) List<String> selectArtists) {
        //null값 허용을 위해 required 설정 false
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveArtists(userId, selectArtists);
        return ResponseDto.of("selectArtists", "아티스트 선택 모달창 완료");
    }

    private boolean checkRole() { //로그인했는지와 유저인지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //로그 확인용
        System.out.println("checkRole:" + auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        System.out.println("유저 등급 확인: "+ auth.getAuthorities().stream().findFirst());

        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

}
