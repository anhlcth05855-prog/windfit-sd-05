# WINDFIT – TÀI LIỆU NGHIỆP VỤ (business.md)

> Mục đích: cho AI (Claude Code, Cursor, Codex...) hiểu **hệ thống làm gì, quy tắc nào bắt buộc**.
> Nguồn: tài liệu khảo sát nhóm SD_005 + ERD/Use Case (`WindFit_ERD_UC_sd005_hoan_thien.drawio`) + code hiện có (`SD_05-master`).
> Quy ước: **[CHỐT]** = lấy từ tài liệu nhóm, bắt buộc theo. **[ĐỀ XUẤT]** = tài liệu chưa rõ, mình đề xuất mặc định, nhóm cần xác nhận (xem mục 11).

---

## 1. Tổng quan

WindFit là hệ thống bán **áo khoác gió** gồm 3 phần dùng chung một database:

| Phần | Người dùng | Mô tả |
|---|---|---|
| Website bán hàng | Khách hàng | Xem sản phẩm, giỏ hàng, đặt hàng, thanh toán online, xem đơn |
| POS (bán tại quầy) | Nhân viên bán hàng / thu ngân | Quét mã vạch, tư vấn, chốt món, thanh toán, in hóa đơn |
| Trang quản trị | Admin, nhân viên kho | Sản phẩm, tồn kho, hóa đơn, khách hàng, phiếu giảm giá, đợt giảm giá, nhân viên, báo cáo |

Hình thức thanh toán:
- **Tại quầy:** tiền mặt, QR (chuyển khoản), quẹt thẻ. Cho phép **kết hợp** nhiều hình thức trên 1 hóa đơn.
- **Online:** VNPAY, MoMo, COD.

## 2. Vai trò & quyền [CHỐT]

| Vai trò | Được làm | Không được |
|---|---|---|
| `ADMIN` | Toàn quyền, cấu hình, xem báo cáo tài chính | – |
| `BAN_HANG` (bán hàng/thu ngân) | Tạo đơn, áp mã, thu tiền, in hóa đơn | Sửa giá, xem lợi nhuận |
| `KHO` (nhân viên kho) | Nhập hàng, kiểm kê, điều chuyển, xuất hàng online | Tạo đơn |
| `CUSTOMER` | Mua online, xem/hủy đơn của mình, quản lý hồ sơ & địa chỉ | Vào trang quản trị |

Nhật ký bắt buộc ghi khi: **sửa giá, hủy hóa đơn, chỉnh kho tay, duyệt hoàn tiền**.

## 3. Sản phẩm & biến thể

- Sản phẩm: tên, thương hiệu, danh mục, chất liệu, xuất xứ, kiểu dáng, môn thể thao, đối tượng, tính năng, mô tả, ảnh, video.
- Biến thể = **Màu sắc × Kích cỡ (S, M, L, XL, 2XL)** → mỗi biến thể là 1 **SKU** (bảng `san_pham_chi_tiet`) có `ma_spct`, `ma_vach`, giá bán.
- **Mọi nghiệp vụ trừ/cộng kho (online và POS) bắt buộc đi qua SKU** [CHỐT].
- Nhập **giá vốn** + **giá niêm yết** → hệ thống tính **lợi nhuận gộp** (= giá bán thực tế − giá vốn). Chỉ ADMIN thấy.
- Hiển thị trạng thái trên website: còn hàng / sắp hết / hết hàng. [ĐỀ XUẤT] "sắp hết" khi khả dụng ≤ 5 (cấu hình được).

## 4. Tồn kho

Tồn chia **2 vị trí** [CHỐT]: `TRUNG_BAY` (trên sàn) và `LUU_TRU` (trong kho).

Với mỗi SKU có 3 con số:
- `tồn thực` = trưng bày + lưu trữ
- `đang tạm giữ` = tổng đơn online đang chờ (chưa trừ chính thức)
- `khả dụng` = tồn thực − đang tạm giữ  ← **con số website dùng để cho đặt**

Khi nào thay đổi kho:

| Sự kiện | Kho |
|---|---|
| Đơn tại quầy thanh toán xong | **Trừ chính thức** (ưu tiên trừ từ `TRUNG_BAY`) |
| Đơn online được đặt | **Tạm giữ** 15 phút (chưa trừ) |
| Online thanh toán thành công (VNPAY/MoMo) hoặc nhân viên bấm xác nhận (COD) | Chuyển **Tạm giữ → Trừ chính thức**, sang khâu đóng gói |
| Quá 15 phút chưa thanh toán / khách hủy / nhân viên hủy | **Giải phóng tạm giữ** (khả dụng tăng lại) |
| Hủy đơn đã trừ kho, trả hàng, hàng hoàn về | **Cộng lại kho** |
| Nhập hàng, kiểm kê, điều chuyển trưng bày ↔ lưu trữ | Do `KHO` thực hiện, ghi nhật ký |

