# WINDFIT – TÀI LIỆU KỸ THUẬT (technical.md)

> Đọc kèm `business.md`. Tài liệu này nói **làm bằng gì, code theo quy ước nào, database/API ra sao**.
> Quy ước đặt tên và cấu trúc lấy cảm hứng từ `AGENTS.md` của dự án mẫu FashionStore, điều chỉnh cho WindFit.

---

## 1. Hiện trạng code WindFit (đã kiểm tra trong `SD_05-master`)

**Đã có**
- Backend `windfit-be`: Java 17, Spring Boot 3.3.4, JPA, JWT, 26 entity, các controller: `Auth`, `HoaDon`, `SanPham`, `PhieuGiamGia`, `NhanVien`; service cho hóa đơn, sản phẩm/biến thể, giảm giá, thuộc tính, nhân viên. Trả về `ApiResponse<T>`.
- Frontend `windfit-fe` (Vue 3 + Vite + Pinia + Bootstrap 5): trang admin (đơn hàng, POS checkout, sản phẩm + biến thể, thuộc tính, phiếu giảm giá, khách hàng, nhân viên, dashboard), modal chi tiết/in hóa đơn.
- DB SQL Server `WindFit_DB` (script `database/windfit_db_sqlserver.sql`), ERD khớp 26 bảng.

**Còn thiếu so với tài liệu nghiệp vụ**

| Thiếu | Ghi chú |
|---|---|
| **Website cho khách** (trang chủ, danh sách/chi tiết SP, giỏ, checkout, đơn của tôi, đăng nhập khách) | Chưa có view nào cho `CUSTOMER` |
| Tồn kho 2 vị trí + **tạm giữ 15 phút** + job nhả hàng | Hiện chỉ có `san_pham_chi_tiet.so_luong_ton` |
| **Ca bán hàng POS**, đối soát ca | Chưa có bảng/API |
| Thanh toán **VNPAY, MoMo**, callback | Chưa có; FashionStore có sẵn VNPAY để tham khảo |
| **Hạng thành viên**, tích lũy, phiếu giảm giá sinh nhật, **danh sách đen** | `khach_hang` chưa có cột tích lũy/hạng |
| Vai trò **KHO**, **BAN_HANG** (hiện chỉ ADMIN/STAFF) | |
| **Nhật ký thao tác nhạy cảm**, doanh số nhân viên | |
| **Giá vốn**, lợi nhuận gộp | `san_pham_chi_tiet` chỉ có `gia_ban` |
| Báo cáo ngày/tháng, xuất Excel/PDF | Có `DashboardView.vue` nhưng chưa đủ |
| Gửi Email/SMS xác nhận | |

**Vấn đề cần sửa trước khi làm tính năng mới**
1. `SecurityConfig` đang `.anyRequest().permitAll()` → **mọi API (trừ `/api/v1/admin/**`) đều mở**. Phải đổi thành mặc định `authenticated()` + phân quyền theo vai trò.
2. `pom.xml` đang khai báo cả `mssql-jdbc`, `postgresql`, `h2`. Chốt **SQL Server** (đúng với script DB của nhóm), bỏ driver thừa.
3. Mật khẩu (`mat_khau` của `nhan_vien`, `khach_hang`) phải là **BCrypt**; kiểm tra dữ liệu seed.
4. Đặt tên DTO không thống nhất (`...DTO` lẫn `...Request/Response`) → code **mới** dùng `Request`/`Response`.

## 2. Technology stack (giữ nguyên của nhóm)

| Lớp | Công nghệ |
|---|---|
| Backend | Java 17, Spring Boot 3.3.x, Spring Web, Spring Data JPA (Hibernate), Spring Security, Validation, Lombok |
| Auth | JWT (jjwt 0.11.5), BCrypt |
| DB | Microsoft SQL Server (`WindFit_DB`) |
| Frontend | Vue 3 (Composition API, `<script setup>`), Vite, Pinia, Vue Router 4, Axios, Bootstrap 5 + Bootstrap Icons |
| Thanh toán | VNPAY (sandbox), MoMo (sandbox) |
| Email | Spring Mail (SMTP) |
| Xuất file | Apache POI (Excel), OpenPDF hoặc iText (PDF) |
| Lịch chạy | Spring `@Scheduled` |
| Mã vạch | Quét bằng đầu đọc USB (giả lập bàn phím) → ô input; không cần thư viện camera ở MVP |
| DevOps | Git, Docker Compose (SQL Server + BE + FE) – tùy chọn |

