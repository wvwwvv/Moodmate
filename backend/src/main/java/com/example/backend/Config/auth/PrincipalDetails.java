package com.example.backend.Config.auth;

import com.example.backend.Entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Spring Security가 로그인 사용자를 다루는 객체인 OAuth2User를 구현
@Getter
public class PrincipalDetails implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한을 반환하는 곳이지만, 지금은 사용하지 않으므로 비워둠
        Collection<GrantedAuthority> collect = new ArrayList<>();
        return collect;
    }

    @Override
    public String getName() {
        // 사용자 식별 고유 값을 반환. 여기서는 카카오 ID 사용
        return user.getKakaoId().toString();
    }

}