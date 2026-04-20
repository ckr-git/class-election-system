/**
 * 格式化 ISO 时间字符串为 "YYYY-MM-DD HH:mm" 格式
 */
export function formatTime(time) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}
