<template>
  <div class="customer-list-page">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="fw-bold mb-1">Quản Lý Khách Hàng</h4>
        <p class="text-muted mb-0 small">Danh sách khách hàng thành viên, lịch sử mua sắm và địa chỉ giao hàng</p>
      </div>
      <button class="btn btn-primary d-flex align-items-center gap-1 shadow-sm">
        <i class="bi bi-person-plus"></i>
        <span>Thêm Khách Hàng</span>
      </button>
    </div>

    <div class="card border-0 shadow-sm rounded-3 p-4">
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead class="table-light">
            <tr>
              <th>#</th>
              <th>Mã KH</th>
              <th>Họ Và Tên</th>
              <th>Số Điện Thoại</th>
              <th>Email</th>
              <th>Giới Tính</th>
              <th>Trạng Thái</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>1</td>
              <td><span class="badge bg-light text-dark border">KH001</span></td>
              <td class="fw-semibold">Nguyễn Văn An</td>
              <td>0912345678</td>
              <td>nguyenvanan@gmail.com</td>
              <td>Nam</td>
              <td><span class="badge bg-success-subtle text-success">Hoạt động</span></td>
            </tr>
            <tr>
              <td>2</td>
              <td><span class="badge bg-light text-dark border">KH002</span></td>
              <td class="fw-semibold">Trần Thị Mai</td>
              <td>0923456789</td>
              <td>tranmai@gmail.com</td>
              <td>Nữ</td>
              <td><span class="badge bg-success-subtle text-success">Hoạt động</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
