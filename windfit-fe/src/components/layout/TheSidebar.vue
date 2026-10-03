<template>
  <aside class="windfit-sidebar" :class="{ 'collapsed': isCollapsed }">
    <!-- Brand Header -->
    <div class="sidebar-header">
      <router-link to="/admin/dashboard" class="brand-link">
        <div class="brand-logo-wrap">
          <i class="bi bi-wind brand-icon"></i>
        </div>
        <div class="brand-details" v-show="!isCollapsed">
          <span class="brand-name">WINDFIT</span>
          <span class="brand-sub">PERFORMANCE JACKETS</span>
        </div>
      </router-link>
      <button class="toggle-btn d-none d-lg-flex" @click="toggleSidebar" :title="isCollapsed ? 'Mở rộng' : 'Thu gọn'">
        <i :class="isCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'"></i>
      </button>
    </div>

    <!-- Navigation Menu -->
    <div class="sidebar-menu-wrapper custom-scrollbar">
      <!-- Section: TỔNG QUAN -->
      <div class="menu-section" v-if="authStore.isAdmin">
        <span class="section-title" v-show="!isCollapsed">TỔNG QUAN</span>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/dashboard" class="nav-link-item" active-class="active">
              <i class="bi bi-grid-1x2-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Bảng Điều Khiển</span>
            </router-link>
          </li>
        </ul>
      </div>

      <!-- Section: BÁN HÀNG & ĐƠN HÀNG -->
      <div class="menu-section">
        <span class="section-title" v-show="!isCollapsed">GIAO DỊCH & ĐƠN HÀNG</span>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/pos" class="nav-link-item pos-highlight" active-class="active">
              <i class="bi bi-lightning-charge-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Bán Hàng Tại Quầy (POS)</span>
              <span class="badge bg-warning text-dark ms-auto" v-show="!isCollapsed">HOT</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/hoa-don" class="nav-link-item" active-class="active">
              <i class="bi bi-receipt nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Quản Lý Hóa Đơn</span>
            </router-link>
          </li>
        </ul>
      </div>

      <!-- Section: SẢN PHẨM & KHO -->
      <div class="menu-section">
        <span class="section-title" v-show="!isCollapsed">SẢN PHẨM & KHO</span>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/san-pham" class="nav-link-item" active-class="active">
              <i class="bi bi-layers-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Áo Khoác Gió (SPCT)</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/thuoc-tinh" class="nav-link-item" active-class="active">
              <i class="bi bi-tags-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Thuộc Tính Sản Phẩm</span>
            </router-link>
          </li>
        </ul>
      </div>

      <!-- Section: KHUYẾN MẠI -->
      <div class="menu-section">
        <span class="section-title" v-show="!isCollapsed">KHUYẾN MẠI & GIẢM GIÁ</span>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/phieu-giam-gia" class="nav-link-item" active-class="active">
              <i class="bi bi-ticket-perforated-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Phiếu Giảm Giá (Voucher)</span>
            </router-link>
          </li>
          <li class="nav-item">
            <router-link to="/admin/dot-giam-gia" class="nav-link-item" active-class="active">
              <i class="bi bi-fire nav-icon text-danger"></i>
              <span class="nav-text" v-show="!isCollapsed">Đợt Giảm Giá (Sale)</span>
            </router-link>
          </li>
        </ul>
      </div>

      <!-- Section: TÀI KHOẢN -->
      <div class="menu-section">
        <span class="section-title" v-show="!isCollapsed">KHÁCH HÀNG & NHÂN SỰ</span>
        <ul class="nav-list">
          <li class="nav-item">
            <router-link to="/admin/khach-hang" class="nav-link-item" active-class="active">
              <i class="bi bi-people-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Quản Lý Khách Hàng</span>
            </router-link>
          </li>
          <li class="nav-item" v-if="authStore.isAdmin">
            <router-link to="/admin/nhan-vien" class="nav-link-item" active-class="active">
              <i class="bi bi-shield-lock-fill nav-icon"></i>
              <span class="nav-text" v-show="!isCollapsed">Nhân Viên & Phân Quyền</span>
            </router-link>
          </li>
        </ul>
      </div>
    </div>

    <!-- Sidebar Footer / User Profile -->
    <div class="sidebar-user-footer">
      <div class="user-info-card">
        <img :src="authStore.user?.anhDaiDien || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100'" 
             alt="Avatar" class="avatar-img" />
        <div class="user-details" v-show="!isCollapsed">
          <span class="user-name text-truncate">{{ authStore.user?.hoTen || 'Nhân viên WindFit' }}</span>
          <span class="badge" :class="authStore.isAdmin ? 'bg-danger' : 'bg-primary'">
            {{ authStore.isAdmin ? 'QUẢN TRỊ VIÊN' : 'NHÂN VIÊN' }}
          </span>
        </div>
        <button class="logout-btn ms-auto" @click="handleLogout" title="Đăng xuất" v-show="!isCollapsed">
          <i class="bi bi-box-arrow-right"></i>
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const isCollapsed = ref(false)

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}

function handleLogout() {
  if (confirm('Bạn có chắc chắn muốn đăng xuất khỏi hệ thống WindFit?')) {
    authStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.windfit-sidebar {
  width: 280px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: linear-gradient(180deg, #091224 0%, #0d1b38 100%);
  color: #e2e8f0;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.25);
  z-index: 1040;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.windfit-sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.25rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  text-decoration: none;
  color: #fff;
}

.brand-logo-wrap {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.35);
}

.brand-icon {
  font-size: 1.5rem;
  color: #ffffff;
}

.brand-details {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-weight: 800;
  font-size: 1.25rem;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #38bdf8, #818cf8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-sub {
  font-size: 0.65rem;
  letter-spacing: 1.5px;
  color: #94a3b8;
  font-weight: 600;
}

.toggle-btn {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #94a3b8;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.sidebar-menu-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 1.25rem 0.85rem;
}

.menu-section {
  margin-bottom: 1.25rem;
}

.section-title {
  display: block;
  font-size: 0.68rem;
  font-weight: 700;
  color: #64748b;
  letter-spacing: 1.2px;
  padding: 0 0.85rem 0.5rem;
  text-transform: uppercase;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.nav-link-item {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding: 0.65rem 0.85rem;
  color: #94a3b8;
  text-decoration: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
}

.nav-link-item:hover {
  color: #f8fafc;
  background: rgba(255, 255, 255, 0.06);
  transform: translateX(3px);
}

.nav-link-item.active {
  color: #ffffff;
  background: linear-gradient(90deg, rgba(14, 165, 233, 0.2) 0%, rgba(14, 165, 233, 0.05) 100%);
  border-left: 3px solid #0ea5e9;
  font-weight: 600;
}

.nav-icon {
  font-size: 1.2rem;
  min-width: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pos-highlight {
  background: rgba(245, 158, 11, 0.1);
  color: #fbbf24;
}

.pos-highlight:hover {
  background: rgba(245, 158, 11, 0.2);
  color: #fef3c7;
}

.sidebar-user-footer {
  padding: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(0, 0, 0, 0.2);
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #0ea5e9;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-name {
  font-weight: 600;
  font-size: 0.85rem;
  color: #f1f5f9;
}

.logout-btn {
  background: transparent;
  border: none;
  color: #ef4444;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.35rem;
  border-radius: 6px;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.15);
}

/* Scrollbar tùy chỉnh */
.custom-scrollbar::-webkit-scrollbar {
  width: 5px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
}
</style>
