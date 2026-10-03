package com.windfit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_lieu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_chat_lieu", length = 50, nullable = false, unique = true)
    private String maChatLieu;

    @Column(name = "ten_chat_lieu", length = 150, nullable = false)
    private String tenChatLieu;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;
}
