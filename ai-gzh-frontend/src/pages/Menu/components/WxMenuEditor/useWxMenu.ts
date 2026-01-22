import {
  deleteMenuUsingPOST,
  getMenuTypeUsingGET,
  getMenuUsingGET,
  updateMenuUsingPOST,
} from '@/services/backend/wxMenuController';
import { message } from 'antd';
import { useCallback, useState } from 'react';

/**
 * 微信公众号菜单管理 Hook
 * 提供菜单数据获取、更新、删除等功能
 */
const useWxMenu = () => {
  // 菜单数据
  const [menuData, setMenuData] = useState<API.WxMenuDTO | undefined>(undefined);
  // 菜单类型列表
  const [menuTypes, setMenuTypes] = useState<API.WxMenuButtonTypeEnum[]>([]);
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  // 保存状态
  const [saving, setSaving] = useState<boolean>(false);

  /**
   * 获取菜单数据
   * @param appId 公众号AppID
   */
  const fetchMenuData = useCallback(async (appId?: string) => {
    if (!appId) {
      message.warning('请先选择公众号');
      return;
    }

    setLoading(true);
    try {
      const res = await getMenuUsingGET({ appId });
      if (res.code === 0) {
        setMenuData(res.data);
      } else {
        message.error(res.message || '获取菜单失败');
      }
    } catch (error: any) {
      console.error('获取菜单失败:', error);
      message.error('获取菜单失败: ' + (error.message || '未知错误'));
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * 获取菜单类型列表
   */
  const fetchMenuTypes = useCallback(async () => {
    try {
      const res = await getMenuTypeUsingGET();
      if (res.code === 0 && res.data) {
        setMenuTypes(res.data);
      }
    } catch (error) {
      console.error('获取菜单类型失败:', error);
    }
  }, []);

  /**
   * 更新菜单
   * @param appId 公众号AppID
   * @param buttons 菜单按钮数据
   */
  const updateMenu = useCallback(
    async (appId: string, buttons: API.WxMenuButtonDTO[]) => {
      const safeButtons = buttons.map((button) => ({
        ...button,
        type: button.type || 'click',
      }));
      if (!appId) {
        message.warning('请先选择公众号');
        return false;
      }

      setSaving(true);
      try {
        const res = await updateMenuUsingPOST({ appId }, safeButtons);
        if (res.code === 0 && res.data) {
          message.success('菜单更新成功');
          // 更新成功后重新获取菜单数据
          setTimeout(async () => {
            await fetchMenuData(appId);
          }, 1000);
          return true;
        } else {
          message.error(res.message || '菜单更新失败');
          return false;
        }
      } catch (error: any) {
        console.error('菜单更新失败:', error);
        message.error('菜单更新失败: ' + (error.message || '未知错误'));
        return false;
      } finally {
        setSaving(false);
      }
    },
    [fetchMenuData],
  );

  /**
   * 删除全部菜单
   * @param appId 公众号AppID
   */
  const deleteAllMenu = useCallback(async (appId: string) => {
    if (!appId) {
      message.warning('请先选择公众号');
      return false;
    }

    setLoading(true);
    try {
      const res = await deleteMenuUsingPOST({ appId });
      if (res.code === 0 && res.data) {
        message.success('菜单删除成功');
        // 删除成功后清空菜单数据
        setMenuData(undefined);
        return true;
      } else {
        message.error(res.message || '菜单删除失败');
        return false;
      }
    } catch (error: any) {
      console.error('菜单删除失败:', error);
      message.error('菜单删除失败: ' + (error.message || '未知错误'));
      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    menuData,
    menuTypes,
    loading,
    saving,
    fetchMenuData,
    fetchMenuTypes,
    updateMenu,
    deleteAllMenu,
  };
};

export default useWxMenu;
