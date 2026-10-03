import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // Khởi tạo state từ localStorage nếu có
  const savedUser = localStorage.getItem('windfit_user')
  const savedToken = localStorage.getItem('windfit_token')

  const user = ref(savedUser ? JSON.parse(savedUser) : {
    id: 1,
    maNhanVien: 'NV001',
    hoTen: 'Quản Trị Viên WindFit',
    email: 'admin@windfit.vn',
    vaiTro: 'ADMIN',
    anhDaiDien: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100'
  })

  const token = ref(savedToken || 'dummy_admin_token')

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.vaiTro === 'ADMIN')
  const isStaff = computed(() => user.value?.vaiTro === 'STAFF')

  function setAuth(userData, tokenValue) {
    user.value = userData
    token.value = tokenValue
    localStorage.setItem('windfit_user', JSON.stringify(userData))
    localStorage.setItem('windfit_token', tokenValue)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('windfit_user')
    localStorage.removeItem('windfit_token')
  }

  return {
    user,
    token,
    isAuthenticated,
    isAdmin,
    isStaff,
    setAuth,
    logout
  }
})
