import BaseLayout from '@/components/BaseLayout';
import { useModel } from '@umijs/max';
import { Empty } from 'antd';
import React from 'react';
import WxMenuEditor from './components/WxMenuEditor';

/**
 * 菜单管理页面
 */
const MenuPage: React.FC = () => {
  const { wxAccountList, currentWxAccount } = useModel('myWxAccount');

  return (
    <BaseLayout title="菜单管理">
      {wxAccountList.length === 0 ? (
        <Empty description="暂无公众号，请先添加公众号" />
      ) : (
        <WxMenuEditor
          appId={currentWxAccount?.appId}
          onSuccess={() => {
            // 菜单操作成功后的回调
          }}
        />
      )}
    </BaseLayout>
  );
};

export default MenuPage;
