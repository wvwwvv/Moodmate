package com.example.backend.Service;

import com.example.backend.Dto.TermsAgreementDto;
import com.example.backend.Entity.User;
import com.example.backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void agreeTerms(User user, TermsAgreementDto termsAgreementDto) {

        // 자바 객체의 값만 변경 하면 Transactional 이 알아서 DB에 UPDATE 쿼리를 실행
        user.setTermsAgreed(termsAgreementDto.isTermsAgreed());
        user.setPrivacyAgreed(termsAgreementDto.isPrivacyAgreed());
        user.setTermsVersion(termsAgreementDto.getTermsVersion());
        user.setPrivacyVersion(termsAgreementDto.getPrivacyVersion());
        user.setTermsAgreedAt(LocalDateTime.now());
        user.setPrivacyAgreedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
