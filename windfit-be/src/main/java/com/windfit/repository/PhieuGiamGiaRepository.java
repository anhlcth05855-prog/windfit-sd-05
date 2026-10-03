package com.windfit.repository;

import com.windfit.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Long> {
    Optional<PhieuGiamGia> findByMaPhieuGiamGia(String maPhieuGiamGia);

    boolean existsByMaPhieuGiamGia(String maPhieuGiamGia);

    @Query("SELECT p FROM PhieuGiamGia p WHERE " +
           "(:keyword IS NULL OR LOWER(p.maPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.tenPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:loaiGiamGia IS NULL OR p.loaiGiamGia = :loaiGiamGia) AND " +
           "(:trangThai IS NULL OR p.trangThai = :trangThai)")
    Page<PhieuGiamGia> searchPhieuGiamGia(
            @Param("keyword") String keyword,
            @Param("loaiGiamGia") String loaiGiamGia,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 1 AND :now >= p.ngayBatDau AND :now <= p.ngayKetThuc AND p.soLuongDaDung < p.soLuong")
    List<PhieuGiamGia> findAvailableVouchers(@Param("now") LocalDateTime now);
}
