package com.windfit.repository;

import com.windfit.entity.MonTheThao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonTheThaoRepository extends JpaRepository<MonTheThao, Long> {
    Optional<MonTheThao> findByMaMonTheThao(String maMonTheThao);
    List<MonTheThao> findByTrangThaiOrderByTenMonTheThaoAsc(Integer trangThai);
    boolean existsByMaMonTheThao(String maMonTheThao);
}
