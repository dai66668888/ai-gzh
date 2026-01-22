import {
  getMaterialTypeUsingGET,
  listAllMaterialUsingGET,
} from '@/services/backend/wxMaterialController';
import { useModel } from '@umijs/max';
import { message } from 'antd';
import { useCallback, useState } from 'react';

type typeFetchMaterialList = (params?: {
  current?: number;
  pageSize?: number;
  materialType?: string;
  AccountId?: string;
}) => Promise<boolean>;

/**
 * 微信素材数据流模型
 * 管理素材类型和素材列表
 */
export default () => {
  // 素材类型列表
  const [materialTypes, setMaterialTypes] = useState<API.WxMaterialTypeEnum[]>([]);
  // 素材列表
  const [materialList, setMaterialList] = useState<API.WxMaterialFileBatchGetNewsItem[]>([]);
  // 素材总数
  const [totalCount, setTotalCount] = useState<number>(0);
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  // 分页信息
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 18,
  });

  // 获取当前选中的公众号
  const { currentWxAccount } = useModel('myWxAccount');

  /**
   * 获取素材类型列表
   */
  const fetchMaterialTypes = async (
    currentMaterialType: string,
    setSearchParams: (params: any) => void,
  ) => {
    setLoading(true);
    try {
      const res = await getMaterialTypeUsingGET();
      if (res.code === 0 && res.data) {
        setMaterialTypes(res.data);
        // 如果有素材类型且当前未选中任何素材类型，则默认选中第一个
        if (res.data.length > 0 && !currentMaterialType) {
          setSearchParams({ tab: res.data[0].value || '' });
        }
      }
    } catch (error) {
      console.error('获取素材类型列表失败：', error);
      message.error('获取素材类型列表失败');
    } finally {
      setLoading(false);
    }
  };

  /**
   * 获取素材列表
   */
  const fetchMaterialList: typeFetchMaterialList = async (params?: {
    AccountId?: string;
    current?: number;
    pageSize?: number;
    materialType?: string;
  }) => {
    if (!(currentWxAccount?.appId || params?.AccountId)) {
      message.warning('请先选择公众号');
      return false;
    }

    const materialType = params?.materialType;
    if (!materialType) {
      message.warning('请先选择素材类型');
      return false;
    }

    setLoading(true);
    try {
      const res = await listAllMaterialUsingGET({
        appId: params?.AccountId || currentWxAccount?.appId || '',
        materialType,
        current: params?.current || pagination.current,
        pageSize: params?.pageSize || pagination.pageSize,
      });

      if (res.code === 0 && res.data) {
        setMaterialList(res.data.items || []);
        setTotalCount(res.data.totalCount || 0);

        // 更新分页信息
        setPagination({
          current: params?.current || pagination.current,
          pageSize: params?.pageSize || pagination.pageSize,
        });
      }
    } catch (error: any) {
      console.error('获取素材列表失败：', error);
      message.error('获取素材列表失败' + error.message);
    } finally {
      setLoading(false);
    }
    return true;
  };

  /**
   * 切换分页
   */
  const changePagination = useCallback(
    (current: number, pageSize: number, materialType: string) => {
      fetchMaterialList({ current, pageSize, materialType });
    },
    [],
  );
  return {
    materialTypes,
    materialList,
    totalCount,
    loading,
    pagination,
    fetchMaterialTypes,
    fetchMaterialList,
    changePagination,
  };
};
