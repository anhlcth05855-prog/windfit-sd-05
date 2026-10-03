# WINDFIT — HỆ THỐNG QUẢN LÝ BÁN HÀNG ÁO KHOÁC GIÓ CHUYÊN DỤNG

> **ĐỒ ÁN TỐT NGHIỆP CHUYÊN NGÀNH PHÁT TRIỂN PHẦN MỀM**  
> **Mã Nhóm:** `SD_005` | **Lớp:** `SD05`  
> **Sinh viên thực hiện:** Hoàng Anh Thái & Nhóm SD_005  
> **Giảng viên hướng dẫn:** Cô Nguyễn Thúy Hằng (`HangNT169`)  

---

## 📖 Giới Thiệu Đề Tài
WindFit là hệ thống phần mềm quản lý và bán lẻ áo khoác gió thể thao hiệu năng cao (chống nước, cản gió, chống tia UV). Hệ thống hỗ trợ bán hàng đa kênh kết hợp giữa bán hàng trực tiếp tại quầy (POS) và đơn hàng trực tuyến (E-Commerce), quản lý kho biến thể sản phẩm (màu sắc, kích cỡ, chất liệu), cùng quy trình xử lý hóa đơn, khuyến mại và đổi trả hàng minh bạch.

---

## 🛠 Công Nghệ Sử Dụng

### Backend (`windfit-be`)
- **Ngôn ngữ & Nền tảng:** Java 17, Spring Boot 3.3.4
- **Bảo mật & Phân quyền:** Spring Security, JWT (JSON Web Token), BCrypt Password Encoder
- **Truy vấn Dữ liệu:** Spring Data JPA (Hibernate), kết nối Microsoft SQL Server
- **Quản lý Thư viện:** Apache Maven

### Frontend (`windfit-fe`)
- **Framework:** Vue.js 3 (Composition API, `<script setup>`)
- **Build Tool:** Vite
- **Quản lý Trạng thái:** Pinia Store
- **Điều hướng:** Vue Router 4 (kèm Route Navigation Guards theo vai trò)
- **Giao diện:** Bootstrap 5, Bootstrap Icons, Custom CSS Theme WindFit

### Cơ Sở Dữ Liệu
- Hệ quản trị CSDL: Microsoft SQL Server (`WindFit_DB`)
- Lược đồ gồm 26 bảng chuẩn hóa quan hệ thực thể (ERD), hỗ trợ đầy đủ các phân hệ người dùng, sản phẩm biến thể, đợt giảm giá, voucher và hóa đơn đa kênh.

---

## 📂 Cấu Trúc Hệ Thống

### 1. Kiến Trúc Backend (`windfit-be`)
- `com.windfit.entity`: Chứa 26 Entity JPA tương ứng với các bảng cơ sở dữ liệu.
- `com.windfit.repository`: Các Repository thao tác dữ liệu, tìm kiếm, phân trang và truy vấn khóa bi quan (`Pessimistic Lock`) đảm bảo an toàn tồn kho.
- `com.windfit.service` & `com.windfit.service.impl`: Xử lý nghiệp vụ (Hóa đơn, Bán lẻ POS, Khuyến mại, Sản phẩm & Biến thể, Phân quyền).
- `com.windfit.controller`: Các RESTful Controller tiếp nhận request và trả về định dạng `ApiResponse<T>` thống nhất.
- `com.windfit.dto`: Đối tượng truyền dữ liệu (Request / Response DTOs).
- `com.windfit.security`: Cấu hình Security Filter Chain, JWT Provider, Authentication Filter.
- `com.windfit.config`: Cấu hình CORS và các thiết lập toàn cục.

### 2. Kiến Trúc Frontend (`windfit-fe`)
- `src/views/AdminOrders.vue`: Màn hình Quản lý Hóa đơn & Đơn hàng (Lọc kênh bán, tab trạng thái, chi tiết đơn, chuyển trạng thái, hủy đơn, hoàn hàng).
- `src/views/PosCheckout.vue`: Màn hình Bán hàng tại quầy POS (Giỏ hàng trực quan, chọn khách hàng, voucher, thanh toán nhanh).
- `src/views/products/ProductList.vue`: Quản lý áo khoác gió và ma trận biến thể chi tiết (SPCT: Màu sắc, Kích cỡ, Tồn kho, Giá bán).
- `src/views/products/AttributeList.vue`: Quản lý 8 loại danh mục thuộc tính kỹ thuật.
- `src/views/vouchers/VoucherList.vue` & `CampaignList.vue`: Quản lý phiếu giảm giá và đợt khuyến mại.
- `src/views/customers/CustomerList.vue`: Quản lý khách hàng thành viên.
- `src/views/employees/EmployeeList.vue`: Quản lý nhân viên và phân quyền ADMIN / STAFF.

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Cấu Hình Cơ Sở Dữ Liệu
- Tạo database `WindFit_DB` trên SQL Server.
- Chạy file script khởi tạo dữ liệu: `database/windfit_db_sqlserver.sql`.
- Kiểm tra cấu hình kết nối tại: `windfit-be/src/main/resources/application.properties`.

### 2. Khởi Chạy Backend (Spring Boot)
```bash
cd windfit-be
.\mvnw.cmd spring-boot:run
```
- API Base URL: `http://localhost:8080`

### 3. Khởi Chạy Frontend (Vue 3 + Vite)
```bash
cd windfit-fe
npm install
npm run dev
```
- Truy cập trình duyệt: `http://localhost:5173`

---

## 🔑 Tài Khoản Trải Nghiệm Mẫu
- **Quản trị viên (ADMIN):**
  - Tài khoản / Email: `admin@windfit.vn`
  - Mật khẩu: `123456`
- **Nhân viên bán hàng (STAFF):**
  - Tài khoản / Email: `tuanlm@windfit.vn`
  - Mật khẩu: `123456`
