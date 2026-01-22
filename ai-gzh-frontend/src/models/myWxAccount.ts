import {
  deleteWxMpAccountByIdsUsingPOST,
  listWxMpAccountByPageUsingGET,
} from '@/services/backend/wxAccountController';
import { message } from 'antd';
import { useCallback, useState } from 'react';

/**
 * 公众号数据流模型
 * 管理公众号列表和当前选中的公众号
 */
export default () => {
  // 公众号列表
  const [wxAccountList, setWxAccountList] = useState<API.WxAccountVO[]>([]);
  // 当前选中的公众号
  const [currentWxAccount, setCurrentWxAccount] = useState<API.WxAccountVO | undefined>(undefined);
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);

  /**
   * 获取公众号列表
   */
  const fetchWxAccountList = useCallback(async () => {
    setLoading(true);
    try {
      const res = await listWxMpAccountByPageUsingGET({
        pageSize: 100, // 设置较大的页面大小，确保能获取所有公众号
        current: 1,
      });
      if (res.code === 0 && res.data) {
        const accountList = res.data.records || [];
        setWxAccountList(accountList);

        // 如果有公众号且当前未选中任何公众号，则默认选中第一个
        if (accountList.length > 0 && !currentWxAccount) {
          setCurrentWxAccount(accountList[0]);
        }
      }
    } catch (error) {
      console.error('获取公众号列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [currentWxAccount]);

  /**
   * 清空所有公众号
   * @returns 是否清空成功
   */
  const clearAllWxAccounts = useCallback(async (): Promise<boolean> => {
    if (wxAccountList.length === 0) {
      message.info('当前没有公众号可清空');
      return false;
    }

    setLoading(true);
    try {
      // 提取所有公众号的appId
      const appIds = wxAccountList.map((account) => account.appId as string).filter(Boolean);

      if (appIds.length === 0) {
        message.error('没有有效的公众号AppID');
        return false;
      }

      // 调用删除接口
      const result = await deleteWxMpAccountByIdsUsingPOST(appIds);

      if (result.code === 0 && result.data) {
        // 清空本地状态
        setWxAccountList([]);
        setCurrentWxAccount(undefined);
        message.success('已清空所有公众号');
        return true;
      } else {
        message.error(result.message || '清空公众号失败');
        return false;
      }
    } catch (error: any) {
      console.error('清空公众号失败:', error);
      message.error('清空公众号失败: ' + (error.message || '未知错误'));
      return false;
    } finally {
      setLoading(false);
    }
  }, [wxAccountList]);

  return {
    wxAccountList,
    currentWxAccount,
    loading,
    fetchWxAccountList,
    setCurrentWxAccount,
    clearAllWxAccounts,
  };
};
