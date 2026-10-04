import Swal from 'sweetalert2'

// Custom styling matching WindFit Theme
const customSwal = Swal.mixin({
  customClass: {
    popup: 'rounded-4 shadow-lg border-0 p-4',
    title: 'fs-5 fw-bold text-dark mb-2',
    htmlContainer: 'text-secondary fs-6 mb-3',
    confirmButton: 'btn btn-primary px-4 py-2 rounded-3 me-2 fw-semibold',
    cancelButton: 'btn btn-outline-secondary px-4 py-2 rounded-3 fw-semibold',
    input: 'form-control rounded-3 py-2 px-3'
  },
  buttonsStyling: false
})

/**
 * Popup xác nhận hành động thông thường (Màu xanh Primary)
 */
export async function showConfirm(title, text, confirmButtonText = 'Đồng ý', cancelButtonText = 'Hủy bỏ') {
  const result = await customSwal.fire({
    title,
    text,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText,
    cancelButtonText,
    reverseButtons: true,
    focusConfirm: false
  })
  return result.isConfirmed
}

/**
 * Popup xác nhận hành động nguy hiểm / Hủy / Đổi trả (Nút đỏ Cảnh báo)
 */
export async function showDangerConfirm(title, text, confirmButtonText = 'Xác nhận', cancelButtonText = 'Quay lại') {
  const result = await Swal.fire({
    title,
    text,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText,
    cancelButtonText,
    reverseButtons: true,
    customClass: {
      popup: 'rounded-4 shadow-lg border-0 p-4',
      title: 'fs-5 fw-bold text-dark mb-2',
      htmlContainer: 'text-secondary fs-6 mb-3',
      confirmButton: 'btn btn-danger px-4 py-2 rounded-3 me-2 fw-semibold',
      cancelButton: 'btn btn-outline-secondary px-4 py-2 rounded-3 fw-semibold'
    },
    buttonsStyling: false
  })
  return result.isConfirmed
}

/**
 * Popup nhập lý do (Thay thế window.prompt thô sơ)
 */
export async function showPrompt(title, text, placeholder = 'Nhập lý do tại đây...', confirmButtonText = 'Xác nhận') {
  const result = await Swal.fire({
    title,
    text,
    input: 'textarea',
    inputPlaceholder: placeholder,
    showCancelButton: true,
    confirmButtonText,
    cancelButtonText: 'Đóng',
    reverseButtons: true,
    customClass: {
      popup: 'rounded-4 shadow-lg border-0 p-4',
      title: 'fs-5 fw-bold text-dark mb-2',
      htmlContainer: 'text-secondary fs-6 mb-3',
      input: 'form-control rounded-3 p-3 my-2',
      confirmButton: 'btn btn-danger px-4 py-2 rounded-3 me-2 fw-semibold',
      cancelButton: 'btn btn-outline-secondary px-4 py-2 rounded-3 fw-semibold'
    },
    buttonsStyling: false,
    inputValidator: (value) => {
      if (!value || !value.trim()) {
        return 'Vui lòng nhập lý do để tiếp tục!'
      }
      return null
    }
  })

  if (result.isConfirmed) {
    return result.value.trim()
  }
  return null
}

/**
 * Hệ thống Toast thông báo góc trên bên phải (Tự tắt sau 2.5s)
 */
const Toast = Swal.mixin({
  toast: true,
  position: 'top-end',
  showConfirmButton: false,
  timer: 2500,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.onmouseenter = Swal.stopTimer
    toast.onmouseleave = Swal.resumeTimer
  }
})

export const showToast = {
  success(message, title = '') {
    Toast.fire({
      icon: 'success',
      title: title || message,
      text: title ? message : ''
    })
  },
  error(message, title = '') {
    Toast.fire({
      icon: 'error',
      title: title || message,
      text: title ? message : ''
    })
  },
  warning(message, title = '') {
    Toast.fire({
      icon: 'warning',
      title: title || message,
      text: title ? message : ''
    })
  },
  info(message, title = '') {
    Toast.fire({
      icon: 'info',
      title: title || message,
      text: title ? message : ''
    })
  }
}
