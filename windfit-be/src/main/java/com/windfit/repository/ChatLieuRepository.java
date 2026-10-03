package com.windfit.repository;

import com.windfit.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Long> {
    Optional<ChatLieu> findByMaChatLieu(String maChatLieu);
    List<ChatLieu> findByTrangThaiOrderByTenChatLieuAsc(Integer trangThai);
    boolean existsByMaChatLieu(String maChatLieu);
}
