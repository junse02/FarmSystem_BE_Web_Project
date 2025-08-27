package com.example.board.user.controller;

import com.example.board.common.response.ApiResponse;
import com.example.board.user.dto.UserResponse;
import com.example.board.user.dto.UserUpdateRequest;
import com.example.board.user.service.UserMeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeService meService;

    // GET /users/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(meService.getMe(userId)));
    }

    // PATCH /users/me  (이름 변경만 허용)
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(meService.updateName(userId, req)));
    }

    // DELETE /users/me  (soft delete)
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal Long userId) {
        meService.softDelete(userId);
        return ResponseEntity.noContent().build();
    }
}
