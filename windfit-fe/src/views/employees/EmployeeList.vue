<template>
  <div class="employee-list-page">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bold mb-1">Quản Lý Nhân Viên & Phân Quyền</h4>
        <p class="text-muted mb-0 small">Danh sách nhân viên cửa hàng, tài khoản đăng nhập & vai trò (ADMIN / STAFF)</p>
      </div>
      <button class="btn btn-primary d-flex align-items-center gap-1 shadow-sm">
        <i class="bi bi-person-plus-fill"></i>
        <span>Thêm Nhân Viên Mới</span>
      </button>
    </div>

    <div class="card border-0 shadow-sm rounded-3 p-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>#</th>
              <th>Mã NV</th>
              <th>Họ Tên</th>
              <th>Vai Trò</th>
              <th>Email</th>
              <th>Số Điện Thoại</th>
              <th>Ngày Tạo</th>
              <th>Trạng Thái</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>1</td>
              <td><span class="badge bg-light text-dark border">NV001</span></td>
              <td class="fw-semibold">Admin WindFit</td>
              <td><span class="badge bg-danger">ADMIN (Quản trị)</span></td>
              <td>admin@windfit.vn</td>
              <td>0988888888</td>
              <td><small class="text-muted">20/09/2026</small></td>
              <td><span class="badge bg-success-subtle text-success">Hoạt động</span></td>
            </tr>
            <tr>
              <td>2</td>
              <td><span class="badge bg-light text-dark border">NV002</span></td>
              <td class="fw-semibold">Lê Minh Tuấn</td>
              <td><span class="badge bg-primary">STAFF (Nhân viên)</span></td>
              <td>tuanlm@windfit.vn</td>
              <td>0977777777</td>
              <td><small class="text-muted">21/09/2026</small></td>
              <td><span class="badge bg-success-subtle text-success">Hoạt động</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
