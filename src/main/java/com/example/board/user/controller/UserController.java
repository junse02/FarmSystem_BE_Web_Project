package com.example.board.user.controller;

import com.example.board.common.response.ApiResponse;
import com.example.board.user.dto.UserCreateRequest;
import com.example.board.user.dto.UserResponse;
import com.example.board.user.dto.UserUpdateRequest;
import com.example.board.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crud/users")  // 명세서와 충돌하지 않는 CRUD 전용 경로
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 생성 (Create) - POST /api/v1/crud/users
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse response = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("이미 존재하는 이메일")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error("DUPLICATE_EMAIL", e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        }
    }

    // 모든 사용자 조회 (Read) - GET /api/v1/crud/users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 특정 사용자 조회 (Read) - GET /api/v1/crud/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse response = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        }
    }

    // 사용자 정보 수정 (Update) - PUT /api/v1/crud/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        }
    }

    // 사용자 삭제 (Delete) - DELETE /api/v1/crud/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        }
    }
}
