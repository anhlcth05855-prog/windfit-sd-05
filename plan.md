# WINDFIT – KẾ HOẠCH TRIỂN KHAI (plan.md)

> Đọc kèm `business.md` (làm gì) và `technical.md` (làm bằng gì).
> Mỗi giai đoạn: **mục tiêu → việc cần làm → tiêu chí xong → prompt mẫu cho AI**.
> Làm **tuần tự**. Xong giai đoạn nào, chạy thử + commit rồi mới sang giai đoạn sau.

---

## Cách làm việc với AI (vibe coding đúng cách)

1. Đặt 3 file này + `README.md` vào thư mục gốc dự án. Với Claude Code / Cursor / Codex, copy phần "Quy ước code" trong `technical.md` vào file `AGENTS.md` hoặc `CLAUDE.md` để AI tự đọc mỗi phiên.
2. **Mỗi prompt = một việc nhỏ** (1 chức năng, 1 API, 1 màn hình). Đừng bảo "làm cả hệ thống".
3. Luôn bắt đầu prompt bằng: *"Đọc business.md, technical.md. Chỉ làm giai đoạn X, mục Y."*
4. Yêu cầu AI **đọc code hiện có trước khi sửa** và **không đổi những phần không liên quan**.
5. Sau mỗi lần AI làm: chạy BE + FE, tự thao tác thử, đọc diff, rồi `git commit`. Có lỗi thì dán nguyên log lỗi cho AI.
6. Gặp chỗ nghiệp vụ chưa rõ → xem `business.md` mục 11. **Bảo AI hỏi, không tự đoán.**
7. Không đưa key thật (VNPAY, DB, JWT) vào chat hoặc vào Git.

---

## Giai đoạn 0 – Dọn nền (1–2 ngày)

**Mục tiêu:** nền an toàn, chạy ổn trước khi thêm tính năng.

- [ ] Nhóm họp chốt 10 câu hỏi mở ở `business.md` §11, cập nhật lại file.
- [ ] Sửa `SecurityConfig`: bỏ `anyRequest().permitAll()`, dùng `authenticated()` + whitelist.
- [ ] Xóa driver thừa trong `pom.xml` (`postgresql`, `h2`), chốt SQL Server.
- [ ] Kiểm tra mật khẩu seed là BCrypt.
- [ ] Thêm `@RestControllerAdvice` + `BusinessException`.
- [ ] Chạy được BE + FE + DB từ máy sạch theo README.

**Xong khi:** gọi API không có token bị 401; đăng nhập admin vẫn vào được các trang cũ.

