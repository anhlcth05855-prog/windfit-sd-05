package com.windfit.repository;

import com.windfit.entity.SanPhamChiTiet;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Long> {
    List<SanPhamChiTiet> findBySanPhamId(Long sanPhamId);

    List<SanPhamChiTiet> findBySanPhamIdAndTrangThai(Long sanPhamId, Integer trangThai);

    Optional<SanPhamChiTiet> findByMaSpct(String maSpct);

    Optional<SanPhamChiTiet> findByMaVach(String maVach);

    boolean existsByMaSpct(String maSpct);

    Optional<SanPhamChiTiet> findBySanPhamIdAndMauSacIdAndKichCoId(Long sanPhamId, Long mauSacId, Long kichCoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SanPhamChiTiet s WHERE s.id = :id")
    Optional<SanPhamChiTiet> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT s FROM SanPhamChiTiet s JOIN s.sanPham p WHERE " +
           "(:keyword IS NULL OR LOWER(s.maSpct) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR s.maVach LIKE CONCAT('%', :keyword, '%')) AND " +
           "(:idSanPham IS NULL OR p.id = :idSanPham) AND " +
           "(:trangThai IS NULL OR s.trangThai = :trangThai)")
    Page<SanPhamChiTiet> searchSanPhamChiTiet(
            @Param("keyword") String keyword,
            @Param("idSanPham") Long idSanPham,
            @Param("trangThai") Integer trangThai,
            Pageable pageable
    );
}
