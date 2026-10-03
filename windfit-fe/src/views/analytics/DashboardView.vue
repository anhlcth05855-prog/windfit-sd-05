<template>
  <div class="dashboard-page">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bold mb-1">Bảng Điều Khiển Tổng Quan</h4>
        <p class="text-muted mb-0 small">Tổng hợp dữ liệu kinh doanh áo khoác gió thể thao WindFit</p>
      </div>
      <div class="d-flex gap-2">
        <button class="btn btn-outline-secondary btn-sm">
          <i class="bi bi-calendar3 me-1"></i> Hôm nay
        </button>
        <button class="btn btn-primary btn-sm">
          <i class="bi bi-download me-1"></i> Xuất Báo Cáo
        </button>
      </div>
    </div>

    <!-- Quick Stats Metric Cards -->
    <div class="row g-3 mb-4">
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm rounded-3 p-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="text-muted small fw-semibold">DOANH THU HÔM NAY</span>
            <div class="badge bg-success-subtle text-success p-2 rounded-circle">
              <i class="bi bi-cash-stack fs-5"></i>
            </div>
          </div>
          <h4 class="fw-bold mb-1">3.450.000 đ</h4>
          <span class="text-success small fw-semibold"><i class="bi bi-arrow-up-right"></i> +12% so với hôm qua</span>
        </div>
      </div>

      <div class="col-12 col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm rounded-3 p-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="text-muted small fw-semibold">ĐƠN HÀNG HÔM NAY</span>
            <div class="badge bg-primary-subtle text-primary p-2 rounded-circle">
              <i class="bi bi-cart-check fs-5"></i>
            </div>
          </div>
          <h4 class="fw-bold mb-1">5 Đơn</h4>
          <span class="text-primary small fw-semibold"><i class="bi bi-shop"></i> 2 Tại quầy, 3 Online</span>
        </div>
      </div>

      <div class="col-12 col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm rounded-3 p-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="text-muted small fw-semibold">SẢN PHẨM ÁO GIÓ</span>
            <div class="badge bg-warning-subtle text-warning p-2 rounded-circle">
              <i class="bi bi-layers fs-5"></i>
            </div>
          </div>
          <h4 class="fw-bold mb-1">10 Biến thể</h4>
          <span class="text-muted small">StormBreaker, AeroLite, NightGlow</span>
        </div>
      </div>

      <div class="col-12 col-sm-6 col-xl-3">
        <div class="card border-0 shadow-sm rounded-3 p-3">
          <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="text-muted small fw-semibold">CẢNH BÁO TỒN KHO</span>
            <div class="badge bg-danger-subtle text-danger p-2 rounded-circle">
              <i class="bi bi-exclamation-triangle fs-5"></i>
            </div>
          </div>
          <h4 class="fw-bold mb-1 text-danger">2 SPCT</h4>
          <span class="text-danger small fw-semibold">Sắp hết hàng (&lt; 10 cái)</span>
        </div>
      </div>
    </div>

    <!-- Quick Navigation Shortcuts -->
    <div class="card border-0 shadow-sm rounded-3 p-4 mb-4">
      <h6 class="fw-bold mb-3"><i class="bi bi-speedometer2 text-primary me-2"></i>Thao Tác Nhanh Nghiệp Vụ</h6>
      <div class="row g-3">
        <div class="col-md-3">
          <router-link to="/admin/pos" class="btn btn-warning w-100 py-3 fw-bold d-flex flex-column align-items-center gap-1 shadow-sm">
            <i class="bi bi-cash-coin fs-3"></i>
            <span>Bán Hàng POS Tại Quầy</span>
          </router-link>
        </div>
        <div class="col-md-3">
          <router-link to="/admin/hoa-don" class="btn btn-outline-primary w-100 py-3 fw-bold d-flex flex-column align-items-center gap-1">
            <i class="bi bi-receipt fs-3"></i>
            <span>Quản Lý Hóa Đơn</span>
          </router-link>
        </div>
        <div class="col-md-3">
          <router-link to="/admin/san-pham" class="btn btn-outline-dark w-100 py-3 fw-bold d-flex flex-column align-items-center gap-1">
            <i class="bi bi-box-seam fs-3"></i>
            <span>Kho Áo Khoác Gió</span>
          </router-link>
        </div>
        <div class="col-md-3">
          <router-link to="/admin/phieu-giam-gia" class="btn btn-outline-success w-100 py-3 fw-bold d-flex flex-column align-items-center gap-1">
            <i class="bi bi-ticket-perforated fs-3"></i>
            <span>Tạo Voucher Giảm Giá</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>
