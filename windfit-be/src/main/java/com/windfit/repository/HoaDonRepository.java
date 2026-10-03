package com.windfit.repository;

import com.windfit.entity.HoaDon;
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
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);

    List<HoaDon> findByTrangThaiHoaDonOrderByNgayTaoDesc(String trangThaiHoaDon);

    List<HoaDon> findByLoaiHoaDonOrderByNgayTaoDesc(String loaiHoaDon);

    @Query("SELECT h FROM HoaDon h WHERE " +
           "(:keyword IS NULL OR LOWER(h.maHoaDon) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(h.tenNguoiNhan) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           " OR LOWER(h.sdtNguoiNhan) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:trangThai IS NULL OR h.trangThaiHoaDon = :trangThai) " +
           "AND (:kenhBan IS NULL OR h.loaiHoaDon = :kenhBan) " +
           "AND (:tuNgay IS NULL OR h.ngayTao >= :tuNgay) " +
           "AND (:denNgay IS NULL OR h.ngayTao <= :denNgay) " +
           "ORDER BY h.ngayTao DESC")
    Page<HoaDon> timKiemHoaDon(
            @Param("keyword") String keyword,
            @Param("trangThai") String trangThai,
            @Param("kenhBan") String kenhBan,
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay,
            Pageable pageable
    );
}
