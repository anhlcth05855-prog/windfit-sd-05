package com.windfit.repository;

import com.windfit.entity.DotGiamGiaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DotGiamGiaChiTietRepository extends JpaRepository<DotGiamGiaChiTiet, Long> {
    List<DotGiamGiaChiTiet> findByDotGiamGiaId(Long dotGiamGiaId);

    List<DotGiamGiaChiTiet> findByDotGiamGiaIdAndTrangThai(Long dotGiamGiaId, Integer trangThai);

    @Query("SELECT c FROM DotGiamGiaChiTiet c WHERE c.sanPhamChiTiet.id = :spctId AND c.trangThai = 1 AND c.dotGiamGia.trangThai = 1 AND :now >= c.dotGiamGia.ngayBatDau AND :now <= c.dotGiamGia.ngayKetThuc ORDER BY c.phanTramGiam DESC")
    List<DotGiamGiaChiTiet> findActiveDiscountsForSpct(@Param("spctId") Long spctId, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM DotGiamGiaChiTiet c WHERE c.sanPhamChiTiet.id IN :spctIds AND c.trangThai = 1 AND c.dotGiamGia.trangThai = 1 AND :now >= c.dotGiamGia.ngayBatDau AND :now <= c.dotGiamGia.ngayKetThuc")
    List<DotGiamGiaChiTiet> findActiveDiscountsForSpctList(@Param("spctIds") List<Long> spctIds, @Param("now") LocalDateTime now);
}
