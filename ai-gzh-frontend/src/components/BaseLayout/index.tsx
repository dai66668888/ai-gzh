import AddWxAccount from '@/components/AddWxAccount';
import ClearWxAccounts from '@/components/ClearWxAccounts';
import WxAccountSelector from '@/components/WxAccountSelector';
import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, Divider, Space } from 'antd';
import React, { useEffect } from 'react';

interface BaseLayoutProps {
  title: string;
  children: React.ReactNode;
}

/**
 * 基础布局组件
 */
const BaseLayout: React.FC<BaseLayoutProps> = ({ title, children }) => {
  const { fetchWxAccountList } = useModel('myWxAccount');
  const { initialState } = useModel('@@initialState');
  useEffect(() => {
    if (initialState?.currentUser?.id) {
      fetchWxAccountList();
    }
  }, [initialState?.currentUser?.id]);
  return (
    <PageContainer title={false}>
      <Card
        title={title}
        extra={
          <Space split={<Divider type="vertical" />}>
            <ClearWxAccounts
              onSuccess={() => {
                fetchWxAccountList();
              }}
            />
            <AddWxAccount
              onSuccess={() => {
                fetchWxAccountList();
              }}
            />
            <WxAccountSelector />
          </Space>
        }
      >
        {children}
      </Card>
    </PageContainer>
  );
};

export default BaseLayout;