## 3. Quy ước code (BẮT BUỘC cho AI)

**Backend**
- Package gốc `com.windfit`: `controller`, `service`, `service.impl`, `repository`, `entity`, `dto`, `security`, `config`, `exception`, `scheduler`.
- Controller **chỉ** nhận request/trả response/validate. **Không** viết nghiệp vụ trong controller.
- Service tách **interface + impl** (`HoaDonService` / `HoaDonServiceImpl`).
- Dùng Lombok `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`; tiêm phụ thuộc bằng `@RequiredArgsConstructor` (không `@Autowired` trên field).
- Repository kế thừa `JpaRepository<Entity, Long>` (đồng bộ kiểu id đang dùng trong entity hiện có – kiểm tra trước khi viết).
- DTO: `XxxRequest`, `XxxResponse`. Không trả entity trực tiếp ra API.
- Mọi API trả `ApiResponse<T>` hiện có: `{ "success": true, "message": "...", "data": ... }`. Lỗi nghiệp vụ ném `BusinessException(message)`, bắt ở `@RestControllerAdvice` → HTTP 400/404/409.
- Phân quyền bằng `@PreAuthorize("hasRole('ADMIN')")` / `hasAnyRole('ADMIN','BAN_HANG')`; không so sánh chuỗi role trong controller.
- **Soft delete:** không `DELETE FROM`. Dùng `trang_thai` + (nếu bảng chưa có) cột `deleted_at`; query luôn lọc bản ghi đã xóa.
- Tiền dùng `BigDecimal` (DB: `DECIMAL(18,0)` cho VND). Thời gian dùng `LocalDateTime`, múi giờ `Asia/Ho_Chi_Minh`.
- Mọi thao tác thay đổi kho / thanh toán chạy trong `@Transactional` và **khóa bi quan** (`@Lock(PESSIMISTIC_WRITE)`) trên SKU (repo đã có sẵn kiểu truy vấn này).
- Không commit bí mật (JWT secret, mật khẩu DB, key VNPAY/MoMo). Dùng `application.properties` (đã `.gitignore`) + `application.properties.example`.

**Frontend**
- Cấu trúc: `src/views/{admin,pos,shop,auth}`, `src/components`, `src/stores` (Pinia), `src/services` (gọi API qua `api.js`), `src/router` (route guard theo vai trò).
- Giao diện cửa hàng (`shop`) tách layout riêng khỏi `AdminLayout`.
- Giỏ hàng online lưu Pinia + `localStorage`.
- Mọi gọi API đi qua `services/*.js`, không gọi axios trực tiếp trong component.

## 4. Mô hình dữ liệu

### 4.1 Bảng đã có (giữ nguyên, 26 bảng)
`vai_tro, nhan_vien, khach_hang, dia_chi_khach_hang, danh_muc, thuong_hieu, chat_lieu, xuat_xu, kieu_dang, mon_the_thao, mau_sac, kich_co, san_pham, san_pham_chi_tiet, hinh_anh_san_pham, phieu_giam_gia, khach_hang_phieu_giam_gia, dot_giam_gia, dot_giam_gia_chi_tiet, phuong_thuc_thanh_toan, hinh_thuc_thanh_toan, hoa_don, chi_tiet_hoa_don, lich_su_hoa_don, thanh_toan_hoa_don, lich_su_thanh_toan`.

### 4.2 Thay đổi bảng hiện có

