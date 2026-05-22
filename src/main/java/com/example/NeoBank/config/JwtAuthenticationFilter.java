package com.example.NeoBank.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // classe responsavel por gerar, validar e ler dados do token
    private final TokenProvider tokenProvider;
    // service que busca o usuario no banco para montar a autenticacao do Spring
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // pega o token enviado no header Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

String token = authHeader.substring(7);

            if (tokenProvider.validateToken(token)) {
                // extrai o email do token para descobrir qual usuario esta fazendo a requisicao
                String username = tokenProvider.getUsernameFromToken(token);

                // carrega o usuario completo com roles/permissoes
                UserDetails userDetails  = userDetailsService.loadUserByUsername(username);

                // monta o objeto de autenticacao que o Spring usa internamente
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                     userDetails,null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // salva a autenticacao no contexto da request atual
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new BadCredentialsException("Token JWT invalido ou expirado");
            }
        }

        // continua o fluxo normal da requisicao
        filterChain.doFilter(request, response);
    }
}
