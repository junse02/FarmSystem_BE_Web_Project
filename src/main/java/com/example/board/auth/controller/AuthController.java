package com.example.board.auth.controller;

import com.example.board.auth.dto.LoginReq;
import com.example.board.auth.service.AuthService;
import com.example.board.auth.dto.SignupReq;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    public record RefreshReq(@NotBlank String refreshToken) {}

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReq req) {
        auth.signup(req.getEmail(), req.getPassword(), req.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {
        System.out.println(">>>> login controller 진입됨");
        System.out.println(">>>> 받은 req: " + req);
        var data = auth.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshReq req) {
        var data = auth.refresh(req.refreshToken());
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody RefreshReq req) {
        auth.logout(userId, req.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
