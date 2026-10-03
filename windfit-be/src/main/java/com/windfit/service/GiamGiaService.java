package com.windfit.service;

import com.windfit.dto.discount.*;
import com.windfit.dto.product.SanPhamChiTietResponseDTO;
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
    Page<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(
            String keyword,
            String loaiGiamGia,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size
    );

    PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id);

    PhieuGiamGiaResponseDTO createPhieuGiamGia(PhieuGiamGiaRequestDTO request);

    PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request);

    void toggleStatusPhieuGiamGia(Long id);

    ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request);

    List<PhieuGiamGiaResponseDTO> getAvailableVouchers();

    Page<DotGiamGiaResponseDTO> getDotGiamGiaList(
            String keyword,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size
    );

    DotGiamGiaResponseDTO getDotGiamGiaById(Long id);

    DotGiamGiaResponseDTO createDotGiamGia(DotGiamGiaRequestDTO request);

    DotGiamGiaResponseDTO updateDotGiamGia(Long id, DotGiamGiaRequestDTO request);

    void toggleStatusDotGiamGia(Long id);

    BigDecimal getActiveDiscountPriceForSpct(Long spctId, BigDecimal originalPrice);

    Map<Long, BigDecimal> getActiveDiscountPricesForSpctList(List<Long> spctIds);

    Page<SanPhamChiTietResponseDTO> searchSanPhamChiTiet(String keyword, Long idSanPham, int page, int size);

    Page<KhachHangSimpleDTO> searchKhachHang(String keyword, int page, int size);

    List<Long> getAssignedCustomerIds(Long idVoucher);

    void assignCustomersToVoucher(Long idVoucher, List<Long> customerIds);
}

