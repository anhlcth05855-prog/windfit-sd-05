package com.windfit.repository;

import com.windfit.entity.KichCo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KichCoRepository extends JpaRepository<KichCo, Long> {
    Optional<KichCo> findByMaKichCo(String maKichCo);
    List<KichCo> findByTrangThaiOrderByTenKichCoAsc(Integer trangThai);
    boolean existsByMaKichCo(String maKichCo);
}