| Bảng | Thay đổi |
|---|---|
| `san_pham_chi_tiet` | + `gia_von DECIMAL(18,0)`; **bỏ dần** `so_luong_ton` (tồn chuyển sang `ton_kho`) hoặc giữ làm cột tổng được cập nhật bằng service |
| `khach_hang` | + `tong_tich_luy DECIMAL(18,0) DEFAULT 0`, `id_hang_thanh_vien`, `so_lan_tu_choi_nhan INT DEFAULT 0`, `bi_chan_cod BIT DEFAULT 0` |
| `nhan_vien` | `id_vai_tro` trỏ tới 3 vai trò: `ADMIN`, `BAN_HANG`, `KHO` |
| `hoa_don` | + `kenh_ban` (`TAI_QUAY`/`ONLINE`), `id_ca_lam_viec` (null nếu online), `hinh_thuc_nhan` , `ma_xac_nhan`, `het_han_giu_hang DATETIME`, `id_phieu_giam_gia`, `loai_uu_dai_ap_dung` (`PHIEU_GIAM_GIA`/`HANG`/`NONE`). (`loai_hoa_don` trong ERD có thể dùng làm `kenh_ban`, đừng tạo trùng) |
| `chi_tiet_hoa_don` | + `gia_goc`, `gia_von_luc_ban` (để tính lợi nhuận đúng dù sau này đổi giá vốn), `so_luong_da_hoan` |
| `phieu_giam_gia` | + `nhom` (`HANG`/`CHUNG`/`DAC_QUYEN`/`SINH_NHAT`/`SU_KIEN`), `id_hang_thanh_vien` (null = mọi hạng) |
| `dot_giam_gia` | + `loai_dot` (`MUA`/`FLASH_SALE`) |

### 4.3 Bảng mới

```sql
-- Tồn kho theo SKU và vị trí
ton_kho (id PK, id_spct FK, vi_tri NVARCHAR(20) /* TRUNG_BAY | LUU_TRU */, so_luong INT NOT NULL,
         UNIQUE(id_spct, vi_tri))

-- Tạm giữ cho đơn online
giu_hang (id PK, id_hoa_don FK, id_spct FK, so_luong INT, het_han DATETIME,
          trang_thai NVARCHAR(20) /* DANG_GIU | DA_TRU | DA_NHA */, ngay_tao)

-- Mọi biến động kho (kiểm kê, nhập, điều chuyển, bán, hoàn...)
phieu_kho_lich_su (id PK, id_spct FK, vi_tri, loai /* NHAP | XUAT_BAN | DIEU_CHUYEN | KIEM_KE | HOAN | HUY_DON | CHINH_TAY */,
                   so_luong_thay_doi INT, so_luong_sau INT, id_hoa_don NULL, id_nhan_vien FK, ghi_chu, ngay_tao)

ca_lam_viec (id PK, id_nhan_vien FK, gio_mo, gio_dong NULL, tien_dau_ca, tien_mat_he_thong NULL,
             tien_mat_thuc_te NULL, chenh_lech NULL, trang_thai /* DANG_MO | DA_DONG */)

hang_thanh_vien (id PK, ma_hang, ten_hang, nguong_tich_luy DECIMAL, phan_tram_giam DECIMAL(5,2), trang_thai)

nhat_ky_thao_tac (id PK, id_nhan_vien FK, hanh_dong /* SUA_GIA | HUY_HOA_DON | CHINH_KHO | DUYET_HOAN_TIEN | ... */,
                  doi_tuong, id_doi_tuong, gia_tri_cu NVARCHAR(MAX), gia_tri_moi NVARCHAR(MAX), ngay_tao)

yeu_cau_hoan (id PK, id_hoa_don FK, id_chi_tiet_hoa_don FK, so_luong, ly_do, so_tien_hoan, trang_thai, id_nhan_vien_duyet NULL, ngay_tao)

cau_hinh_he_thong (khoa PK, gia_tri, mo_ta)  -- ví dụ: GIU_HANG_PHUT=15, NGUONG_SAP_HET=5, NGUONG_DEN_DANH_SACH=2, PHI_SHIP=30000
```

Seed bắt buộc: 3 vai trò, `hinh_thuc_thanh_toan` (`TIEN_MAT, QR, THE, VNPAY, MOMO, COD`), 3 hạng thành viên, 1 tài khoản ADMIN (BCrypt), `cau_hinh_he_thong`.

## 5. Thuật toán cốt lõi

### 5.1 Khả dụng
```
tonThuc(spct)   = Σ ton_kho.so_luong
dangGiu(spct)   = Σ giu_hang.so_luong WHERE trang_thai='DANG_GIU' AND het_han > now
khaDung(spct)   = tonThuc − dangGiu
```

