import { ref } from 'vue'

const toasts = ref([])
let toastIdCounter = 0

export function useToast() {
  function addToast({ message, title = '', type = 'info', duration = 3500 }) {
    const id = ++toastIdCounter
    const toast = { id, message, title, type }
    toasts.value.push(toast)

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, duration)
    }

    return id
  }

  function removeToast(id) {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index !== -1) {
      toasts.value.splice(index, 1)
    }
  }

  function success(message, title = 'Thành công') {
    return addToast({ message, title, type: 'success' })
  }

  function error(message, title = 'Có lỗi xảy ra') {
    return addToast({ message, title, type: 'danger' })
  }

  function warning(message, title = 'Cảnh báo') {
    return addToast({ message, title, type: 'warning' })
  }

  function info(message, title = 'Thông tin') {
    return addToast({ message, title, type: 'info' })
  }

  function clearAll() {
    toasts.value = []
  }

  return {
    toasts,
    addToast,
    removeToast,
    success,
    error,
    warning,
    info,
    clearAll
  }
}
