package com.windfit.service;

import com.windfit.dto.ApiResponse;
import com.windfit.dto.product.ThuocTinhDTO;
import com.windfit.entity.*;
import com.windfit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

public interface ThuocTinhService {
    ApiResponse<List<ThuocTinhDTO>> getByType(String type);
    ApiResponse<ThuocTinhDTO> create(String type, ThuocTinhDTO dto);
    ApiResponse<ThuocTinhDTO> update(String type, Long id, ThuocTinhDTO dto);
    ApiResponse<Void> toggleStatus(String type, Long id);
    ApiResponse<Map<String, List<ThuocTinhDTO>>> getAllOptions();
}

@Service
@RequiredArgsConstructor
class ThuocTinhServiceImpl implements ThuocTinhService {

    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final XuatXuRepository xuatXuRepository;
    private final KieuDangRepository kieuDangRepository;
    private final MonTheThaoRepository monTheThaoRepository;
    private final MauSacRepository mauSacRepository;
    private final KichCoRepository kichCoRepository;

    @Override
    public ApiResponse<List<ThuocTinhDTO>> getByType(String type) {
        List<ThuocTinhDTO> list = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "danh-muc":
                list = danhMucRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaDanhMuc(), e.getTenDanhMuc(), e.getTrangThai(), "danh-muc"))
                        .collect(Collectors.toList());
                break;
            case "thuong-hieu":
                list = thuongHieuRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaThuongHieu(), e.getTenThuongHieu(), e.getTrangThai(), "thuong-hieu"))
                        .collect(Collectors.toList());
                break;
            case "chat-lieu":
                list = chatLieuRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaChatLieu(), e.getTenChatLieu(), e.getTrangThai(), "chat-lieu"))
                        .collect(Collectors.toList());
                break;
            case "xuat-xu":
                list = xuatXuRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaXuatXu(), e.getTenXuatXu(), e.getTrangThai(), "xuat-xu"))
                        .collect(Collectors.toList());
                break;
            case "kieu-dang":
                list = kieuDangRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaKieuDang(), e.getTenKieuDang(), e.getTrangThai(), "kieu-dang"))
                        .collect(Collectors.toList());
                break;
            case "mon-the-thao":
                list = monTheThaoRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaMonTheThao(), e.getTenMonTheThao(), e.getTrangThai(), "mon-the-thao"))
                        .collect(Collectors.toList());
                break;
            case "mau-sac":
                list = mauSacRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaMau(), e.getTenMau(), e.getTrangThai(), "mau-sac"))
                        .collect(Collectors.toList());
                break;
            case "kich-co":
                list = kichCoRepository.findAll().stream()
                        .map(e -> new ThuocTinhDTO(e.getId(), e.getMaKichCo(), e.getTenKichCo(), e.getTrangThai(), "kich-co"))
                        .collect(Collectors.toList());
                break;
            default:
                return ApiResponse.error("Loại thuộc tính không hợp lệ: " + type);
        }
        return ApiResponse.ok(list);
    }

    @Override
    @Transactional
    public ApiResponse<ThuocTinhDTO> create(String type, ThuocTinhDTO dto) {
        String ma = dto.getMa().trim();
        String ten = dto.getTen().trim();
        Integer trangThai = dto.getTrangThai() != null ? dto.getTrangThai() : 1;

        switch (type.toLowerCase()) {
            case "danh-muc":
                if (danhMucRepository.existsByMaDanhMuc(ma)) return ApiResponse.error("Mã danh mục đã tồn tại!");
                DanhMuc dm = danhMucRepository.save(DanhMuc.builder().maDanhMuc(ma).tenDanhMuc(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo danh mục thành công!", new ThuocTinhDTO(dm.getId(), dm.getMaDanhMuc(), dm.getTenDanhMuc(), dm.getTrangThai(), type));
            case "thuong-hieu":
                if (thuongHieuRepository.existsByMaThuongHieu(ma)) return ApiResponse.error("Mã thương hiệu đã tồn tại!");
                ThuongHieu th = thuongHieuRepository.save(ThuongHieu.builder().maThuongHieu(ma).tenThuongHieu(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo thương hiệu thành công!", new ThuocTinhDTO(th.getId(), th.getMaThuongHieu(), th.getTenThuongHieu(), th.getTrangThai(), type));
            case "chat-lieu":
                if (chatLieuRepository.existsByMaChatLieu(ma)) return ApiResponse.error("Mã chất liệu đã tồn tại!");
                ChatLieu cl = chatLieuRepository.save(ChatLieu.builder().maChatLieu(ma).tenChatLieu(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo chất liệu thành công!", new ThuocTinhDTO(cl.getId(), cl.getMaChatLieu(), cl.getTenChatLieu(), cl.getTrangThai(), type));
            case "xuat-xu":
                if (xuatXuRepository.existsByMaXuatXu(ma)) return ApiResponse.error("Mã xuất xứ đã tồn tại!");
                XuatXu xx = xuatXuRepository.save(XuatXu.builder().maXuatXu(ma).tenXuatXu(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo xuất xứ thành công!", new ThuocTinhDTO(xx.getId(), xx.getMaXuatXu(), xx.getTenXuatXu(), xx.getTrangThai(), type));
            case "kieu-dang":
                if (kieuDangRepository.existsByMaKieuDang(ma)) return ApiResponse.error("Mã kiểu dáng đã tồn tại!");
                KieuDang kd = kieuDangRepository.save(KieuDang.builder().maKieuDang(ma).tenKieuDang(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo kiểu dáng thành công!", new ThuocTinhDTO(kd.getId(), kd.getMaKieuDang(), kd.getTenKieuDang(), kd.getTrangThai(), type));
            case "mon-the-thao":
                if (monTheThaoRepository.existsByMaMonTheThao(ma)) return ApiResponse.error("Mã môn thể thao đã tồn tại!");
                MonTheThao mtt = monTheThaoRepository.save(MonTheThao.builder().maMonTheThao(ma).tenMonTheThao(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo môn thể thao thành công!", new ThuocTinhDTO(mtt.getId(), mtt.getMaMonTheThao(), mtt.getTenMonTheThao(), mtt.getTrangThai(), type));
            case "mau-sac":
                if (mauSacRepository.existsByMaMau(ma)) return ApiResponse.error("Mã màu sắc đã tồn tại!");
                MauSac ms = mauSacRepository.save(MauSac.builder().maMau(ma).tenMau(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo màu sắc thành công!", new ThuocTinhDTO(ms.getId(), ms.getMaMau(), ms.getTenMau(), ms.getTrangThai(), type));
            case "kich-co":
                if (kichCoRepository.existsByMaKichCo(ma)) return ApiResponse.error("Mã kích cỡ đã tồn tại!");
                KichCo kc = kichCoRepository.save(KichCo.builder().maKichCo(ma).tenKichCo(ten).trangThai(trangThai).build());
                return ApiResponse.ok("Tạo kích cỡ thành công!", new ThuocTinhDTO(kc.getId(), kc.getMaKichCo(), kc.getTenKichCo(), kc.getTrangThai(), type));
            default:
                return ApiResponse.error("Loại thuộc tính không hợp lệ: " + type);
        }
    }

    @Override
    @Transactional
    public ApiResponse<ThuocTinhDTO> update(String type, Long id, ThuocTinhDTO dto) {
        String ten = dto.getTen().trim();
        Integer trangThai = dto.getTrangThai();

        switch (type.toLowerCase()) {
            case "danh-muc":
                DanhMuc dm = danhMucRepository.findById(id).orElse(null);
                if (dm == null) return ApiResponse.error("Không tìm thấy danh mục!");
                dm.setTenDanhMuc(ten);
                if (trangThai != null) dm.setTrangThai(trangThai);
                danhMucRepository.save(dm);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(dm.getId(), dm.getMaDanhMuc(), dm.getTenDanhMuc(), dm.getTrangThai(), type));
            case "thuong-hieu":
                ThuongHieu th = thuongHieuRepository.findById(id).orElse(null);
                if (th == null) return ApiResponse.error("Không tìm thấy thương hiệu!");
                th.setTenThuongHieu(ten);
                if (trangThai != null) th.setTrangThai(trangThai);
                thuongHieuRepository.save(th);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(th.getId(), th.getMaThuongHieu(), th.getTenThuongHieu(), th.getTrangThai(), type));
            case "chat-lieu":
                ChatLieu cl = chatLieuRepository.findById(id).orElse(null);
                if (cl == null) return ApiResponse.error("Không tìm thấy chất liệu!");
                cl.setTenChatLieu(ten);
                if (trangThai != null) cl.setTrangThai(trangThai);
                chatLieuRepository.save(cl);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(cl.getId(), cl.getMaChatLieu(), cl.getTenChatLieu(), cl.getTrangThai(), type));
            case "xuat-xu":
                XuatXu xx = xuatXuRepository.findById(id).orElse(null);
                if (xx == null) return ApiResponse.error("Không tìm thấy xuất xứ!");
                xx.setTenXuatXu(ten);
                if (trangThai != null) xx.setTrangThai(trangThai);
                xuatXuRepository.save(xx);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(xx.getId(), xx.getMaXuatXu(), xx.getTenXuatXu(), xx.getTrangThai(), type));
            case "kieu-dang":
                KieuDang kd = kieuDangRepository.findById(id).orElse(null);
                if (kd == null) return ApiResponse.error("Không tìm thấy kiểu dáng!");
                kd.setTenKieuDang(ten);
                if (trangThai != null) kd.setTrangThai(trangThai);
                kieuDangRepository.save(kd);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(kd.getId(), kd.getMaKieuDang(), kd.getTenKieuDang(), kd.getTrangThai(), type));
            case "mon-the-thao":
                MonTheThao mtt = monTheThaoRepository.findById(id).orElse(null);
                if (mtt == null) return ApiResponse.error("Không tìm thấy môn thể thao!");
                mtt.setTenMonTheThao(ten);
                if (trangThai != null) mtt.setTrangThai(trangThai);
                monTheThaoRepository.save(mtt);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(mtt.getId(), mtt.getMaMonTheThao(), mtt.getTenMonTheThao(), mtt.getTrangThai(), type));
            case "mau-sac":
                MauSac ms = mauSacRepository.findById(id).orElse(null);
                if (ms == null) return ApiResponse.error("Không tìm thấy màu sắc!");
                ms.setTenMau(ten);
                if (trangThai != null) ms.setTrangThai(trangThai);
                mauSacRepository.save(ms);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(ms.getId(), ms.getMaMau(), ms.getTenMau(), ms.getTrangThai(), type));
            case "kich-co":
                KichCo kc = kichCoRepository.findById(id).orElse(null);
                if (kc == null) return ApiResponse.error("Không tìm thấy kích cỡ!");
                kc.setTenKichCo(ten);
                if (trangThai != null) kc.setTrangThai(trangThai);
                kichCoRepository.save(kc);
                return ApiResponse.ok("Cập nhật thành công!", new ThuocTinhDTO(kc.getId(), kc.getMaKichCo(), kc.getTenKichCo(), kc.getTrangThai(), type));
            default:
                return ApiResponse.error("Loại thuộc tính không hợp lệ: " + type);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> toggleStatus(String type, Long id) {
        switch (type.toLowerCase()) {
            case "danh-muc":
                DanhMuc dm = danhMucRepository.findById(id).orElse(null);
                if (dm == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                dm.setTrangThai(dm.getTrangThai() == 1 ? 0 : 1);
                danhMucRepository.save(dm);
                break;
            case "thuong-hieu":
                ThuongHieu th = thuongHieuRepository.findById(id).orElse(null);
                if (th == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                th.setTrangThai(th.getTrangThai() == 1 ? 0 : 1);
                thuongHieuRepository.save(th);
                break;
            case "chat-lieu":
                ChatLieu cl = chatLieuRepository.findById(id).orElse(null);
                if (cl == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                cl.setTrangThai(cl.getTrangThai() == 1 ? 0 : 1);
                chatLieuRepository.save(cl);
                break;
            case "xuat-xu":
                XuatXu xx = xuatXuRepository.findById(id).orElse(null);
                if (xx == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                xx.setTrangThai(xx.getTrangThai() == 1 ? 0 : 1);
                xuatXuRepository.save(xx);
                break;
            case "kieu-dang":
                KieuDang kd = kieuDangRepository.findById(id).orElse(null);
                if (kd == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                kd.setTrangThai(kd.getTrangThai() == 1 ? 0 : 1);
                kieuDangRepository.save(kd);
                break;
            case "mon-the-thao":
                MonTheThao mtt = monTheThaoRepository.findById(id).orElse(null);
                if (mtt == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                mtt.setTrangThai(mtt.getTrangThai() == 1 ? 0 : 1);
                monTheThaoRepository.save(mtt);
                break;
            case "mau-sac":
                MauSac ms = mauSacRepository.findById(id).orElse(null);
                if (ms == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                ms.setTrangThai(ms.getTrangThai() == 1 ? 0 : 1);
                mauSacRepository.save(ms);
                break;
            case "kich-co":
                KichCo kc = kichCoRepository.findById(id).orElse(null);
                if (kc == null) return ApiResponse.error("Không tìm thấy bản ghi!");
                kc.setTrangThai(kc.getTrangThai() == 1 ? 0 : 1);
                kichCoRepository.save(kc);
                break;
            default:
                return ApiResponse.error("Loại thuộc tính không hợp lệ: " + type);
        }
        return ApiResponse.ok("Chuyển trạng thái thành công!", null);
    }

    @Override
    public ApiResponse<Map<String, List<ThuocTinhDTO>>> getAllOptions() {
        Map<String, List<ThuocTinhDTO>> map = new HashMap<>();

        map.put("danhMuc", danhMucRepository.findByTrangThaiOrderByTenDanhMucAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaDanhMuc(), e.getTenDanhMuc(), 1, "danh-muc")).collect(Collectors.toList()));

        map.put("thuongHieu", thuongHieuRepository.findByTrangThaiOrderByTenThuongHieuAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaThuongHieu(), e.getTenThuongHieu(), 1, "thuong-hieu")).collect(Collectors.toList()));

        map.put("chatLieu", chatLieuRepository.findByTrangThaiOrderByTenChatLieuAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaChatLieu(), e.getTenChatLieu(), 1, "chat-lieu")).collect(Collectors.toList()));

        map.put("xuatXu", xuatXuRepository.findByTrangThaiOrderByTenXuatXuAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaXuatXu(), e.getTenXuatXu(), 1, "xuat-xu")).collect(Collectors.toList()));

        map.put("kieuDang", kieuDangRepository.findByTrangThaiOrderByTenKieuDangAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaKieuDang(), e.getTenKieuDang(), 1, "kieu-dang")).collect(Collectors.toList()));

        map.put("monTheThao", monTheThaoRepository.findByTrangThaiOrderByTenMonTheThaoAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaMonTheThao(), e.getTenMonTheThao(), 1, "mon-the-thao")).collect(Collectors.toList()));

        map.put("mauSac", mauSacRepository.findByTrangThaiOrderByTenMauAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaMau(), e.getTenMau(), 1, "mau-sac")).collect(Collectors.toList()));

        map.put("kichCo", kichCoRepository.findByTrangThaiOrderByTenKichCoAsc(1).stream()
                .map(e -> new ThuocTinhDTO(e.getId(), e.getMaKichCo(), e.getTenKichCo(), 1, "kich-co")).collect(Collectors.toList()));

        return ApiResponse.ok("Thành công", map);
    }
}