Giỏ hàng online lưu **trên trình duyệt** (localStorage), **không** giữ/trừ kho.

## 5. Bán hàng tại quầy (POS)

Luồng: Đăng nhập ca → tìm SP (quét mã vạch / gõ tên) → xem tồn từng size/màu (sàn + kho) + gợi ý món phối/bán kèm → khách thử → chốt món → nhập SĐT khách (hiện hạng + ưu đãi) → thanh toán → in hóa đơn → tự trừ kho + ghi doanh số cho nhân viên.

Quy tắc:
- Phải **mở ca** mới bán được; đóng ca có đối soát tiền theo từng hình thức (tiền mặt / QR / thẻ).
- Giá hiển thị đã áp **đợt giảm giá** đang chạy (giá gốc gạch ngang + giá mới + nhãn %).
- Thanh toán **một hoặc nhiều dòng** (ví dụ 200.000đ tiền mặt + 350.000đ chuyển khoản); tổng các dòng phải **bằng** tổng cần thanh toán, nếu không không cho hoàn tất.
- Khách không có SĐT trong hệ thống: bán như khách lẻ, có thể tạo nhanh hồ sơ.
- Đơn gắn `id_nhan_vien` để tính doanh số theo ngày/tháng.

## 6. Bán hàng online

Luồng: xem SP → chọn màu/size → giỏ (trình duyệt) → nhập thông tin nhận hàng (tên, SĐT, địa chỉ) → nhập mã giảm giá → thanh toán → xác nhận đơn.

Quy tắc:
- Đặt thành công → tạo đơn **`MOI_TAO`** + tạm giữ kho 15 phút + gửi **mã xác nhận qua SMS/Email**.
- Mỗi đơn chỉ áp **1 ưu đãi tốt nhất** (xem mục 8).
- Hủy: khách tự hủy khi đơn chưa vào đóng gói [ĐỀ XUẤT]; sau đó phải liên hệ cửa hàng.
- COD: khách **từ chối nhận ≥ 2 lần không lý do** → vào danh sách đen (mục 7).

### Vòng đời đơn online [ĐỀ XUẤT, khớp status trong code]

```
MOI_TAO ──(thanh toán OK / NV xác nhận COD)──> CHO_DONG_GOI ──> DANG_GIAO ──> HOAN_THANH
   │                                                                  │
   ├──(quá 15' / khách hủy / NV hủy)──> DA_HUY                        └──> GIAO_THAT_BAI ──> (hoàn kho) ──> DA_HUY
HOAN_THANH ──(yêu cầu hoàn)──> CHO_DUYET_HOAN ──> DA_HOAN | TU_CHOI_HOAN
```

Đơn tại quầy chỉ có: `HOAN_THANH` (hoặc `DA_HUY`, `DA_HOAN`).
Trạng thái thanh toán tách riêng: `CHUA_THANH_TOAN` → `DA_THANH_TOAN` → (`DA_HOAN_TIEN`).

## 7. Khách hàng, hạng thành viên, danh sách đen

- Hồ sơ: họ tên, **SĐT (khóa nhận diện, duy nhất)**, địa chỉ, ngày sinh.
- Tích lũy tự cộng sau **mỗi đơn hoàn thành** (trừ phần đã hoàn).
- Hạng [CHỐT số liệu khảo sát, Admin chỉnh được]:

| Hạng | Điều kiện tích lũy | Ưu đãi |
|---|---|---|
| Thường | < 10.000.000đ | 0% |
| Bạc | ≥ 10.000.000đ | ~5% |
| Vàng | ≥ 20.000.000đ | 8% |
| VIP | [ĐỀ XUẤT] Admin gán/tùy cấu hình | Theo dõi chi tiêu riêng + mã đặc quyền |

  (Tên hạng là đề xuất; tài liệu chỉ nói mốc 10tr và 20tr. Admin có thể **thêm hạng / sửa mức**, nên lưu trong bảng, không hardcode.)
- **Tháng sinh nhật:** tự gửi mã ưu đãi (job chạy ngày 1 hằng tháng).
- **Danh sách đen:** đếm số lần từ chối nhận hàng không lý do; đạt ngưỡng (mặc định 2) → **cảnh báo** nhân viên và **chặn đặt COD** (vẫn cho thanh toán trước). [ĐỀ XUẤT mặc định, cấu hình được]

## 8. Phiếu giảm giá & đợt giảm giá

### 8.1 Đợt giảm giá (giảm thẳng vào giá niêm yết)
- Hình thức: theo mùa, hoặc **Flash Sale giờ vàng** (ví dụ 20:00–22:00).
- Chọn SKU, % giảm, thời gian bắt đầu/kết thúc → website & POS hiện giá mới.
- Hết hạn **tự trả về giá niêm yết** (tính theo thời gian thực khi truy vấn giá, hoặc job; không sửa tay giá gốc).

