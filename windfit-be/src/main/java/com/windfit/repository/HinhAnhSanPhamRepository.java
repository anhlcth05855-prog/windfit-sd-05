package com.windfit.repository;

import com.windfit.entity.HinhAnhSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Long> {
    List<HinhAnhSanPham> findBySanPhamId(Long sanPhamId);
    Optional<HinhAnhSanPham> findBySanPhamIdAndIsAnhChinhTrue(Long sanPhamId);
    void deleteBySanPhamId(Long sanPhamId);
}
