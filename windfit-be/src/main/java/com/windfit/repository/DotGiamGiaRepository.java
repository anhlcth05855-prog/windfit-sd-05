package com.windfit.repository;

import com.windfit.entity.DotGiamGia;
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
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Long>, JpaSpecificationExecutor<DotGiamGia> {
    Optional<DotGiamGia> findByMaDotGiamGia(String maDotGiamGia);

    boolean existsByMaDotGiamGia(String maDotGiamGia);

    @Query("SELECT d FROM DotGiamGia d WHERE " +
           "(:keyword IS NULL OR LOWER(d.maDotGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.tenDotGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:trangThai IS NULL OR d.trangThai = :trangThai) AND " +
           "(:tuNgay IS NULL OR d.ngayBatDau >= :tuNgay) AND " +
           "(:denNgay IS NULL OR d.ngayKetThuc <= :denNgay)")
    Page<DotGiamGia> searchDotGiamGia(
            @Param("keyword") String keyword,
            @Param("trangThai") Integer trangThai,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );

    @Query("SELECT d FROM DotGiamGia d WHERE d.trangThai = 1 AND :now >= d.ngayBatDau AND :now <= d.ngayKetThuc")
    List<DotGiamGia> findActiveCampaigns(@Param("now") LocalDateTime now);
}
