package com.thp.project.vintud.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thp.project.vintud.dto.UserDto;
import com.thp.project.vintud.entity.Role;
import com.thp.project.vintud.entity.impl.RoleImpl;
import com.thp.project.vintud.service.UserService;
import com.thp.project.vintud.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Transactional
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ca')")
    @GetMapping(value = "/user")
    @ResponseBody
    public List<UserDto> all() {
        return userService.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/user")
    public void addUser(@RequestBody UserDto user) {
        userService.addUser(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/user/{userId}")
    @ResponseBody
    public UserDto getUser(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/user/{userId}")
    public void delete(@PathVariable int userId) {
        userService.delete(userId);
    }

    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(JWTUtil.PREFIX)) {
            try {
                String refresh_token = authorizationHeader.substring(JWTUtil.PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                UserDto user = userService.getUserByUsername(username);
                List<RoleImpl> roles = new ArrayList<>();
                roles.add(user.getRole_id());
                String access_token = JWT.create()
                        .withSubject(user.getPseudo())
                        .withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", roles.stream().map(RoleImpl::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
            }
        }
    }
}
