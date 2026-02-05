package com.example.backend.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TermsAgreementDto {
    private boolean termsAgreed;
    private boolean privacyAgreed;
    private String termsVersion;
    private String privacyVersion;
}
