package com.coho.invitation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Order(0)
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = parseBearerToken(request);
        User user = parseMemberSpec(token);
        AbstractAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, token,user.getAuthorities());
        authenticated.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        request.setAttribute("user",user);
        request.setAttribute("uid",user.getUsername());

        filterChain.doFilter(request,response);
    }

    /* 토큰 정보 추출 */
    private String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.substring(0,7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

    private User parseMemberSpec(String token) {
        System.out.println(token);

        String[] tokenSplit = Optional.ofNullable(token)
                .filter(subject -> subject.length() >= 10)
                .map(tokenProvider::validateTokenAndGetSubject)
                .orElse("anonymous:")
//                .orElse("anonymous:ROLE_ANONYMOUS")     // filter와 map을 통과하지 못했을 때
                .split(":");

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (tokenSplit != null && tokenSplit.length == 2)
            authorities.add(new SimpleGrantedAuthority(tokenSplit[1]));

        return new User(tokenSplit[0],"", authorities);

//        return new User(tokenSplit[0],"", List.of(new SimpleGrantedAuthority(tokenSplit[1])));
    }

}
