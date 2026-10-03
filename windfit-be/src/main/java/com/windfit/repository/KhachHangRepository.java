package com.windfit.repository;

import com.windfit.entity.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    Optional<KhachHang> findByMaKhachHang(String maKhachHang);

    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    Optional<KhachHang> findByEmail(String email);

    @Query("SELECT k FROM KhachHang k WHERE " +
           "(:keyword IS NULL OR LOWER(k.maKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR k.soDienThoai LIKE CONCAT('%', :keyword, '%') OR LOWER(k.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:trangThai IS NULL OR k.trangThai = :trangThai)")
    Page<KhachHang> searchKhachHang(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}
