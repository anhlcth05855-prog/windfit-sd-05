package com.windfit.service;

import com.windfit.dto.discount.*;
import com.windfit.entity.*;
import com.windfit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface GiamGiaService {
    Page<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(String keyword, String loaiGiamGia, Integer trangThai, int page, int size);
    PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id);
    PhieuGiamGiaResponseDTO createPhieuGiamGia(PhieuGiamGiaRequestDTO request);
    PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request);
    void toggleStatusPhieuGiamGia(Long id);
    ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request);
    List<PhieuGiamGiaResponseDTO> getAvailableVouchers();
    Page<DotGiamGiaResponseDTO> getDotGiamGiaList(String keyword, Integer trangThai, int page, int size);
    DotGiamGiaResponseDTO getDotGiamGiaById(Long id);
    DotGiamGiaResponseDTO createDotGiamGia(DotGiamGiaRequestDTO request);
    DotGiamGiaResponseDTO updateDotGiamGia(Long id, DotGiamGiaRequestDTO request);
    void toggleStatusDotGiamGia(Long id);
    BigDecimal getActiveDiscountPriceForSpct(Long spctId, BigDecimal originalPrice);
    Map<Long, BigDecimal> getActiveDiscountPricesForSpctList(List<Long> spctIds);
}

@Service
@RequiredArgsConstructor
class GiamGiaServiceImpl implements GiamGiaService {

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(
            String keyword,
            String loaiGiamGia,
            Integer trangThai,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanType = (loaiGiamGia != null && !loaiGiamGia.trim().isEmpty()) ? loaiGiamGia.trim() : null;

        Page<PhieuGiamGia> resultPage = phieuGiamGiaRepository.searchPhieuGiamGia(cleanKw, cleanType, trangThai, pageable);
        return resultPage.map(this::mapVoucherToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá ID: " + id));
        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public PhieuGiamGiaResponseDTO createPhieuGiamGia(PhieuGiamGiaRequestDTO request) {
        String code = request.getMaPhieuGiamGia();
        if (code == null || code.trim().isEmpty()) {
            code = "WF" + (int)(Math.random() * 90000 + 10000);
        } else {
            code = code.trim().toUpperCase();
            if (phieuGiamGiaRepository.existsByMaPhieuGiamGia(code)) {
                throw new RuntimeException("Mã phiếu giảm giá '" + code + "' đã tồn tại!");
            }
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        PhieuGiamGia p = PhieuGiamGia.builder()
                .maPhieuGiamGia(code)
                .tenPhieuGiamGia(request.getTenPhieuGiamGia().trim())
                .loaiGiamGia(request.getLoaiGiamGia().trim().toUpperCase())
                .giaTriGiam(request.getGiaTriGiam())
                .giamToiDa(request.getGiamToiDa())
                .donToiThieu(request.getDonToiThieu())
                .soLuong(request.getSoLuong())
                .soLuongDaDung(0)
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .ngayTao(LocalDateTime.now())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .build();

        p = phieuGiamGiaRepository.save(p);
        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá ID: " + id));

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        p.setTenPhieuGiamGia(request.getTenPhieuGiamGia().trim());
        p.setLoaiGiamGia(request.getLoaiGiamGia().trim().toUpperCase());
        p.setGiaTriGiam(request.getGiaTriGiam());
        p.setGiamToiDa(request.getGiamToiDa());
        p.setDonToiThieu(request.getDonToiThieu());
        p.setSoLuong(request.getSoLuong());
        p.setNgayBatDau(request.getNgayBatDau());
        p.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) p.setTrangThai(request.getTrangThai());

        p = phieuGiamGiaRepository.save(p);
        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public void toggleStatusPhieuGiamGia(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá ID: " + id));
        p.setTrangThai((p.getTrangThai() == null || p.getTrangThai() == 1) ? 0 : 1);
        phieuGiamGiaRepository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request) {
        String code = request.getMaVoucher().trim();
        Optional<PhieuGiamGia> opt = phieuGiamGiaRepository.findByMaPhieuGiamGia(code);

        if (opt.isEmpty()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher '" + code + "' không tồn tại!")
                    .build();
        }

        PhieuGiamGia pgg = opt.get();
        LocalDateTime now = LocalDateTime.now();

        if (pgg.getTrangThai() == 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher '" + code + "' đã bị tạm khóa!")
                    .build();
        }
        if (now.isBefore(pgg.getNgayBatDau())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher chưa tới thời gian áp dụng!")
                    .build();
        }
        if (now.isAfter(pgg.getNgayKetThuc())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher đã hết hạn sử dụng!")
                    .build();
        }
        if (pgg.getSoLuongDaDung() >= pgg.getSoLuong()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher đã hết lượt sử dụng!")
                    .build();
        }
        if (request.getTongTienDonHang().compareTo(pgg.getDonToiThieu()) < 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Đơn hàng phải tối thiểu " + formatMoney(pgg.getDonToiThieu()) + " để áp dụng voucher này!")
                    .build();
        }

        if (request.getIdKhachHang() != null) {
            Optional<KhachHangPhieuGiamGia> khPgg = khachHangPhieuGiamGiaRepository
                    .findByKhachHangIdAndPhieuGiamGiaId(request.getIdKhachHang(), pgg.getId());
            if (khPgg.isPresent() && khPgg.get().getTrangThai() == 0) {
                return ValidateVoucherResponse.builder()
                        .valid(false)
                        .message("Bạn đã sử dụng voucher này rồi!")
                        .build();
            }
        }

        BigDecimal discountAmount;
        if ("PHAN_TRAM".equalsIgnoreCase(pgg.getLoaiGiamGia())) {
            discountAmount = request.getTongTienDonHang()
                    .multiply(pgg.getGiaTriGiam())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (pgg.getGiamToiDa() != null && discountAmount.compareTo(pgg.getGiamToiDa()) > 0) {
                discountAmount = pgg.getGiamToiDa();
            }
        } else {
            discountAmount = pgg.getGiaTriGiam();
        }

        if (discountAmount.compareTo(request.getTongTienDonHang()) > 0) {
            discountAmount = request.getTongTienDonHang();
        }

        BigDecimal finalTotal = request.getTongTienDonHang().subtract(discountAmount);

        return ValidateVoucherResponse.builder()
                .valid(true)
                .idVoucher(pgg.getId())
                .maVoucher(pgg.getMaPhieuGiamGia())
                .tenVoucher(pgg.getTenPhieuGiamGia())
                .loaiGiamGia(pgg.getLoaiGiamGia())
                .giaTriGiam(pgg.getGiaTriGiam())
                .soTienGiam(discountAmount)
                .thanhTienSauGiam(finalTotal)
                .message("Áp dụng mã voucher thành công!")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuGiamGiaResponseDTO> getAvailableVouchers() {
        return phieuGiamGiaRepository.findAvailableVouchers(LocalDateTime.now()).stream()
                .map(this::mapVoucherToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DotGiamGiaResponseDTO> getDotGiamGiaList(String keyword, Integer trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<DotGiamGia> resultPage = dotGiamGiaRepository.searchDotGiamGia(cleanKw, trangThai, pageable);
        return resultPage.map(this::mapCampaignToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DotGiamGiaResponseDTO getDotGiamGiaById(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));
        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public DotGiamGiaResponseDTO createDotGiamGia(DotGiamGiaRequestDTO request) {
        String code = request.getMaDotGiamGia();
        if (code == null || code.trim().isEmpty()) {
            code = "DGG" + (int)(Math.random() * 90000 + 10000);
        } else {
            code = code.trim().toUpperCase();
            if (dotGiamGiaRepository.existsByMaDotGiamGia(code)) {
                throw new RuntimeException("Mã đợt giảm giá '" + code + "' đã tồn tại!");
            }
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        DotGiamGia d = DotGiamGia.builder()
                .maDotGiamGia(code)
                .tenDotGiamGia(request.getTenDotGiamGia().trim())
                .moTa(request.getMoTa())
                .dieuKienApDung(request.getDieuKienApDung())
                .ngayBatDau(request.getNgayBatDau())
                .ngayKetThuc(request.getNgayKetThuc())
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .build();

        d = dotGiamGiaRepository.save(d);

        if (request.getDanhSachChiTiet() != null) {
            for (DotGiamGiaItemRequestDTO item : request.getDanhSachChiTiet()) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSanPhamChiTiet())
                        .orElseThrow(() -> new RuntimeException("Biến thể áo khoác không tồn tại ID: " + item.getIdSanPhamChiTiet()));

                BigDecimal phanTram = item.getPhanTramGiam();
                BigDecimal discountMultiplier = BigDecimal.valueOf(100).subtract(phanTram).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal giaSauGiam = spct.getGiaBan().multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP);

                DotGiamGiaChiTiet detail = DotGiamGiaChiTiet.builder()
                        .dotGiamGia(d)
                        .sanPhamChiTiet(spct)
                        .soLuong(item.getSoLuong())
                        .phanTramGiam(phanTram)
                        .giaSauGiam(giaSauGiam)
                        .trangThai(1)
                        .build();

                dotGiamGiaChiTietRepository.save(detail);
            }
        }

        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public DotGiamGiaResponseDTO updateDotGiamGia(Long id, DotGiamGiaRequestDTO request) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new RuntimeException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        d.setTenDotGiamGia(request.getTenDotGiamGia().trim());
        d.setMoTa(request.getMoTa());
        d.setDieuKienApDung(request.getDieuKienApDung());
        d.setNgayBatDau(request.getNgayBatDau());
        d.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) d.setTrangThai(request.getTrangThai());

        d = dotGiamGiaRepository.save(d);

        if (request.getDanhSachChiTiet() != null) {
            dotGiamGiaChiTietRepository.deleteByDotGiamGiaId(d.getId());
            for (DotGiamGiaItemRequestDTO item : request.getDanhSachChiTiet()) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSanPhamChiTiet())
                        .orElseThrow(() -> new RuntimeException("Biến thể áo khoác không tồn tại ID: " + item.getIdSanPhamChiTiet()));

                BigDecimal phanTram = item.getPhanTramGiam();
                BigDecimal discountMultiplier = BigDecimal.valueOf(100).subtract(phanTram).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal giaSauGiam = spct.getGiaBan().multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP);

                DotGiamGiaChiTiet detail = DotGiamGiaChiTiet.builder()
                        .dotGiamGia(d)
                        .sanPhamChiTiet(spct)
                        .soLuong(item.getSoLuong())
                        .phanTramGiam(phanTram)
                        .giaSauGiam(giaSauGiam)
                        .trangThai(1)
                        .build();

                dotGiamGiaChiTietRepository.save(detail);
            }
        }

        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public void toggleStatusDotGiamGia(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));
        int newStatus = (d.getTrangThai() == null || d.getTrangThai() == 1) ? 0 : 1;
        d.setTrangThai(newStatus);
        dotGiamGiaRepository.save(d);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getActiveDiscountPriceForSpct(Long spctId, BigDecimal originalPrice) {
        List<DotGiamGiaChiTiet> activeDiscounts = dotGiamGiaChiTietRepository.findActiveDiscountsForSpct(spctId, LocalDateTime.now());
        if (activeDiscounts.isEmpty()) {
            return originalPrice;
        }
        return activeDiscounts.get(0).getGiaSauGiam();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getActiveDiscountPricesForSpctList(List<Long> spctIds) {
        if (spctIds == null || spctIds.isEmpty()) return Collections.emptyMap();
        List<DotGiamGiaChiTiet> list = dotGiamGiaChiTietRepository.findActiveDiscountsForSpctList(spctIds, LocalDateTime.now());
        Map<Long, BigDecimal> result = new HashMap<>();
        for (DotGiamGiaChiTiet c : list) {
            Long sId = c.getSanPhamChiTiet().getId();
            if (!result.containsKey(sId) || c.getGiaSauGiam().compareTo(result.get(sId)) < 0) {
                result.put(sId, c.getGiaSauGiam());
            }
        }
        return result;
    }

    private PhieuGiamGiaResponseDTO mapVoucherToDTO(PhieuGiamGia p) {
        LocalDateTime now = LocalDateTime.now();
        String tinhTrang;

        if (p.getTrangThai() == 0) {
            tinhTrang = "DA_KHOA";
        } else if (p.getSoLuongDaDung() >= p.getSoLuong()) {
            tinhTrang = "HET_LUOT";
        } else if (now.isBefore(p.getNgayBatDau())) {
            tinhTrang = "SAP_DIEN_RA";
        } else if (now.isAfter(p.getNgayKetThuc())) {
            tinhTrang = "DA_KET_THUC";
        } else {
            tinhTrang = "DANG_DIEN_RA";
        }

        return PhieuGiamGiaResponseDTO.builder()
                .id(p.getId())
                .maPhieuGiamGia(p.getMaPhieuGiamGia())
                .tenPhieuGiamGia(p.getTenPhieuGiamGia())
                .loaiGiamGia(p.getLoaiGiamGia())
                .giaTriGiam(p.getGiaTriGiam())
                .giamToiDa(p.getGiamToiDa())
                .donToiThieu(p.getDonToiThieu())
                .soLuong(p.getSoLuong())
                .soLuongDaDung(p.getSoLuongDaDung())
                .ngayBatDau(p.getNgayBatDau())
                .ngayKetThuc(p.getNgayKetThuc())
                .ngayTao(p.getNgayTao())
                .trangThai(p.getTrangThai())
                .tinhTrang(tinhTrang)
                .build();
    }

    private DotGiamGiaResponseDTO mapCampaignToDTO(DotGiamGia d) {
        LocalDateTime now = LocalDateTime.now();
        String tinhTrang;

        if (d.getTrangThai() == 0) {
            tinhTrang = "DA_KHOA";
        } else if (now.isBefore(d.getNgayBatDau())) {
            tinhTrang = "SAP_DIEN_RA";
        } else if (now.isAfter(d.getNgayKetThuc())) {
            tinhTrang = "DA_KET_THUC";
        } else {
            tinhTrang = "DANG_DIEN_RA";
        }

        List<DotGiamGiaChiTiet> details = dotGiamGiaChiTietRepository.findByDotGiamGiaId(d.getId());
        List<DotGiamGiaChiTietResponseDTO> dtos = details.stream().map(c -> {
            SanPhamChiTiet spct = c.getSanPhamChiTiet();
            SanPham sp = spct != null ? spct.getSanPham() : null;
            return DotGiamGiaChiTietResponseDTO.builder()
                    .id(c.getId())
                    .idDotGiamGia(d.getId())
                    .idSanPhamChiTiet(spct != null ? spct.getId() : null)
                    .idSanPham(sp != null ? sp.getId() : null)
                    .tenSanPham(sp != null ? sp.getTenSanPham() : "")
                    .maSpct(spct != null ? spct.getMaSpct() : "")
                    .maVach(spct != null ? spct.getMaVach() : "")
                    .tenMauSac(spct != null && spct.getMauSac() != null ? spct.getMauSac().getTenMau() : "")
                    .tenKichCo(spct != null && spct.getKichCo() != null ? spct.getKichCo().getTenKichCo() : "")
                    .giaGoc(spct != null ? spct.getGiaBan() : BigDecimal.ZERO)
                    .phanTramGiam(c.getPhanTramGiam())
                    .giaSauGiam(c.getGiaSauGiam())
                    .soLuong(c.getSoLuong())
                    .soLuongTonHienTai(spct != null ? spct.getSoLuongTon() : 0)
                    .trangThai(c.getTrangThai())
                    .build();
        }).collect(Collectors.toList());

        return DotGiamGiaResponseDTO.builder()
                .id(d.getId())
                .maDotGiamGia(d.getMaDotGiamGia())
                .tenDotGiamGia(d.getTenDotGiamGia())
                .moTa(d.getMoTa())
                .dieuKienApDung(d.getDieuKienApDung())
                .ngayBatDau(d.getNgayBatDau())
                .ngayKetThuc(d.getNgayKetThuc())
                .trangThai(d.getTrangThai())
                .tinhTrang(tinhTrang)
                .soLuongSanPham(details.size())
                .danhSachChiTiet(dtos)
                .build();
    }

    private String formatMoney(BigDecimal val) {
        if (val == null) return "0 đ";
        return String.format("%,d đ", val.longValue());
    }
}