@Service
@RequiredArgsConstructor
class GiamGiaServiceImpl implements GiamGiaService {

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final KhachHangPhieuGiamGiaRepository khachHangPhieuGiamGiaRepository;
    private final KhachHangRepository khachHangRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<PhieuGiamGiaResponseDTO> getPhieuGiamGiaList(
            String keyword,
            String loaiGiamGia,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanType = (loaiGiamGia != null && !loaiGiamGia.trim().isEmpty()) ? loaiGiamGia.trim() : null;

        Page<PhieuGiamGia> resultPage = phieuGiamGiaRepository.searchPhieuGiamGia(cleanKw, cleanType, trangThai, tuNgay, denNgay, pageable);
        return resultPage.map(this::mapVoucherToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PhieuGiamGiaResponseDTO getPhieuGiamGiaById(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));
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
                throw new IllegalArgumentException("Mã phiếu giảm giá '" + code + "' đã tồn tại trong hệ thống!");
            }
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        if ("PHAN_TRAM".equalsIgnoreCase(request.getLoaiGiamGia())) {
            if (request.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Phần trăm giảm giá không được vượt quá 100%!");
            }
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

        if (request.getCustomerIds() != null && !request.getCustomerIds().isEmpty()) {
            assignCustomersToVoucher(p.getId(), request.getCustomerIds());
        }

        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public PhieuGiamGiaResponseDTO updatePhieuGiamGia(Long id, PhieuGiamGiaRequestDTO request) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));

        if (request.getMaPhieuGiamGia() != null && !request.getMaPhieuGiamGia().trim().equalsIgnoreCase(p.getMaPhieuGiamGia())) {
            String newCode = request.getMaPhieuGiamGia().trim().toUpperCase();
            if (phieuGiamGiaRepository.existsByMaPhieuGiamGia(newCode)) {
                throw new IllegalArgumentException("Mã phiếu giảm giá '" + newCode + "' đã tồn tại!");
            }
            p.setMaPhieuGiamGia(newCode);
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        if ("PHAN_TRAM".equalsIgnoreCase(request.getLoaiGiamGia())) {
            if (request.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Phần trăm giảm giá không được vượt quá 100%!");
            }
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

        if (request.getCustomerIds() != null) {
            assignCustomersToVoucher(p.getId(), request.getCustomerIds());
        }

        return mapVoucherToDTO(p);
    }

    @Override
    @Transactional
    public void toggleStatusPhieuGiamGia(Long id) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + id));
        p.setTrangThai((p.getTrangThai() == null || p.getTrangThai() == 1) ? 0 : 1);
        phieuGiamGiaRepository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateVoucherResponse validateVoucher(ValidateVoucherRequest request) {
        if (request.getMaVoucher() == null || request.getMaVoucher().trim().isEmpty()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher không được để trống!")
                    .build();
        }

        Optional<PhieuGiamGia> pOpt = phieuGiamGiaRepository.findByMaPhieuGiamGia(request.getMaVoucher().trim().toUpperCase());
        if (pOpt.isEmpty()) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Mã voucher '" + request.getMaVoucher() + "' không tồn tại!")
                    .build();
        }

        PhieuGiamGia pgg = pOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (pgg.getTrangThai() == null || pgg.getTrangThai() == 0) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher này đang tạm khóa hoặc đã ngưng áp dụng!")
                    .build();
        }
        if (now.isBefore(pgg.getNgayBatDau())) {
            return ValidateVoucherResponse.builder()
                    .valid(false)
                    .message("Voucher chưa đến ngày bắt đầu sử dụng!")
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
    public Page<DotGiamGiaResponseDTO> getDotGiamGiaList(
            String keyword,
            Integer trangThai,
            LocalDateTime tuNgay,
            LocalDateTime denNgay,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<DotGiamGia> resultPage = dotGiamGiaRepository.searchDotGiamGia(cleanKw, trangThai, tuNgay, denNgay, pageable);
        return resultPage.map(this::mapCampaignToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DotGiamGiaResponseDTO getDotGiamGiaById(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));
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
                throw new IllegalArgumentException("Mã đợt giảm giá '" + code + "' đã tồn tại trong hệ thống!");
            }
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
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
                        .orElseThrow(() -> new IllegalArgumentException("Biến thể áo khoác không tồn tại ID: " + item.getIdSanPhamChiTiet()));

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
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));

        if (request.getMaDotGiamGia() != null && !request.getMaDotGiamGia().trim().equalsIgnoreCase(d.getMaDotGiamGia())) {
            String newCode = request.getMaDotGiamGia().trim().toUpperCase();
            if (dotGiamGiaRepository.existsByMaDotGiamGia(newCode)) {
                throw new IllegalArgumentException("Mã đợt giảm giá '" + newCode + "' đã tồn tại!");
            }
            d.setMaDotGiamGia(newCode);
        }

        if (request.getNgayKetThuc().isBefore(request.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải diễn ra sau ngày bắt đầu!");
        }

        d.setTenDotGiamGia(request.getTenDotGiamGia().trim());
        d.setMoTa(request.getMoTa());
        d.setDieuKienApDung(request.getDieuKienApDung());
        d.setNgayBatDau(request.getNgayBatDau());
        d.setNgayKetThuc(request.getNgayKetThuc());
        if (request.getTrangThai() != null) d.setTrangThai(request.getTrangThai());

        d = dotGiamGiaRepository.save(d);

        // Soft-update child details: No hard delete!
        List<DotGiamGiaChiTiet> existingDetails = dotGiamGiaChiTietRepository.findByDotGiamGiaId(d.getId());
        for (DotGiamGiaChiTiet detail : existingDetails) {
            detail.setTrangThai(0);
            dotGiamGiaChiTietRepository.save(detail);
        }

        if (request.getDanhSachChiTiet() != null) {
            for (DotGiamGiaItemRequestDTO item : request.getDanhSachChiTiet()) {
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(item.getIdSanPhamChiTiet())
                        .orElseThrow(() -> new IllegalArgumentException("Biến thể áo khoác không tồn tại ID: " + item.getIdSanPhamChiTiet()));

                BigDecimal phanTram = item.getPhanTramGiam();
                BigDecimal discountMultiplier = BigDecimal.valueOf(100).subtract(phanTram).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal giaSauGiam = spct.getGiaBan().multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP);

                // Check if existing record exists for this spct in this campaign
                Optional<DotGiamGiaChiTiet> matched = existingDetails.stream()
                        .filter(ed -> ed.getSanPhamChiTiet().getId().equals(spct.getId()))
                        .findFirst();

                if (matched.isPresent()) {
                    DotGiamGiaChiTiet detail = matched.get();
                    detail.setSoLuong(item.getSoLuong());
                    detail.setPhanTramGiam(phanTram);
                    detail.setGiaSauGiam(giaSauGiam);
                    detail.setTrangThai(1);
                    dotGiamGiaChiTietRepository.save(detail);
                } else {
                    DotGiamGiaChiTiet newDetail = DotGiamGiaChiTiet.builder()
                            .dotGiamGia(d)
                            .sanPhamChiTiet(spct)
                            .soLuong(item.getSoLuong())
                            .phanTramGiam(phanTram)
                            .giaSauGiam(giaSauGiam)
                            .trangThai(1)
                            .build();
                    dotGiamGiaChiTietRepository.save(newDetail);
                }
            }
        }

        return mapCampaignToDTO(d);
    }

    @Override
    @Transactional
    public void toggleStatusDotGiamGia(Long id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đợt giảm giá ID: " + id));
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

    @Override
    @Transactional(readOnly = true)
    public Page<SanPhamChiTietResponseDTO> searchSanPhamChiTiet(String keyword, Long idSanPham, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<SanPhamChiTiet> pageResult = sanPhamChiTietRepository.searchSanPhamChiTiet(cleanKw, idSanPham, 1, pageable);
        return pageResult.map(s -> SanPhamChiTietResponseDTO.builder()
                .id(s.getId())
                .idSanPham(s.getSanPham() != null ? s.getSanPham().getId() : null)
                .tenSanPham(s.getSanPham() != null ? s.getSanPham().getTenSanPham() : "")
                .maSpct(s.getMaSpct())
                .maVach(s.getMaVach())
                .idMauSac(s.getMauSac() != null ? s.getMauSac().getId() : null)
                .tenMauSac(s.getMauSac() != null ? s.getMauSac().getTenMau() : "")
                .idKichCo(s.getKichCo() != null ? s.getKichCo().getId() : null)
                .tenKichCo(s.getKichCo() != null ? s.getKichCo().getTenKichCo() : "")
                .giaBan(s.getGiaBan())
                .soLuongTon(s.getSoLuongTon())
                .trangThai(s.getTrangThai())
                .ngayTao(s.getNgayTao())
                .ngaySua(s.getNgaySua())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KhachHangSimpleDTO> searchKhachHang(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String cleanKw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<KhachHang> pageResult = khachHangRepository.searchKhachHang(cleanKw, 1, pageable);
        return pageResult.map(k -> KhachHangSimpleDTO.builder()
                .id(k.getId())
                .maKhachHang(k.getMaKhachHang())
                .hoTen(k.getHoTen())
                .soDienThoai(k.getSoDienThoai())
                .email(k.getEmail())
                .trangThai(k.getTrangThai())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAssignedCustomerIds(Long idVoucher) {
        return khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaId(idVoucher).stream()
                .filter(item -> item.getTrangThai() != null && item.getTrangThai() == 1)
                .map(item -> item.getKhachHang().getId())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignCustomersToVoucher(Long idVoucher, List<Long> customerIds) {
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idVoucher)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu giảm giá ID: " + idVoucher));

        List<KhachHangPhieuGiamGia> existing = khachHangPhieuGiamGiaRepository.findByPhieuGiamGiaId(idVoucher);
        // Soft deactivate existing
        for (KhachHangPhieuGiamGia item : existing) {
            item.setTrangThai(0);
            khachHangPhieuGiamGiaRepository.save(item);
        }

        if (customerIds != null) {
            for (Long cId : customerIds) {
                KhachHang kh = khachHangRepository.findById(cId).orElse(null);
                if (kh != null) {
                    Optional<KhachHangPhieuGiamGia> matched = existing.stream()
                            .filter(e -> e.getKhachHang().getId().equals(cId))
                            .findFirst();

                    if (matched.isPresent()) {
                        KhachHangPhieuGiamGia item = matched.get();
                        item.setTrangThai(1);
                        khachHangPhieuGiamGiaRepository.save(item);
                    } else {
                        KhachHangPhieuGiamGia newItem = KhachHangPhieuGiamGia.builder()
                                .phieuGiamGia(pgg)
                                .khachHang(kh)
                                .trangThai(1)
                                .build();
                        khachHangPhieuGiamGiaRepository.save(newItem);
                    }
                }
            }
        }
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
        List<DotGiamGiaChiTietResponseDTO> dtos = details.stream()
                .filter(c -> c.getTrangThai() != null && c.getTrangThai() == 1)
                .map(c -> {
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
                .soLuongSanPham(dtos.size())
                .danhSachChiTiet(dtos)
                .build();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return String.format("%,.0f đ", amount);
    }
}
