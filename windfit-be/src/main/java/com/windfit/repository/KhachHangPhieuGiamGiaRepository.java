package com.windfit.repository;

import com.windfit.entity.KhachHangPhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangPhieuGiamGiaRepository extends JpaRepository<KhachHangPhieuGiamGia, Long> {
    Optional<KhachHangPhieuGiamGia> findByKhachHangIdAndPhieuGiamGiaId(Long khachHangId, Long phieuGiamGiaId);

    List<KhachHangPhieuGiamGia> findByKhachHangId(Long khachHangId);

    List<KhachHangPhieuGiamGia> findByPhieuGiamGiaId(Long phieuGiamGiaId);

    List<KhachHangPhieuGiamGia> findByPhieuGiamGiaIdAndTrangThai(Long phieuGiamGiaId, Integer trangThai);

    @Query("SELECT k FROM KhachHangPhieuGiamGia k WHERE k.phieuGiamGia.id = :phieuId AND k.trangThai = 1 AND (" +
           ":keyword IS NULL OR " +
           "LOWER(k.khachHang.maKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(k.khachHang.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "k.khachHang.soDienThoai LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(k.khachHang.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<KhachHangPhieuGiamGia> searchAssignedCustomers(
            @Param("phieuId") Long phieuId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
