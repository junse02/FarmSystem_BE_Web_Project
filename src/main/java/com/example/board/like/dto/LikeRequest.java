package com.example.board.like.dto;

import jakarta.validation.constraints.NotNull;

public record LikeRequest(
        @NotNull Boolean like
) {}