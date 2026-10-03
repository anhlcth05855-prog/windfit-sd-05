package com.windfit.repository;

import com.windfit.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Long>, JpaSpecificationExecutor<PhieuGiamGia> {
    Optional<PhieuGiamGia> findByMaPhieuGiamGia(String maPhieuGiamGia);

    boolean existsByMaPhieuGiamGia(String maPhieuGiamGia);

    Optional<PhieuGiamGia> findTopByOrderByIdDesc();

    @Query("SELECT p FROM PhieuGiamGia p WHERE " +
           "(:keyword IS NULL OR LOWER(p.maPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.tenPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:loaiGiamGia IS NULL OR p.loaiGiamGia = :loaiGiamGia) AND " +
           "(:nhomPhieu IS NULL OR p.nhomPhieu = :nhomPhieu) AND " +
           "(:trangThai IS NULL OR p.trangThai = :trangThai) AND " +
           "(:tuNgay IS NULL OR p.ngayBatDau >= :tuNgay) AND " +
           "(:denNgay IS NULL OR p.ngayKetThuc <= :denNgay)")
    Page<PhieuGiamGia> searchPhieuGiamGia(
            @Param("keyword") String keyword,
            @Param("loaiGiamGia") String loaiGiamGia,
            @Param("nhomPhieu") String nhomPhieu,
            @Param("trangThai") Integer trangThai,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );

    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 1 AND :now >= p.ngayBatDau AND :now <= p.ngayKetThuc AND p.soLuongDaDung < p.soLuong")
    List<PhieuGiamGia> findAvailableVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM PhieuGiamGia p WHERE p.trangThai = 1 AND :now >= p.ngayBatDau AND :now <= p.ngayKetThuc AND p.soLuongDaDung < p.soLuong")
    long countActiveVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM PhieuGiamGia p WHERE p.trangThai = 1 AND :now < p.ngayBatDau")
    long countUpcomingVouchers(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(p) FROM PhieuGiamGia p WHERE p.trangThai = 1 AND (:now > p.ngayKetThuc OR p.soLuongDaDung >= p.soLuong)")
    long countExpiredOrDepletedVouchers(@Param("now") LocalDateTime now);

    long countByTrangThai(Integer trangThai);
}