### 8.2 Phiếu giảm giá
- Cấu hình: mã, loại giảm (% hoặc tiền), đơn tối thiểu, mức giảm tối đa, số lượng, ngày bắt đầu/kết thúc.
- 3 nhóm: theo **hạng thành viên**; **chương trình chung** (tặng quà, giảm 30% tổng hóa đơn...); **mã riêng** VIP/sinh nhật; có thể thêm **mã sự kiện** (giải chạy, hội thao).
- Hết lượt dùng → tự vô hiệu. Quá hạn → từ chối. **Mỗi khách dùng 1 mã 1 lần.**

### 8.3 Quy tắc áp dụng [CHỐT + ĐỀ XUẤT cách tính]
1. Giá từng dòng = giá sau **đợt giảm giá** (nếu có).
2. Cộng thành **tạm tính**.
3. Chọn **đúng 1** trong: (a) phiếu giảm giá, (b) ưu đãi hạng thành viên → **lấy cái cho giảm nhiều hơn**, không cộng dồn.
4. `tổng thanh toán = tạm tính − giảm + phí ship`.

## 9. Hóa đơn & thanh toán

- Mã hóa đơn: `HD-YYYYMMDD-NNN` (NNN tăng theo ngày), ví dụ `HD-20260920-001`.
- Hóa đơn in ra có: mã SP, size, màu, số lượng, đơn giá, họ tên, ngày sinh, SĐT khách, tên nhân viên bán, **chính sách đổi trả**.
- Lịch sử thanh toán lưu để **đối soát ca** và **hoàn tiền**.
- VNPAY/MoMo: chuyển `DA_THANH_TOAN` **khi cổng báo về** (IPN/callback, kiểm chữ ký). COD: khi **giao thành công**.
- Hoàn tiền: chỉ ADMIN hoặc người được duyệt; hoàn đúng số tiền khách **thực trả** cho món đó (sau giảm giá).

## 10. Thống kê – báo cáo

Theo **ngày** và **tháng**, gộp **cả quầy + online**:
- Doanh thu, lợi nhuận gộp, số đơn.
- Dòng tiền theo **tiền mặt / QR / quẹt thẻ** (và VNPAY/MoMo/COD cho online).
- Hàng bán chạy, hàng sắp hết, hàng tồn lâu.
- Doanh số từng nhân viên.
- Xuất **Excel** và **PDF**. Lợi nhuận chỉ ADMIN xem.

## 11. Câu hỏi mở – nhóm cần chốt (AI không được tự đoán)

| # | Vấn đề trong tài liệu | Mặc định đang dùng |
|---|---|---|
| 1 | "Mỗi đơn áp 1 ưu đãi tốt nhất" vs "mỗi hóa đơn chỉ 1 chương trình" – đợt giảm giá có tính là "ưu đãi" không? | Đợt giảm giá = giảm giá bán, **cộng được** với 1 phiếu giảm giá/ưu đãi hạng (mục 8.3) |
| 2 | Tạm giữ 15 phút áp dụng cho COD không? COD chưa có thanh toán nên sẽ bị nhả sau 15 phút | **Chỉ 15 phút cho thanh toán online**; COD giữ đến khi NV xác nhận/hủy, tự hủy sau 24 giờ không xác nhận (cấu hình) |
| 3 | Danh sách đen: "cảnh báo hoặc chặn" | Cảnh báo ở lần 1 đạt ngưỡng, chặn COD khi đạt ngưỡng (ngưỡng = 2) |
| 4 | Tên và số hạng thành viên, hạng VIP lên bằng cách nào | Thường/Bạc/Vàng theo tích lũy, VIP do Admin gán |
| 5 | Phí vận chuyển: tài liệu không nhắc nhưng ERD có `phi_ship` | Phí cố định cấu hình + miễn phí từ X đồng (chưa tích hợp GHTK) |
| 6 | Quy tắc đổi trả (hạn ngày, điều kiện) và có đổi size tại quầy không | Hoàn trong 7 ngày, còn tem mác [chưa xác nhận] |
| 7 | "Quẹt thẻ" có tích hợp máy POS thật không | **Không**: nhân viên chọn "thẻ" và nhập mã giao dịch thủ công |
| 8 | "QR tại quầy" tự xác nhận hay nhân viên bấm xác nhận | Nhân viên bấm xác nhận đã nhận tiền |
| 9 | Có cần SMS thật không | MVP: gửi **Email**, SMS để interface + mock |
| 10 | Có làm AI chatbot, đánh giá sản phẩm, banner như FashionStore không | **Không** (ngoài phạm vi tài liệu WindFit) |