**Prompt mẫu**
> Đọc technical.md mục 1 và 7. Sửa SecurityConfig của windfit-be: mặc định mọi request phải đăng nhập, chỉ cho phép công khai /api/v1/auth/** và /api/v1/public/**. Giữ nguyên các endpoint cũ nhưng gắn phân quyền ADMIN/BAN_HANG/KHO hợp lý. Thêm GlobalExceptionHandler dùng ApiResponse. Liệt kê cho tôi những endpoint cũ bị ảnh hưởng.

---

## Giai đoạn 1 – Cơ sở dữ liệu & vai trò (2–3 ngày)

**Mục tiêu:** DB có đủ bảng/cột theo `technical.md` §4.

- [ ] Viết script migration `database/migration_01.sql` (không sửa script gốc): thêm cột, bảng mới, seed.
- [ ] Cập nhật/ tạo entity + repository tương ứng (Long id).
- [ ] Vai trò `ADMIN`, `BAN_HANG`, `KHO`, `CUSTOMER`; đăng nhập trả đúng role.
- [ ] Cập nhật file ERD `.drawio` cho khớp.
- [ ] Chuyển `so_luong_ton` hiện có sang bảng `ton_kho` (đưa hết vào `LUU_TRU` làm mặc định).

**Xong khi:** app khởi động không lỗi mapping; chạy migration 2 lần không hỏng (idempotent).

**Prompt mẫu**
> Đọc technical.md mục 4. Viết file database/migration_01.sql cho SQL Server theo mục 4.2 và 4.3, an toàn khi chạy lại (IF NOT EXISTS). Sau đó tạo entity JPA và repository cho các bảng mới theo đúng quy ước trong mục 3. Chưa viết service/controller.

---

## Giai đoạn 2 – Sản phẩm, biến thể, tồn kho (4–5 ngày)

**Mục tiêu:** quản lý sản phẩm + kho chính xác, là nền của mọi luồng bán.

- [ ] Form sản phẩm: thông tin, ảnh, video; sinh nhanh ma trận **Màu × Size (S–2XL)**, mỗi ô có SKU, mã vạch, giá bán, giá vốn.
- [ ] Lợi nhuận gộp hiển thị (chỉ ADMIN).
- [ ] `KhoService`: nhập hàng, kiểm kê, điều chuyển trưng bày ↔ lưu trữ, ghi `phieu_kho_lich_su`.
- [ ] Hàm khả dụng: `tonThuc − dangGiu` (technical §5.1).
- [ ] Màn hình kho (role `KHO`): tồn từng SKU theo 2 vị trí, lịch sử biến động.
- [ ] Nhật ký thao tác khi sửa giá / chỉnh kho tay.

**Xong khi:** nhập 50 áo → chuyển 10 ra sàn → tồn đúng; sửa giá/kho tay có dòng nhật ký.

**Prompt mẫu**
> Đọc business.md mục 3–4 và technical.md mục 5.1, 6. Viết KhoService + KhoController (nhập, kiểm kê, điều chuyển, xem tồn) cho windfit-be. Mọi thay đổi chạy trong @Transactional, khóa SKU bằng PESSIMISTIC_WRITE, ghi phieu_kho_lich_su. Quyền: KHO và ADMIN. Kèm unit test cho điều chuyển và kiểm kê.

---

## Giai đoạn 3 – Bán hàng tại quầy POS (5–7 ngày)

**Mục tiêu:** bán được một đơn tại quầy trọn vẹn.

- [ ] Mở/đóng ca, đối soát theo hình thức thanh toán.
- [ ] Tìm SP bằng quét mã vạch hoặc gõ tên; hiện tồn sàn + kho từng size/màu; gợi ý món phối/bán kèm (đơn giản: cùng danh mục khác loại / hay mua cùng).
- [ ] Nhập SĐT → hiện hạng + ưu đãi; tạo nhanh khách.
- [ ] Thanh toán **kết hợp** nhiều dòng, kiểm tổng khớp.
- [ ] Trừ kho từ `TRUNG_BAY`, mã hóa đơn `HD-YYYYMMDD-NNN`, ghi `id_nhan_vien`, in hóa đơn (đủ trường ở `business.md` §9).
- [ ] Hoàn hàng tại quầy → cộng kho.

**Xong khi:** bán 1 đơn có 2 dòng thanh toán (tiền mặt + QR), in được, kho giảm đúng, đóng ca khớp tiền.

**Prompt mẫu**
> Đọc business.md mục 5, 7, 8, 9 và technical.md mục 5, 6 (nhóm /pos). Mở rộng PosCheckout.vue và backend: thêm ca làm việc, tìm theo mã vạch, nhập SĐT hiện hạng, thanh toán kết hợp. Tham khảo cách làm ở FashionStore thư mục quanLyBanhangTaiQuay (chỉ để học, giữ stack/tên của WindFit). Làm backend trước, tôi test xong mới làm giao diện.

---

## Giai đoạn 4 – Phiếu giảm giá, đợt giảm giá, hạng thành viên (4–5 ngày)

**Mục tiêu:** một **bộ máy tính giá duy nhất** dùng chung cho POS và online.

- [ ] `PricingService`: giá hiệu lực (đợt giảm) → chọn 1 ưu đãi tốt nhất (phiếu giảm giá vs hạng) → tổng. **Cả POS và online gọi service này.**
- [ ] Phiếu giảm giá: 5 nhóm, giới hạn lượt, 1 khách 1 lần, quá hạn/hết lượt bị từ chối.
- [ ] Đợt giảm giá: mùa + Flash Sale giờ vàng; hiện giá gạch ngang + nhãn %.
- [ ] Hạng thành viên cấu hình được; tích lũy cộng sau đơn hoàn thành.
- [ ] Job sinh nhật.

**Xong khi:** có test cho các tổ hợp: chỉ đợt giảm; đợt giảm + phiếu giảm giá; phiếu giảm giá vs hạng (chọn cái lớn hơn); phiếu giảm giá hết lượt; khách dùng lại mã.

**Prompt mẫu**
> Đọc business.md mục 8 và 11 (#1). Tạo PricingService theo quy tắc 8.3, nhận danh sách {spctId, soLuong}, mã phiếu giảm giá, SĐT khách; trả về chi tiết từng dòng, số tiền giảm, loại ưu đãi áp dụng, tổng. Viết test JUnit cho các tổ hợp trong plan.md giai đoạn 4. Chưa đụng giao diện.

---

## Giai đoạn 5 – Website bán hàng online (6–8 ngày)

**Mục tiêu:** khách mua được hàng trên web.

- [ ] Layout riêng cho khách (header, danh mục, tìm kiếm, footer).
- [ ] Trang chủ, danh sách (lọc: danh mục, thương hiệu, màu, size, giá), chi tiết (chọn màu/size, báo còn/sắp hết/hết).
- [ ] Đăng ký/đăng nhập khách, hồ sơ, sổ địa chỉ.
- [ ] Giỏ hàng (Pinia + localStorage, chưa trừ kho).
- [ ] Checkout: thông tin nhận hàng, nhập mã, hiện giá giảm; **COD** trước.
- [ ] Đặt hàng → `MOI_TAO` + tạm giữ + email mã xác nhận (technical §5.2).
- [ ] Đơn của tôi, hủy đơn, yêu cầu hoàn.
- [ ] Scheduler nhả hàng quá hạn (technical §5.4).
- [ ] Admin: màn xác nhận COD, chuyển trạng thái đóng gói → giao → hoàn thành; nhân viên kho xuất hàng.

**Xong khi:** đặt đơn COD → kho "đang giữ" tăng → admin xác nhận → trừ chính thức; bỏ đơn 15 phút → tự nhả.

**Prompt mẫu**
> Đọc business.md mục 4, 6 và technical.md mục 5.2–5.4. Triển khai backend đặt hàng online (POST /api/v1/don-hang) đúng thuật toán 5.2 và scheduler nhả giữ hàng. Viết test đồng thời: 2 request cùng mua SKU cuối cùng, chỉ 1 thành công. Sau khi test qua, làm giao diện giỏ hàng + checkout bằng Vue theo quy ước mục 3.

---

## Giai đoạn 6 – Thanh toán VNPAY & MoMo (3–4 ngày)

**Mục tiêu:** thanh toán online chạy với sandbox.

- [ ] Tạo URL thanh toán VNPAY (tham khảo `payment` của FashionStore), trang return, **IPN xác thực chữ ký + số tiền**.
- [ ] MoMo tương tự.
- [ ] Callback idempotent; thành công → trừ kho chính thức → `CHO_DONG_GOI` (technical §5.3).
- [ ] Ghi `thanh_toan_hoa_don` + `lich_su_thanh_toan`.
- [ ] Hoàn tiền (ghi nhận, duyệt bởi ADMIN).

**Xong khi:** thanh toán sandbox thành công/thất bại/hủy đều xử lý đúng; gọi IPN 2 lần không trừ kho 2 lần.

**Prompt mẫu**
> Đọc technical.md mục 5.3 và 7. Thêm thanh toán VNPAY sandbox cho đơn online: endpoint tạo URL, return, IPN. Bắt buộc xác thực chữ ký, so khớp số tiền và mã đơn, và xử lý idempotent. Key đọc từ application.properties. Giải thích cách tôi test IPN ở máy local (ví dụ ngrok).

---

## Giai đoạn 7 – Khách hàng, danh sách đen, nhân viên, nhật ký (3–4 ngày)

- [ ] Quản lý khách: tích lũy, hạng, lịch sử mua, VIP chi tiêu riêng.
- [ ] Giao thất bại/từ chối nhận → tăng `so_lan_tu_choi_nhan`; đạt ngưỡng → chặn COD + cảnh báo ở POS/checkout.
- [ ] Quản lý nhân viên, phân quyền 3 vai trò, doanh số ngày/tháng.
- [ ] Màn hình xem nhật ký thao tác nhạy cảm.

**Xong khi:** khách bị chặn COD vẫn thanh toán online được; `BAN_HANG` không thấy giá vốn/lợi nhuận.

---

## Giai đoạn 8 – Thống kê & báo cáo (3–4 ngày)

- [ ] API tổng hợp theo ngày/tháng, gộp quầy + online.
- [ ] Chỉ số: doanh thu, lợi nhuận gộp, số đơn, dòng tiền theo hình thức; top bán chạy, sắp hết, tồn lâu, doanh số nhân viên.
- [ ] Dashboard biểu đồ (có thể tham khảo `components/admin/dashboard` của FashionStore).
- [ ] Xuất **Excel** (Apache POI) và **PDF**.

**Xong khi:** số liệu dashboard khớp tổng tay trên dữ liệu mẫu; đơn đã hủy/hoàn không bị tính vào doanh thu.

---

## Giai đoạn 9 – Kiểm thử, hoàn thiện, bàn giao (3–5 ngày)

- [ ] Kịch bản test end-to-end (bảng dưới).
- [ ] Dữ liệu mẫu đẹp để demo (≥ 15 sản phẩm, ảnh, khách, đơn).
- [ ] Docker Compose (SQL Server + BE + FE), README cập nhật.
- [ ] Cập nhật ERD, Use Case, tài liệu API (Swagger/springdoc).
- [ ] Rà soát bảo mật: không còn key trong Git, không còn endpoint mở.

| # | Kịch bản | Kết quả mong đợi |
|---|---|---|
| 1 | Bán tại quầy, trả 200k tiền mặt + 350k QR | Hóa đơn 1 đơn 2 dòng thanh toán, kho trừ, doanh số NV tăng |
| 2 | Online VNPAY, thanh toán thành công | `CHO_DONG_GOI`, kho trừ chính thức |
| 3 | Online, bỏ ngang 15 phút | Đơn `DA_HUY`, khả dụng tăng lại |
| 4 | Online COD → NV xác nhận → giao thất bại | Hoàn kho; tăng số lần từ chối |
| 5 | Khách đủ 20tr tích lũy | Lên hạng 8%; đơn kế tiếp tự áp |
| 6 | Phiếu giảm giá 30% vs ưu đãi hạng 8% | Áp phiếu giảm giá (lớn hơn), không cộng dồn |
| 7 | Flash sale 20:00–22:00 | Đúng giờ hiện giá giảm; hết giờ về giá gốc |
| 8 | 2 người mua SKU cuối cùng | Chỉ 1 người thành công |
| 9 | `BAN_HANG` gọi API sửa giá / xem lợi nhuận | 403 |
| 10 | Xuất báo cáo tháng Excel/PDF | Số khớp dashboard |

---

## Thứ tự ưu tiên nếu thiếu thời gian

1. **Bắt buộc:** GĐ 0, 1, 2, 3, 4 (POS + kho + giá).
2. **Quan trọng:** GĐ 5, 6 (online + thanh toán).
3. **Có thì tốt:** GĐ 7, 8; MoMo; SMS thật; gợi ý bán kèm thông minh.

## Ngoài phạm vi (không làm trừ khi nhóm yêu cầu)
AI chatbot, đánh giá sản phẩm, banner, tích hợp GHTK, đăng nhập Google/Facebook, ứng dụng di động, máy quẹt thẻ thật.
