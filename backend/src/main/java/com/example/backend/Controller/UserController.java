package com.example.backend.Controller;

import com.example.backend.Config.auth.PrincipalDetails;
import com.example.backend.Dto.TermsAgreementDto;
import com.example.backend.Dto.UserDto;
import com.example.backend.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(401).build();
        }

        UserDto userDto = new UserDto(
                principalDetails.getUser().getKakaoId(),
                principalDetails.getUser().getKakaoName()
        );
        return ResponseEntity.ok(userDto);
    }

    //약관 동의 처리 api
    @PostMapping("/agree-terms")
    public ResponseEntity<Void> agreeTerms(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody TermsAgreementDto termsAgreementDto
            ) {
        if (principalDetails == null) {
            //인증 되지 않은 사용자 이면
            return ResponseEntity.status(401).build();
        }

        //kakaoId 와 동의 정보 넘김
        userService.agreeTerms(principalDetails.getUser(), termsAgreementDto);

        return ResponseEntity.ok().build();
    }
}
