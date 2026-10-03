package com.windfit.service;

import com.windfit.dto.product.*;
import com.windfit.entity.*;
import com.windfit.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface SanPhamService {
    Page<SanPhamResponseDTO> getAllSanPham(SanPhamFilterRequest filter);
    SanPhamResponseDTO getSanPhamById(Long id);
    SanPhamResponseDTO createSanPham(SanPhamCreateRequestDTO request);
    SanPhamResponseDTO updateSanPham(Long id, SanPhamCreateRequestDTO request);
    void toggleStatusSanPham(Long id);
    List<SanPhamChiTietResponseDTO> getBienTheBySanPham(Long idSanPham);
    SanPhamChiTietResponseDTO getBienTheById(Long idSpct);
    SanPhamChiTietResponseDTO updateBienThe(Long idSpct, QuickUpdateVariantDTO request);
    SanPhamChiTietResponseDTO getBienTheByBarcode(String barcode);
    void toggleStatusBienThe(Long idSpct);
}

@Service
@RequiredArgsConstructor
class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final HinhAnhSanPhamRepository hinhAnhSanPhamRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final XuatXuRepository xuatXuRepository;
    private final KieuDangRepository kieuDangRepository;
    private final MonTheThaoRepository monTheThaoRepository;
    private final MauSacRepository mauSacRepository;
    private final KichCoRepository kichCoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<SanPhamResponseDTO> getAllSanPham(SanPhamFilterRequest filter) {
        Specification<SanPham> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
                String kw = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maSanPham")), kw),
                        cb.like(cb.lower(root.get("tenSanPham")), kw)
                ));
            }
            if (filter.getIdDanhMuc() != null) {
                predicates.add(cb.equal(root.get("danhMuc").get("id"), filter.getIdDanhMuc()));
            }
            if (filter.getIdThuongHieu() != null) {
                predicates.add(cb.equal(root.get("thuongHieu").get("id"), filter.getIdThuongHieu()));
            }
            if (filter.getIdChatLieu() != null) {
                predicates.add(cb.equal(root.get("chatLieu").get("id"), filter.getIdChatLieu()));
            }
            if (filter.getIdXuatXu() != null) {
                predicates.add(cb.equal(root.get("xuatXu").get("id"), filter.getIdXuatXu()));
            }
            if (filter.getIdKieuDang() != null) {
                predicates.add(cb.equal(root.get("kieuDang").get("id"), filter.getIdKieuDang()));
            }
            if (filter.getIdMonTheThao() != null) {
                predicates.add(cb.equal(root.get("monTheThao").get("id"), filter.getIdMonTheThao()));
            }
            if (filter.getTrangThai() != null) {
                predicates.add(cb.equal(root.get("trangThai"), filter.getTrangThai()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = "asc".equalsIgnoreCase(filter.getSortDir())
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        Page<SanPham> page = sanPhamRepository.findAll(spec, pageable);

        return page.map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamResponseDTO getSanPhamById(Long id) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        return mapToResponseDTO(sp);
    }

    @Override
    @Transactional
    public SanPhamResponseDTO createSanPham(SanPhamCreateRequestDTO request) {
        String maSp = request.getMaSanPham();
        if (maSp == null || maSp.trim().isEmpty()) {
            maSp = "SP" + System.currentTimeMillis() % 1000000;
        } else {
            maSp = maSp.trim().toUpperCase();
            if (sanPhamRepository.existsByMaSanPham(maSp)) {
                throw new RuntimeException("Mã sản phẩm '" + maSp + "' đã tồn tại!");
            }
        }

        DanhMuc dm = danhMucRepository.findById(request.getIdDanhMuc())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        ThuongHieu th = thuongHieuRepository.findById(request.getIdThuongHieu())
                .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
        ChatLieu cl = chatLieuRepository.findById(request.getIdChatLieu())
                .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại"));
        XuatXu xx = xuatXuRepository.findById(request.getIdXuatXu())
                .orElseThrow(() -> new RuntimeException("Xuất xứ không tồn tại"));
        KieuDang kd = kieuDangRepository.findById(request.getIdKieuDang())
                .orElseThrow(() -> new RuntimeException("Kiểu dáng không tồn tại"));
        MonTheThao mtt = monTheThaoRepository.findById(request.getIdMonTheThao())
                .orElseThrow(() -> new RuntimeException("Môn thể thao không tồn tại"));

        SanPham sanPham = SanPham.builder()
                .maSanPham(maSp)
                .tenSanPham(request.getTenSanPham().trim())
                .danhMuc(dm)
                .thuongHieu(th)
                .chatLieu(cl)
                .xuatXu(xx)
                .kieuDang(kd)
                .monTheThao(mtt)
                .doiTuong(request.getDoiTuong() != null ? request.getDoiTuong() : "Unisex")
                .tinhNang(request.getTinhNang())
                .moTaChiTiet(request.getMoTaChiTiet())
                .videoUrl(request.getVideoUrl())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .ngayTao(LocalDateTime.now())
                .ngaySua(LocalDateTime.now())
                .build();

        sanPham = sanPhamRepository.save(sanPham);

        List<String> images = request.getDanhSachAnh();
        if (images != null && !images.isEmpty()) {
            boolean hasMain = false;
            for (int i = 0; i < images.size(); i++) {
                String imgUrl = images.get(i).trim();
                if (imgUrl.isEmpty()) continue;
                boolean isMain = !hasMain && (imgUrl.equals(request.getAnhChinhUrl()) || i == 0);
                if (isMain) hasMain = true;

                HinhAnhSanPham hasp = HinhAnhSanPham.builder()
                        .sanPham(sanPham)
                        .urlAnh(imgUrl)
                        .isAnhChinh(isMain)
                        .build();
                hinhAnhSanPhamRepository.save(hasp);
            }
        }

        if (request.getBienTheList() != null) {
            int seq = 1;
            for (SanPhamChiTietRequestDTO vReq : request.getBienTheList()) {
                MauSac mauSac = mauSacRepository.findById(vReq.getIdMauSac())
                        .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại: " + vReq.getIdMauSac()));
                KichCo kichCo = kichCoRepository.findById(vReq.getIdKichCo())
                        .orElseThrow(() -> new RuntimeException("Kích cỡ không tồn tại: " + vReq.getIdKichCo()));

                String maSpct = vReq.getMaSpct();
                if (maSpct == null || maSpct.trim().isEmpty()) {
                    maSpct = sanPham.getMaSanPham() + "-" + mauSac.getMaMau() + "-" + kichCo.getMaKichCo();
                    if (sanPhamChiTietRepository.existsByMaSpct(maSpct)) {
                        maSpct += "-" + System.currentTimeMillis() % 1000;
                    }
                }

                String maVach = vReq.getMaVach();
                if (maVach == null || maVach.trim().isEmpty()) {
                    maVach = "893" + String.format("%09d", Math.abs((sanPham.getId() * 10000 + seq) % 1000000000L));
                }

                SanPhamChiTiet spct = SanPhamChiTiet.builder()
                        .sanPham(sanPham)
                        .mauSac(mauSac)
                        .kichCo(kichCo)
                        .maSpct(maSpct.trim())
                        .maVach(maVach.trim())
                        .giaBan(vReq.getGiaBan())
                        .soLuongTon(vReq.getSoLuongTon())
                        .trangThai(vReq.getTrangThai() != null ? vReq.getTrangThai() : 1)
                        .ngayTao(LocalDateTime.now())
                        .ngaySua(LocalDateTime.now())
                        .build();
                sanPhamChiTietRepository.save(spct);
                seq++;
            }
        }

        return mapToResponseDTO(sanPham);
    }

    @Override
    @Transactional
    public SanPhamResponseDTO updateSanPham(Long id, SanPhamCreateRequestDTO request) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        if (request.getIdDanhMuc() != null) {
            sanPham.setDanhMuc(danhMucRepository.findById(request.getIdDanhMuc())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại")));
        }
        if (request.getIdThuongHieu() != null) {
            sanPham.setThuongHieu(thuongHieuRepository.findById(request.getIdThuongHieu())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại")));
        }
        if (request.getIdChatLieu() != null) {
            sanPham.setChatLieu(chatLieuRepository.findById(request.getIdChatLieu())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại")));
        }
        if (request.getIdXuatXu() != null) {
            sanPham.setXuatXu(xuatXuRepository.findById(request.getIdXuatXu())
                    .orElseThrow(() -> new RuntimeException("Xuất xứ không tồn tại")));
        }
        if (request.getIdKieuDang() != null) {
            sanPham.setKieuDang(kieuDangRepository.findById(request.getIdKieuDang())
                    .orElseThrow(() -> new RuntimeException("Kiểu dáng không tồn tại")));
        }
        if (request.getIdMonTheThao() != null) {
            sanPham.setMonTheThao(monTheThaoRepository.findById(request.getIdMonTheThao())
                    .orElseThrow(() -> new RuntimeException("Môn thể thao không tồn tại")));
        }

        sanPham.setTenSanPham(request.getTenSanPham().trim());
        if (request.getDoiTuong() != null) sanPham.setDoiTuong(request.getDoiTuong());
        sanPham.setTinhNang(request.getTinhNang());
        sanPham.setMoTaChiTiet(request.getMoTaChiTiet());
        sanPham.setVideoUrl(request.getVideoUrl());
        if (request.getTrangThai() != null) sanPham.setTrangThai(request.getTrangThai());
        sanPham.setNgaySua(LocalDateTime.now());

        sanPham = sanPhamRepository.save(sanPham);

        if (request.getDanhSachAnh() != null && !request.getDanhSachAnh().isEmpty()) {
            hinhAnhSanPhamRepository.deleteBySanPhamId(sanPham.getId());
            boolean hasMain = false;
            for (int i = 0; i < request.getDanhSachAnh().size(); i++) {
                String imgUrl = request.getDanhSachAnh().get(i).trim();
                if (imgUrl.isEmpty()) continue;
                boolean isMain = !hasMain && (imgUrl.equals(request.getAnhChinhUrl()) || i == 0);
                if (isMain) hasMain = true;

                HinhAnhSanPham hasp = HinhAnhSanPham.builder()
                        .sanPham(sanPham)
                        .urlAnh(imgUrl)
                        .isAnhChinh(isMain)
                        .build();
                hinhAnhSanPhamRepository.save(hasp);
            }
        }

        if (request.getBienTheList() != null) {
            for (SanPhamChiTietRequestDTO vReq : request.getBienTheList()) {
                if (vReq.getId() != null) {
                    SanPhamChiTiet spct = sanPhamChiTietRepository.findById(vReq.getId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể: " + vReq.getId()));
                    spct.setGiaBan(vReq.getGiaBan());
                    spct.setSoLuongTon(vReq.getSoLuongTon());
                    if (vReq.getTrangThai() != null) spct.setTrangThai(vReq.getTrangThai());
                    if (vReq.getMaVach() != null && !vReq.getMaVach().trim().isEmpty()) {
                        spct.setMaVach(vReq.getMaVach().trim());
                    }
                    spct.setNgaySua(LocalDateTime.now());
                    sanPhamChiTietRepository.save(spct);
                } else {
                    MauSac mauSac = mauSacRepository.findById(vReq.getIdMauSac())
                            .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại: " + vReq.getIdMauSac()));
                    KichCo kichCo = kichCoRepository.findById(vReq.getIdKichCo())
                            .orElseThrow(() -> new RuntimeException("Kích cỡ không tồn tại: " + vReq.getIdKichCo()));

                    Optional<SanPhamChiTiet> existing = sanPhamChiTietRepository
                            .findBySanPhamIdAndMauSacIdAndKichCoId(sanPham.getId(), mauSac.getId(), kichCo.getId());

                    if (existing.isPresent()) {
                        SanPhamChiTiet spct = existing.get();
                        spct.setGiaBan(vReq.getGiaBan());
                        spct.setSoLuongTon(vReq.getSoLuongTon());
                        if (vReq.getTrangThai() != null) spct.setTrangThai(vReq.getTrangThai());
                        spct.setNgaySua(LocalDateTime.now());
                        sanPhamChiTietRepository.save(spct);
                    } else {
                        String maSpct = vReq.getMaSpct();
                        if (maSpct == null || maSpct.trim().isEmpty()) {
                            maSpct = sanPham.getMaSanPham() + "-" + mauSac.getMaMau() + "-" + kichCo.getMaKichCo();
                        }
                        String maVach = vReq.getMaVach();
                        if (maVach == null || maVach.trim().isEmpty()) {
                            maVach = "893" + String.format("%09d", System.currentTimeMillis() % 1000000000L);
                        }
                        SanPhamChiTiet spct = SanPhamChiTiet.builder()
                                .sanPham(sanPham)
                                .mauSac(mauSac)
                                .kichCo(kichCo)
                                .maSpct(maSpct.trim())
                                .maVach(maVach.trim())
                                .giaBan(vReq.getGiaBan())
                                .soLuongTon(vReq.getSoLuongTon())
                                .trangThai(vReq.getTrangThai() != null ? vReq.getTrangThai() : 1)
                                .ngayTao(LocalDateTime.now())
                                .ngaySua(LocalDateTime.now())
                                .build();
                        sanPhamChiTietRepository.save(spct);
                    }
                }
            }
        }

        return mapToResponseDTO(sanPham);
    }

    @Override
    @Transactional
    public void toggleStatusSanPham(Long id) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
        int newStatus = (sp.getTrangThai() == null || sp.getTrangThai() == 1) ? 0 : 1;
        sp.setTrangThai(newStatus);
        sp.setNgaySua(LocalDateTime.now());
        sanPhamRepository.save(sp);

        if (newStatus == 0) {
            List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(id);
            for (SanPhamChiTiet v : variants) {
                v.setTrangThai(0);
                v.setNgaySua(LocalDateTime.now());
                sanPhamChiTietRepository.save(v);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamChiTietResponseDTO> getBienTheBySanPham(Long idSanPham) {
        return sanPhamChiTietRepository.findBySanPhamId(idSanPham).stream()
                .map(this::mapVariantToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamChiTietResponseDTO getBienTheById(Long idSpct) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(idSpct)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idSpct));
        return mapVariantToDTO(spct);
    }

    @Override
    @Transactional
    public SanPhamChiTietResponseDTO updateBienThe(Long idSpct, QuickUpdateVariantDTO request) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(idSpct)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idSpct));

        if (request.getGiaBan() != null) spct.setGiaBan(request.getGiaBan());
        if (request.getSoLuongTon() != null) spct.setSoLuongTon(request.getSoLuongTon());
        if (request.getTrangThai() != null) spct.setTrangThai(request.getTrangThai());
        if (request.getMaVach() != null && !request.getMaVach().trim().isEmpty()) {
            spct.setMaVach(request.getMaVach().trim());
        }
        spct.setNgaySua(LocalDateTime.now());
        spct = sanPhamChiTietRepository.save(spct);

        return mapVariantToDTO(spct);
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamChiTietResponseDTO getBienTheByBarcode(String barcode) {
        String clean = barcode.trim();
        SanPhamChiTiet spct = sanPhamChiTietRepository.findByMaVach(clean)
                .or(() -> sanPhamChiTietRepository.findByMaSpct(clean))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã barcode / SKU: " + clean));
        return mapVariantToDTO(spct);
    }

    @Override
    @Transactional
    public void toggleStatusBienThe(Long idSpct) {
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(idSpct)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idSpct));
        spct.setTrangThai((spct.getTrangThai() == null || spct.getTrangThai() == 1) ? 0 : 1);
        spct.setNgaySua(LocalDateTime.now());
        sanPhamChiTietRepository.save(spct);
    }

    private SanPhamResponseDTO mapToResponseDTO(SanPham sp) {
        List<HinhAnhSanPham> images = hinhAnhSanPhamRepository.findBySanPhamId(sp.getId());
        List<SanPhamChiTiet> variants = sanPhamChiTietRepository.findBySanPhamId(sp.getId());

        String mainImg = images.stream()
                .filter(HinhAnhSanPham::getIsAnhChinh)
                .map(HinhAnhSanPham::getUrlAnh)
                .findFirst()
                .orElse(!images.isEmpty() ? images.get(0).getUrlAnh() : null);

        int totalStock = variants.stream().mapToInt(SanPhamChiTiet::getSoLuongTon).sum();

        BigDecimal minPrice = variants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = variants.stream()
                .map(SanPhamChiTiet::getGiaBan)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return SanPhamResponseDTO.builder()
                .id(sp.getId())
                .maSanPham(sp.getMaSanPham())
                .tenSanPham(sp.getTenSanPham())
                .idDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getId() : null)
                .tenDanhMuc(sp.getDanhMuc() != null ? sp.getDanhMuc().getTenDanhMuc() : null)
                .idThuongHieu(sp.getThuongHieu() != null ? sp.getThuongHieu().getId() : null)
                .tenThuongHieu(sp.getThuongHieu() != null ? sp.getThuongHieu().getTenThuongHieu() : null)
                .idChatLieu(sp.getChatLieu() != null ? sp.getChatLieu().getId() : null)
                .tenChatLieu(sp.getChatLieu() != null ? sp.getChatLieu().getTenChatLieu() : null)
                .idXuatXu(sp.getXuatXu() != null ? sp.getXuatXu().getId() : null)
                .tenXuatXu(sp.getXuatXu() != null ? sp.getXuatXu().getTenXuatXu() : null)
                .idKieuDang(sp.getKieuDang() != null ? sp.getKieuDang().getId() : null)
                .tenKieuDang(sp.getKieuDang() != null ? sp.getKieuDang().getTenKieuDang() : null)
                .idMonTheThao(sp.getMonTheThao() != null ? sp.getMonTheThao().getId() : null)
                .tenMonTheThao(sp.getMonTheThao() != null ? sp.getMonTheThao().getTenMonTheThao() : null)
                .doiTuong(sp.getDoiTuong())
                .tinhNang(sp.getTinhNang())
                .moTaChiTiet(sp.getMoTaChiTiet())
                .videoUrl(sp.getVideoUrl())
                .trangThai(sp.getTrangThai())
                .anhChinh(mainImg)
                .danhSachAnh(images.stream().map(img -> HinhAnhSanPhamDTO.builder()
                        .id(img.getId())
                        .urlAnh(img.getUrlAnh())
                        .isAnhChinh(img.getIsAnhChinh())
                        .build()).collect(Collectors.toList()))
                .danhSachBienThe(variants.stream().map(this::mapVariantToDTO).collect(Collectors.toList()))
                .tongSoLuongTon(totalStock)
                .giaThapNhat(minPrice)
                .giaCaoNhat(maxPrice)
                .ngayTao(sp.getNgayTao())
                .ngaySua(sp.getNgaySua())
                .build();
    }

    private SanPhamChiTietResponseDTO mapVariantToDTO(SanPhamChiTiet v) {
        return SanPhamChiTietResponseDTO.builder()
                .id(v.getId())
                .idSanPham(v.getSanPham() != null ? v.getSanPham().getId() : null)
                .tenSanPham(v.getSanPham() != null ? v.getSanPham().getTenSanPham() : null)
                .maSpct(v.getMaSpct())
                .maVach(v.getMaVach())
                .idMauSac(v.getMauSac() != null ? v.getMauSac().getId() : null)
                .tenMauSac(v.getMauSac() != null ? v.getMauSac().getTenMau() : null)
                .idKichCo(v.getKichCo() != null ? v.getKichCo().getId() : null)
                .tenKichCo(v.getKichCo() != null ? v.getKichCo().getTenKichCo() : null)
                .giaBan(v.getGiaBan())
                .soLuongTon(v.getSoLuongTon())
                .trangThai(v.getTrangThai())
                .ngayTao(v.getNgayTao())
                .ngaySua(v.getNgaySua())
                .build();
    }
}
