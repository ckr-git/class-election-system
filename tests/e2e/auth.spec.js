const { test, expect } = require('@playwright/test')

const BASE_URL = process.env.BASE_URL || 'http://localhost:8081'

test.describe('Auth Flow', () => {
  test('login page loads correctly', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/login`)
    await expect(page.getByRole('heading', { name: '班级干部评选系统' })).toBeVisible()
    await expect(page.getByRole('button', { name: '登录' })).toBeVisible()
    await expect(page.getByRole('link', { name: '注册账号' })).toBeVisible()
  })

  test('empty form shows validation errors', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/login`)
    await page.getByRole('button', { name: '登录' }).click()
    await expect(page.getByText('请输入学号/工号')).toBeVisible()
    await expect(page.getByText('请输入密码')).toBeVisible()
  })

  test('admin login redirects to dashboard', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/login`)
    await page.getByRole('textbox', { name: '请输入学号/工号' }).fill('admin')
    await page.getByRole('textbox', { name: '请输入密码' }).fill('admin123')
    await page.getByRole('button', { name: '登录' }).click()
    await page.waitForURL('**/admin/dashboard')
    await expect(page.getByText('总用户数')).toBeVisible()
  })

  test('student login redirects to home', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/login`)
    await page.getByRole('textbox', { name: '请输入学号/工号' }).fill('testuser001')
    await page.getByRole('textbox', { name: '请输入密码' }).fill('123456')
    await page.getByRole('button', { name: '登录' }).click()
    await page.waitForURL('**/home')
    await expect(page.getByText('欢迎使用班级干部评选系统')).toBeVisible()
  })

  test('unauthenticated access redirects to login', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/admin/dashboard`)
    await page.waitForURL('**/login')
  })
})
