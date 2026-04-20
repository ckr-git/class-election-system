const { test, expect } = require('@playwright/test')

const BASE_URL = process.env.BASE_URL || 'http://localhost:8081'

test.describe('Admin Elections', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(`${BASE_URL}/#/login`)
    await page.getByRole('textbox', { name: '请输入学号/工号' }).fill('admin')
    await page.getByRole('textbox', { name: '请输入密码' }).fill('admin123')
    await page.getByRole('button', { name: '登录' }).click()
    await page.waitForURL('**/admin/dashboard')
    await page.goto(`${BASE_URL}/#/admin/elections`)
  })

  test('elections list displays data', async ({ page }) => {
    await expect(page.getByText('选举管理')).toBeVisible()
    await expect(page.locator('table')).toBeVisible()
    await expect(page.getByRole('button', { name: '创建选举' })).toBeVisible()
  })

  test('create dialog has validation', async ({ page }) => {
    await page.getByRole('button', { name: '创建选举' }).click()
    await expect(page.getByRole('dialog')).toBeVisible()
    await page.getByRole('button', { name: '确定' }).click()
    await expect(page.getByText('请输入选举标题')).toBeVisible()
  })

  test('search filters elections', async ({ page }) => {
    await page.getByPlaceholder('搜索选举标题').fill('2026')
    await page.getByRole('button', { name: '搜索' }).click()
    const rows = page.locator('table tbody tr')
    const count = await rows.count()
    expect(count).toBeGreaterThan(0)
  })
})
