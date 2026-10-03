package com.windfit.repository;

import com.windfit.entity.KhachHangPhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangPhieuGiamGiaRepository extends JpaRepository<KhachHangPhieuGiamGia, Long> {
    Optional<KhachHangPhieuGiamGia> findByKhachHangIdAndPhieuGiamGiaId(Long khachHangId, Long phieuGiamGiaId);
    List<KhachHangPhieuGiamGia> findByKhachHangId(Long khachHangId);
    List<KhachHangPhieuGiamGia> findByPhieuGiamGiaId(Long phieuGiamGiaId);
}
