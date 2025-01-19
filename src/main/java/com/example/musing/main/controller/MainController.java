package com.example.musing.main.controller;

import com.example.musing.board.dto.GenreBoardDto;
import com.example.musing.common.dto.ResponseDto;
import com.example.musing.main.dto.LoginMainPageDto;
import com.example.musing.main.dto.NotLoginMainPageDto;
import com.example.musing.main.service.MainService;
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

    @Operation(summary = "로그인 테스트용",
            description = "localhost:8090 기준 http://localhost:8090/oauth2/authorization/google 로 접근<br>" +
                    "로그인 이후 Bearer 토큰 발급받아서 PostMan으로 테스트")
    @GetMapping("test")
    public ResponseDto<String> loginCheck(Principal principal) {
        return ResponseDto.of("", "로그인 성공, 유저이름 : " + userService.findById(principal.getName()).getUsername());
    }

    @Operation(summary = "메인 페이지 이동", description = "메인페이지로 이동할 때 접근한 사용자의 로그인 정보에 따라 전송하는 DTO 다르게 할 예정<br>" +
            "비로그인 : 음악 추천 게시판 및 공지사항 정보<br>" +
            "로그인 : 음악 추천 게시판 및 공지사항, 곡 추천, 좋아요한 음악, 자신의 태그 정보<br>" +
            "<b>반환 타입이 두가지 이니 Example Value 말고 schema도 확인해주세요</b>", responses = {
            @ApiResponse(responseCode = "200", description = "Success<br>" +
                    "<b>JSON 타입의 {}와 []를 구분하여 확인해주세요. []에 경우에는 여러개가 들어갈 부분입니다.</b>",
                    content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {NotLoginMainPageDto.class, LoginMainPageDto.class}))
            )
    })
    @GetMapping("main")
    public ResponseDto<Object> mainPage(Principal principal) {//비로그인 기준 Dto와 로그인 기준 Dto파일을 다르게 보내기 위해 와일드카드 사용
        //메인페이지 로그인 전에 시큐리티 권한확인하여 로그인상태를 구분,[ROLE_USER, ROLE_ADMIN, ROLE_ANONYMOUS]로 구분
        String modalCheck = "notLogIn";
        if (checkRole()) { //로그인 여부 체크
            String check = userService.checkInputTags(principal.getName());//유저가 분위기 및 장르, 좋아하는 아티스트를 넣었는지 확인하기
            modalCheck = check;
            if (check.equals("pass")) {// 최초 로그인 이후의 회원가입 단계에서 다 작성하였을 때
                LoginMainPageDto mainPageDto = mainService.LoginMainPage(principal.getName(), modalCheck);
                return ResponseDto.of(mainPageDto, "모달창 입력을 다 한 상태입니다.");
            } else {
                NotLoginMainPageDto mainPageDto = mainService.notLoginMainPage(modalCheck);
                return ResponseDto.of(mainPageDto, "모달창 입력을 완료 하지않았습니다.");
            }
        }// 작성이 다 안끝났을 경우에는 비로그인 Dto를 띄우고 모달창 추가 호출해야함
        NotLoginMainPageDto mainPageDto = mainService.notLoginMainPage(modalCheck);
        return ResponseDto.of(mainPageDto, "로그인을 하지 않았습니다.");
    }

    @GetMapping("main/genre")
    @Operation(summary = "유저가 좋아하는 장르 부분의 버튼을 클릭 시 해당 장르 최신 5개이하 게시글",
            description = "우선 최신순으로 해두고 데이터 형식 그대로 추천식으로 바꿀 예정")
    public ResponseDto<List<GenreBoardDto>> genreForm(@RequestParam(name = "genre") String genre) {
        return ResponseDto.of(mainService.selcetGenre(genre));
    }

    @PostMapping("/modal/like/genres")
    @Operation(summary = "최초 로그인 이후 선호하는 장르 및 분위기, 아티스트를 입력하지 않았을 때 : 장르 선택",
            description = "클라이언트에서 버튼 클릭으로 선택하는 부분을 ex:['락','블루스','힙합']같이 리스트 형태의 문자열로 저장하여 보내주면 됩니다.<br>" +
                    "적어도 1개 이상의 버튼을 클릭하여 저장하여야 합니다." +
                    "DB에 그대로 저장했다가 백엔드에서 클라이언트로 보내줄 때 특정 기호및 띄어쓰기부분 제거 후 리스트로 만들어 반환할 예정입니다.<br>" +
                    "해당 도메인 다음 musing/modal/like/moods로 이동하면 됩니다.")
    public ResponseDto<String> selectModalGenre(@RequestBody List<Long> selectGenres) { //로그인 접속한 유저의 장르선택 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveGenres(userId, selectGenres);
        return ResponseDto.of("", "장르 선택 모달창 완료");
    }

    @PostMapping("/modal/like/moods")
    @Operation(summary = "최초 로그인 이후 선호하는 장르 및 분위기, 아티스트를 입력하지 않았을 때 : 분위기 선택",
            description = "클라이언트에서 버튼 클릭으로 선택하는 부분을 ex:['신나는','조용한','슬픈']같이 리스트 형태의 문자열로 저장하여 보내주면 됩니다.<br>" +
                    "적어도 1개 이상의 버튼을 클릭하여 저장하여야 합니다." +
                    "DB에 그대로 저장했다가 백엔드에서 클라이언트로 보내줄 때 특정 기호및 띄어쓰기부분 제거 후 리스트로 만들어 반환할 예정입니다.<br>" +
                    "해당 도메인 다음 musing/modal/like/artists 이동하면 됩니다.")
    public ResponseDto<String> selectModalMood(@RequestBody List<Long> selectMoods) { //로그인 접속한 유저의 장르선택 저장
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveMoods(userId, selectMoods);
        return ResponseDto.of("", "장르 선택 모달창 완료");
    }

    @PostMapping("/modal/like/artists")
    @Operation(summary = "최초 로그인 이후 선호하는 장르 및 분위기, 아티스트를 입력하지 않았을 때 : 아티스트 입력",
            description = "클라이언트에서 입력하는 부분을 ex:['블랙핑크','AC/DC','EricClapton']같이 리스트로 저장하여 보내주면 됩니다.<br>" +
                    "아무것도 입력하지 않았을 때에는 null 값으로 보내주어야하며, 하나의 아티스트 작성 이후는 띄어쓰기를 제외한 하나의 단어로 리스트 형태의 문자열로 저장하여 보내주면 됩니다.<br>" +
                    "DB에 그대로 저장했다가 백엔드에서 클라이언트로 보내줄 때 특정 기호및 띄어쓰기부분 제거 후 리스트로 만들어 반환할 예정입니다.<br>" +
                    "해당 도메인 다음 musing/main을 다시 이동하면 됩니다.")
    public ResponseDto<String> selectModalArtists(@RequestBody(required = false) List<Long> selectArtists) { //로그인 접속한 유저의 장르선택 저장
        //null값 허용을 위해 required 설정 false
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.saveArtists(userId, selectArtists);
        return ResponseDto.of("selectArtists", "아티스트 선택 모달창 완료");
    }

    private boolean checkRole() { //로그인했는지와 유저인지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return true;
        }
        return false;
    }

}
