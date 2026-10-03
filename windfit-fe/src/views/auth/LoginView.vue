<template>
  <div class="login-wrapper">
    <div class="login-card shadow-lg">
      <div class="brand-badge text-center mb-4">
        <div class="brand-icon-box mx-auto mb-2">
          <i class="bi bi-wind text-white fs-1"></i>
        </div>
        <h3 class="fw-bold text-dark mb-0">WINDFIT</h3>
        <p class="text-muted small">HỆ THỐNG QUẢN TRỊ BÁN ÁO KHOÁC GIÓ</p>
      </div>

      <!-- Alert Message -->
      <div v-if="errorMessage" class="alert alert-danger py-2 small d-flex align-items-center gap-2 mb-3">
        <i class="bi bi-exclamation-triangle-fill"></i>
        <span>{{ errorMessage }}</span>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label fw-semibold small">Tài khoản / Email / Mã NV</label>
          <div class="input-group">
            <span class="input-group-text bg-light"><i class="bi bi-person"></i></span>
            <input 
              type="text" 
              v-model="username" 
              class="form-control" 
              placeholder="admin@windfit.vn hoặc NV001" 
              required 
              :disabled="loading"
            />
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label fw-semibold small">Mật khẩu</label>
          <div class="input-group">
            <span class="input-group-text bg-light"><i class="bi bi-lock"></i></span>
            <input 
              type="password" 
              v-model="password" 
              class="form-control" 
              placeholder="••••••••" 
              required 
              :disabled="loading"
            />
          </div>
        </div>

        <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm mb-3" :disabled="loading">
          <span v-if="loading" class="spinner-border spinner-border-sm me-1"></span>
          <i v-else class="bi bi-box-arrow-in-right me-1"></i> 
          <span>{{ loading ? 'Đang xác thực...' : 'Đăng Nhập' }}</span>
        </button>

        <div class="demo-accounts p-2 rounded bg-light border text-center">
          <span class="d-block small text-muted mb-1">Tài khoản mẫu thử nghiệm nhanh:</span>
          <div class="d-flex gap-2 justify-content-center">
            <button type="button" class="btn btn-outline-danger btn-sm" @click="fillAdmin" :disabled="loading">
              <i class="bi bi-shield-check me-1"></i>Admin (NV001)
            </button>
            <button type="button" class="btn btn-outline-primary btn-sm" @click="fillStaff" :disabled="loading">
              <i class="bi bi-person me-1"></i>Nhân viên (NV002)
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { authService } from '../../services/api'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('admin@windfit.vn')
const password = ref('123456')
const loading = ref(false)
const errorMessage = ref('')

function fillAdmin() {
  username.value = 'admin@windfit.vn'
  password.value = '123456'
  errorMessage.value = ''
}

function fillStaff() {
  username.value = 'tuanlm@windfit.vn'
  password.value = '123456'
  errorMessage.value = ''
}

async function handleLogin() {
  loading.value = true
  errorMessage.value = ''
  try {
    const res = await authService.login(username.value, password.value)
    if (res.success && res.data) {
      authStore.setAuth(res.data, res.data.token)
      if (res.data.vaiTro === 'ADMIN') {
        router.push('/admin/dashboard')
      } else {
        router.push('/admin/pos')
      }
    } else {
      errorMessage.value = res.message || 'Đăng nhập không thành công!'
    }
  } catch (err) {
    errorMessage.value = err.message || 'Lỗi kết nối tới máy chủ! Vui lòng kiểm tra lại.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #091224 0%, #1e293b 100%);
  padding: 1.5rem;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: #ffffff;
  border-radius: 16px;
  padding: 2.5rem 2rem;
}

.brand-icon-box {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #0284c7 0%, #0ea5e9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(14, 165, 233, 0.4);
}
</style>
