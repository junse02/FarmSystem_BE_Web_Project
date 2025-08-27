package com.example.board.common.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate @Column(nullable=false, updatable=false)
    private Instant createdAt;
    @LastModifiedDate @Column(nullable=false)
    private Instant updatedAt;
    @Column private Instant isDeleted;

    public Instant getCreatedAt(){ return createdAt; }
    public Instant getUpdatedAt(){ return updatedAt; }
    public Instant getIsDeleted(){ return isDeleted; }
    public void setIsDeleted(Instant t){ this.isDeleted = t; }
}
