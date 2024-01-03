package com.example.A201.member.controller;

import com.example.A201.member.dto.AuthDto;
import com.example.A201.member.dto.MemberDTO;
import com.example.A201.member.service.AuthService;
import com.example.A201.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class AuthApiController {
    private final AuthService authService;
    private final BCryptPasswordEncoder encoder;
    private final MemberService memberService;

    private final long COOKIE_EXPIRATION = 7776000; // 90일

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody AuthDto.SignupDto signupDto) throws Exception {
        String encodedPassword = encoder.encode(signupDto.getPassword());
        AuthDto.SignupDto newSignupDto = AuthDto.SignupDto.encodePassword(signupDto, encodedPassword);
        memberService.registerMember(newSignupDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // loginId
    @GetMapping("/check/{loginId}")
    public ResponseEntity<?> checkDuplication(@PathVariable String loginId) {
        if (memberService.existsByLoginId(loginId)) {
            return ResponseEntity.status(HttpStatus.OK).body("이미 사용 중인 loginId 입니다.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 loginId 입니다.");
        }
    }

    // 로그인 -> 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDto.LoginDto loginDto) {
        // User 등록 및 Refresh Token 저장
        AuthDto.TokenDto tokenDto = authService.login(loginDto);

        String loginId = loginDto.getLoginId();
        MemberDTO member = memberService.getMemberByLoginId(loginId);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(true)
                .secure(false) // 로컬에서는 false, 실제 환경에서는 true로 변경
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .body(member);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String requestAccessToken) {
        if (!authService.validate(requestAccessToken)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 재발급 필요
        }
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader() Map<String, String> headers) {
        System.out.println("headers: " + headers);
        String requestAccessToken = headers.get("authorization");
        String requestRefreshToken = headers.get("refresh_token");
        AuthDto.TokenDto reissuedTokenDto = authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) {
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", reissuedTokenDto.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();
        } else {
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                    .maxAge(0)
                    .path("/")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .build();
        }
    }

    // 로그아웃
    @PostMapping("/logout/{memberId}")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String requestAccessToken,
                                    @PathVariable("memberId") String memberId) {
        log.debug("asdasdasdas");
        log.debug(requestAccessToken);
        authService.logout(requestAccessToken, memberId);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

}
