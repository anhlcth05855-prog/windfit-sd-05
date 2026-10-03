<template>
  <div class="admin-layout-wrapper">
    <!-- Toast Container for system notifications -->
    <ToastContainer />

    <!-- Sidebar -->
    <TheSidebar />

    <!-- Main Content Container -->
    <div class="admin-main-container">
      <!-- Header (inlined) -->
      <header class="windfit-header no-print">
        <div class="header-left">
          <div class="breadcrumb-wrap">
            <span class="breadcrumb-item-lead text-muted">
              <i class="bi bi-tag-fill me-1 text-primary"></i> Phân Hệ
            </span>
            <i class="bi bi-chevron-right breadcrumb-separator"></i>
            <span class="breadcrumb-item-current fw-bold text-dark">
              {{ pageTitle }}
            </span>
          </div>
        </div>

        <div class="header-right">
          <div class="status-pill d-none d-md-flex">
            <span class="pulse-dot"></span>
            <span class="status-text">Hệ Thống Sẵn Sàng</span>
          </div>

          <router-link to="/admin/phieu-giam-gia" class="btn btn-outline-primary btn-sm fw-bold d-flex align-items-center gap-1 shadow-sm px-3">
            <i class="bi bi-ticket-perforated-fill"></i>
            <span class="d-none d-sm-inline">Phiếu Giảm Giá</span>
          </router-link>

          <router-link to="/admin/dot-giam-gia" class="btn btn-primary btn-sm fw-bold d-flex align-items-center gap-1 shadow-sm px-3">
            <i class="bi bi-fire"></i>
            <span class="d-none d-sm-inline">Đợt Giảm Giá</span>
          </router-link>

          <div class="user-dropdown">
            <img :src="authStore.user?.anhDaiDien || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100'"
                 alt="Avatar" class="header-avatar" />
            <div class="user-meta d-none d-sm-flex flex-column text-start">
              <span class="header-user-name fw-bold">{{ authStore.user?.hoTen || 'Admin' }}</span>
              <span class="header-user-role text-muted small">{{ authStore.isAdmin ? 'Quản Trị Viên' : 'Nhân Viên' }}</span>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Views Injection -->
      <main class="admin-page-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>

      <!-- Footer -->
      <footer class="windfit-footer no-print">
        <div class="footer-container">
          <div class="footer-left">
            <span class="fw-semibold text-dark">WindFit Performance Jackets</span> &copy; 2026.
            <span class="text-muted d-none d-sm-inline ms-1">Hệ thống Quản trị &amp; Bán hàng Áo khoác gió thể thao.</span>
          </div>
          <div class="footer-right">
            <span class="badge bg-light text-muted border me-2">Phiên bản v1.0.0</span>
            <span class="text-success small fw-semibold">
              <i class="bi bi-shield-check"></i> Đồ án tốt nghiệp
            </span>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import TheSidebar from '../components/layout/TheSidebar.vue'
import ToastContainer from '../components/common/ToastContainer.vue'

const route = useRoute()
const authStore = useAuthStore()

const pageTitle = computed(() => {
  return route.meta?.title || 'Bảng Điều Khiển'
})
</script>

<style scoped>
.admin-layout-wrapper {
  display: flex;
  min-height: 100vh;
  background-color: #f8fafc;
}

.admin-main-container {
  flex: 1;
  margin-left: 280px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.admin-page-content {
  flex: 1;
  padding: 1.75rem;
}

/* Page Transition */
.fade-page-enter-active,
.fade-page-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-page-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.fade-page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (max-width: 991.98px) {
  .admin-main-container {
    margin-left: 0;
  }
}

/* Header Styles */
.windfit-header {
  height: 72px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.75rem;
  position: sticky;
  top: 0;
  z-index: 1030;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.breadcrumb-wrap {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
}

.breadcrumb-separator {
  font-size: 0.75rem;
  color: #94a3b8;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.status-pill {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
  padding: 0.35rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.78rem;
  font-weight: 600;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  background-color: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  animation: pulse-ring 1.8s infinite;
}

@keyframes pulse-ring {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(16, 185, 129, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

.icon-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  width: 38px;
  height: 38px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #475569;
  position: relative;
  cursor: pointer;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: #e2e8f0;
  color: #0ea5e9;
}

.badge-count {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ef4444;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  border-radius: 9999px;
  padding: 0.15rem 0.4rem;
  border: 2px solid white;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 0.65rem;
  padding-left: 0.5rem;
  border-left: 1px solid #e2e8f0;
}

.header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #0ea5e9;
}

.header-user-name {
  font-size: 0.88rem;
  color: #0f172a;
}

.header-user-role {
  font-size: 0.72rem;
  margin-top: -2px;
}

/* Footer Styles */
.windfit-footer {
  padding: 1rem 1.75rem;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
  font-size: 0.82rem;
}

.footer-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem;
}
</style>
