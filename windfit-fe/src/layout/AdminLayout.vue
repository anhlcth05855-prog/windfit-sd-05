<template>
  <div class="admin-shell-wrapper">
    <!-- Sidebar -->
    <TheSidebar :isCollapsed="isSidebarCollapsed" />

    <!-- Main Content Area -->
    <div class="admin-main-viewport" :class="{ 'sidebar-collapsed': isSidebarCollapsed }">
      <!-- Top Navbar Matching Design -->
      <header class="windfit-top-header">
        <div class="header-left-side">
          <!-- Toggle Hamburger Button [≡] -->
          <button class="menu-toggle-btn" @click="toggleSidebar" title="Thu gọn / Mở rộng Sidebar">
            <i class="fa-solid fa-bars"></i>
          </button>

          <!-- Dynamic Page Title (All Caps) -->
          <h1 class="header-page-title">{{ currentDisplayTitle }}</h1>
        </div>

        <div class="header-right-side">
          <!-- Button: Cửa hàng Khách hàng -->
          <router-link to="/admin/pos" class="btn-storefront">
            <i class="fa-solid fa-store"></i>
            <span>Cửa hàng Khách hàng</span>
          </router-link>

          <!-- Button: Dark Mode Toggle (Moon) -->
          <button class="btn-moon-toggle" @click="toggleDarkMode" title="Chế độ tối / sáng">
            <i class="fa-regular fa-moon"></i>
          </button>

          <!-- User Profile Dropdown Pill -->
          <div class="user-profile-pill" @click="toggleUserMenu">
            <div class="user-avatar-circle">
              <i class="fa-solid fa-user"></i>
            </div>
            <div class="user-meta-lines">
              <span class="user-full-name">{{ authStore.user?.hoTen || 'Nguyễn Quản Lý' }}</span>
              <span class="user-role-badge">
                <i class="fa-solid fa-crown text-warning me-1"></i>
                <span>{{ authStore.isAdmin ? 'Quản lý (Admin)' : 'Nhân viên bán hàng' }}</span>
              </span>
            </div>
            <i class="fa-solid fa-chevron-down caret-arrow"></i>

            <!-- Dropdown Menu -->
            <div class="user-dropdown-menu" v-if="showUserMenu">
              <div class="dropdown-header">
                <strong>{{ authStore.user?.email || 'admin@windfit.vn' }}</strong>
              </div>
              <router-link to="/admin/nhan-vien" class="dropdown-item-link">
                <i class="fa-solid fa-id-badge"></i> Hồ sơ cá nhân
              </router-link>
              <div class="dropdown-divider"></div>
              <button class="dropdown-item-btn text-danger" @click="handleLogout">
                <i class="fa-solid fa-arrow-right-from-bracket"></i> Đăng xuất
              </button>
            </div>
          </div>
        </div>
      </header>

      <!-- Main Page Content Body -->
      <main class="page-content-canvas">
        <router-view v-slot="{ Component }">
          <transition name="fade-page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import TheSidebar from '../components/layout/TheSidebar.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isSidebarCollapsed = ref(false)
const showUserMenu = ref(false)

function toggleSidebar() {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

function toggleDarkMode() {
  document.body.classList.toggle('dark-mode')
}

function toggleUserMenu() {
  showUserMenu.value = !showUserMenu.value
}

function handleLogout() {
  if (confirm('Bạn có muốn đăng xuất khỏi hệ thống WindFit?')) {
    authStore.logout()
    router.push('/login')
  }
}

const currentDisplayTitle = computed(() => {
  const p = route.path
  if (p.includes('/admin/hoa-don/lich-su')) return 'LỊCH SỬ ĐƠN HÀNG'
  if (p.includes('/admin/hoa-don')) return 'QUẢN LÝ HÓA ĐƠN'
  if (p.includes('/admin/nhan-vien')) return 'QUẢN LÝ NHÂN VIÊN'
  if (p.includes('/admin/khach-hang')) return 'QUẢN LÝ KHÁCH HÀNG'
  if (p.includes('/admin/phieu-giam-gia')) return 'QUẢN LÝ PHIẾU GIẢM GIÁ'
  if (p.includes('/admin/dot-giam-gia')) return 'ĐỢT GIẢM GIÁ'
  if (p.includes('/admin/san-pham')) return 'QUẢN LÝ SẢN PHẨM'
  if (p.includes('/admin/thuoc-tinh')) return 'THUỘC TÍNH SẢN PHẨM'
  if (p.includes('/admin/pos')) return 'BÁN HÀNG TẠI QUẦY (POS)'
  if (p.includes('/admin/dashboard')) return 'TỔNG QUAN HỆ THỐNG'
  return route.meta?.title?.toUpperCase() || 'QUẢN TRỊ WINDFIT'
})
</script>

<style scoped>
.admin-shell-wrapper {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
}

.admin-main-viewport {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.25s ease;
}

.admin-main-viewport.sidebar-collapsed {
  margin-left: 78px;
}

/* Top Navbar */
.windfit-top-header {
  height: 68px;
  background-color: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
}

.header-left-side {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-toggle-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  border: 1px solid #bfdbfe;
  background-color: #eff6ff;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.05rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.menu-toggle-btn:hover {
  background-color: #dbeafe;
  color: #1d4ed8;
}

.header-page-title {
  font-size: 1.15rem;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: 0.5px;
  margin: 0;
  text-transform: uppercase;
}

/* Right Side Actions */
.header-right-side {
  display: flex;
  align-items: center;
  gap: 14px;
}

.btn-storefront {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #bfdbfe;
  background-color: #eff6ff;
  color: #2563eb;
  font-size: 0.86rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
}

.btn-storefront:hover {
  background-color: #dbeafe;
  color: #1d4ed8;
}

.btn-moon-toggle {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-moon-toggle:hover {
  background-color: #f1f5f9;
  color: #0f172a;
}

/* User Pill */
.user-profile-pill {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 14px 4px 6px;
  border-radius: 9999px;
  border: 1px solid #e2e8f0;
  background-color: #ffffff;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s ease;
}

.user-profile-pill:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.user-avatar-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #0f172a;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
}

.user-meta-lines {
  display: flex;
  flex-direction: column;
}

.user-full-name {
  font-size: 0.84rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.user-role-badge {
  font-size: 0.72rem;
  font-weight: 600;
  color: #059669;
  line-height: 1.2;
}

.caret-arrow {
  font-size: 0.68rem;
  color: #94a3b8;
}

.user-dropdown-menu {
  position: absolute;
  top: 48px;
  right: 0;
  width: 190px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  padding: 6px;
  z-index: 1050;
}

.dropdown-header {
  padding: 8px 10px;
  font-size: 0.75rem;
  color: #64748b;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 4px;
}

.dropdown-item-link,
.dropdown-item-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.84rem;
  font-weight: 500;
  color: #334155;
  text-decoration: none;
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s;
}

.dropdown-item-link:hover,
.dropdown-item-btn:hover {
  background-color: #f1f5f9;
}

.dropdown-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 4px 0;
}

/* Page Canvas */
.page-content-canvas {
  flex: 1;
  padding: 24px 28px;
}

.fade-page-enter-active,
.fade-page-leave-active {
  transition: opacity 0.18s ease;
}

.fade-page-enter-from,
.fade-page-leave-to {
  opacity: 0;
}
</style>
