package com.windfit.repository;

import com.windfit.entity.DotGiamGiaChiTiet;
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
public interface DotGiamGiaChiTietRepository extends JpaRepository<DotGiamGiaChiTiet, Long> {
    List<DotGiamGiaChiTiet> findByDotGiamGiaId(Long dotGiamGiaId);

    List<DotGiamGiaChiTiet> findByDotGiamGiaIdAndTrangThai(Long dotGiamGiaId, Integer trangThai);

    Optional<DotGiamGiaChiTiet> findByDotGiamGiaIdAndSanPhamChiTietId(Long dotGiamGiaId, Long spctId);

    @Query("SELECT c FROM DotGiamGiaChiTiet c " +
           "WHERE c.dotGiamGia.id = :dotGiamGiaId AND c.trangThai = 1 AND (" +
           ":keyword IS NULL OR " +
           "LOWER(c.sanPhamChiTiet.maSpct) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.sanPhamChiTiet.sanPham.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DotGiamGiaChiTiet> searchByDotGiamGiaId(
            @Param("dotGiamGiaId") Long dotGiamGiaId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT c FROM DotGiamGiaChiTiet c " +
           "WHERE c.sanPhamChiTiet.id = :spctId " +
           "AND c.trangThai = 1 " +
           "AND c.dotGiamGia.trangThai = 1 " +
           "AND (:excludeDotId IS NULL OR c.dotGiamGia.id != :excludeDotId) " +
           "AND (c.dotGiamGia.ngayBatDau <= :ngayKetThuc AND c.dotGiamGia.ngayKetThuc >= :ngayBatDau)")
    List<DotGiamGiaChiTiet> findConflictingDiscounts(
            @Param("spctId") Long spctId,
            @Param("ngayBatDau") LocalDateTime ngayBatDau,
            @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
            @Param("excludeDotId") Long excludeDotId
    );

    @Query("SELECT c FROM DotGiamGiaChiTiet c " +
           "WHERE c.sanPhamChiTiet.id = :spctId " +
           "AND c.trangThai = 1 " +
           "AND c.dotGiamGia.trangThai = 1 " +
           "AND :now >= c.dotGiamGia.ngayBatDau " +
           "AND :now <= c.dotGiamGia.ngayKetThuc " +
           "ORDER BY c.phanTramGiam DESC")
    List<DotGiamGiaChiTiet> findActiveDiscountsForSpct(@Param("spctId") Long spctId, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM DotGiamGiaChiTiet c " +
           "WHERE c.sanPhamChiTiet.id IN :spctIds " +
           "AND c.trangThai = 1 " +
           "AND c.dotGiamGia.trangThai = 1 " +
           "AND :now >= c.dotGiamGia.ngayBatDau " +
           "AND :now <= c.dotGiamGia.ngayKetThuc")
    List<DotGiamGiaChiTiet> findActiveDiscountsForSpctList(@Param("spctIds") List<Long> spctIds, @Param("now") LocalDateTime now);
}