### 5.2 Đặt hàng online (1 transaction)
1. Với từng dòng: khóa SKU (`PESSIMISTIC_WRITE`) → nếu `khaDung < soLuong` → lỗi 409 "Hết hàng".
2. Tính tiền theo thứ tự ở `business.md` §8.3 (đợt giảm → 1 ưu đãi tốt nhất → ship). **Tính ở server, không tin giá từ client.**
3. Tạo `hoa_don` (`MOI_TAO`, `CHUA_THANH_TOAN`) + `chi_tiet_hoa_don` + `giu_hang` (`het_han = now + 15'` nếu VNPAY/MoMo; COD theo `business.md` §11 #2).
4. Gửi email mã xác nhận (bất đồng bộ, lỗi gửi mail **không** làm hỏng đơn).
5. Trả `maHoaDon` + (nếu online) URL thanh toán.

### 5.3 Xác nhận thanh toán / xác nhận COD
Khóa SKU → trừ `ton_kho` (ưu tiên `LUU_TRU` cho online; POS ưu tiên `TRUNG_BAY`) → `giu_hang.trang_thai='DA_TRU'` → ghi `phieu_kho_lich_su` → đơn sang `CHO_DONG_GOI`, `DA_THANH_TOAN`. **Idempotent**: gọi callback 2 lần không được trừ 2 lần (kiểm tra trạng thái đơn trước).

### 5.4 Scheduler
- Mỗi phút: tìm `giu_hang` `DANG_GIU` quá `het_han` → `DA_NHA`, đơn chưa thanh toán → `DA_HUY` (lý do "Quá hạn thanh toán").
- Mỗi ngày 00:05: ngày 1 hằng tháng gửi phiếu giảm giá sinh nhật cho khách có `ngay_sinh` trong tháng.
- Đợt giảm giá: **không cần job** – tính giá hiệu lực theo `now` lúc truy vấn (`ngay_bat_dau <= now <= ngay_ket_thuc` và `trang_thai='DANG_DIEN_RA'`).

### 5.5 Hoàn tất đơn
Sau `HOAN_THANH`: cộng `tong_tich_luy` khách, xét lại hạng; hoàn hàng thì trừ lại phần đã hoàn.

## 6. Thiết kế API

Tiền tố `/api/v1`. Nhóm theo đối tượng dùng, quyền ghi bên phải.

**Công khai** (không cần đăng nhập)
```
GET  /public/san-pham?keyword&danhMuc&thuongHieu&mau&size&giaMin&giaMax&page   (giá đã áp đợt giảm, kèm trạng thái còn hàng/sắp hết/hết)
GET  /public/san-pham/{id}                 chi tiết + biến thể + khả dụng từng SKU
POST /auth/dang-ky | /auth/login | /auth/quen-mat-khau   (khách + nhân viên)
POST /public/phieu-giam-gia/validate              kiểm tra mã trước khi đặt
POST /public/thanh-toan/vnpay/ipn | /momo/ipn   callback cổng (kiểm chữ ký)
GET  /public/thanh-toan/vnpay/return
```

**Khách hàng** `CUSTOMER`
```
GET/PUT /khach-hang/me    GET/POST/PUT/DELETE /khach-hang/me/dia-chi
POST /don-hang            đặt hàng (§5.2)
GET  /don-hang/cua-toi    GET /don-hang/{ma}
PUT  /don-hang/{ma}/huy
POST /don-hang/{ma}/yeu-cau-hoan
```

**POS** `BAN_HANG`, `ADMIN`
```
POST /pos/ca/mo | /pos/ca/dong     GET /pos/ca/hien-tai
GET  /pos/san-pham/barcode/{ma}    GET /pos/san-pham/tim?keyword      (tồn sàn + kho + gợi ý bán kèm)
GET  /pos/khach-hang/sdt/{sdt}     POST /pos/khach-hang  (tạo nhanh)
POST /pos/hoa-don                  tạo + tính tiền + áp ưu đãi
POST /pos/hoa-don/{id}/thanh-toan  body: danh sách {hinhThuc, soTien, maGiaoDich}
GET  /pos/hoa-don/{id}/in          dữ liệu in hóa đơn
POST /pos/hoa-don/{id}/hoan        hoàn tại quầy
```

**Quản trị** (`ADMIN`; ghi chú vai trò khác)
```
/admin/san-pham, /admin/san-pham/{id}/bien-the     CRUD + giá vốn (giá vốn chỉ ADMIN xem)
/admin/thuoc-tinh/{type}                           danh mục/màu/size/thương hiệu/chất liệu/...
/admin/kho/ton | /nhap | /kiem-ke | /dieu-chuyen   `KHO`, `ADMIN`
/admin/hoa-don  + /{id}/trang-thai | /huy | /xac-nhan-cod | /duyet-hoan   (xác nhận COD: ADMIN, BAN_HANG)
/admin/khach-hang  + /{id}/blacklist
/admin/hang-thanh-vien
/admin/nhan-vien  + /{id}/doanh-so
/admin/phieu-giam-gia   /admin/dot-giam-gia
/admin/nhat-ky
/admin/bao-cao/tong-quan?tu&den&theo=ngay|thang
/admin/bao-cao/xuat?loai=excel|pdf
```
> Các endpoint hiện có (`/api/v1/hoa-don`, `/san-pham`, `/phieu-giam-gia`, `/dot-giam-gia`, `/thuoc-tinh`, `/nhan-vien`) **giữ lại và refactor dần**, không đập đi viết lại. Chỉ thêm tiền tố/phân quyền khi sửa.

## 7. Bảo mật
- JWT Bearer, claim: `sub`, `role`, `type` (`STAFF`|`CUSTOMER`). Token nhân viên và khách tách nhau.
- Mặc định `anyRequest().authenticated()`; whitelist `/public/**`, `/auth/**`.
- Giới hạn số lần đăng nhập sai [ĐỀ XUẤT: khóa 5 phút sau 5 lần].
- Callback VNPAY/MoMo: **bắt buộc xác thực chữ ký + so khớp số tiền + mã đơn**; nếu sai thì bỏ qua và log.
- Không tin client về giá/giảm giá/số lượng tồn. Chặn mass-assignment (DTO riêng, không bind entity).
- CORS chỉ cho origin của FE.

## 8. Cấu hình môi trường (`application.properties`, KHÔNG commit)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=WindFit_DB;encrypt=false
spring.datasource.username=...
spring.datasource.password=...
app.jwt.secret=...
app.jwt.expiration-ms=86400000
app.giu-hang-phut=15
vnpay.tmn-code=...      vnpay.hash-secret=...      vnpay.return-url=...
momo.partner-code=...   momo.access-key=...        momo.secret-key=...
spring.mail.host=...    spring.mail.username=...   spring.mail.password=...
```

## 9. Tham chiếu FashionStore (để AI "học" cách làm, không copy nguyên)

| Cần làm ở WindFit | Xem ở FashionStore |
|---|---|
| VNPAY: tạo URL, xử lý return/IPN | package `payment` (8 file) |
| Ca POS, quỹ tiền mặt, hoàn hàng tại quầy | `quanLyBanhangTaiQuay` (service/controller/DTO `PosSession*`, `PosPayment*`, `PosRefund*`) |
| Đơn hàng nhiều dòng, hoàn từng sản phẩm | `QuanLyDonHang`, `customer/order` |
| Thống kê dashboard | `QuanLyThongKe` + `components/admin/dashboard/*` |
| Khách hàng: giỏ, địa chỉ, đơn của tôi | `customer/*`, `QuanLyDiaChi`, `components/client/*` |
| Soft delete, status convention | `AGENTS.md` mục 5–6 |

FashionStore dùng **PostgreSQL, Java 21, Spring Boot 3, id `Integer`** còn WindFit dùng **SQL Server, Java 17**: khi tham khảo, **đổi theo WindFit**, đừng đổi stack của WindFit. Phần AI chatbot, GHTK, review, banner của FashionStore **không** nằm trong phạm vi WindFit.

## 10. Tiêu chí chất lượng
- Mỗi tính năng có test cho **luật nghiệp vụ khó** (tạm giữ/nhả, trừ kho idempotent, áp 1 ưu đãi, thanh toán kết hợp đúng tổng, callback lặp).
- Test đồng thời: 2 người cùng mua SKU cuối → chỉ 1 thành công.
- Không có N+1 ở danh sách sản phẩm/hóa đơn (dùng `@EntityGraph`/fetch join + phân trang).
