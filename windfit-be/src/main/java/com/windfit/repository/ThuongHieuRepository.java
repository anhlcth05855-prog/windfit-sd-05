package com.windfit.repository;

import com.windfit.entity.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Long> {
    Optional<ThuongHieu> findByMaThuongHieu(String maThuongHieu);
    List<ThuongHieu> findByTrangThaiOrderByTenThuongHieuAsc(Integer trangThai);
    boolean existsByMaThuongHieu(String maThuongHieu);
}
