import BaseLayout from '@/components/BaseLayout';
import WxAccountGuide from '@/components/WxAccountGuide';
import React from 'react';

/**
 * 公众号管理页面
 */
const WxAccountPage: React.FC = () => {
  return (
    <BaseLayout title="公众号管理">
      <WxAccountGuide />
    </BaseLayout>
  );
};

export default WxAccountPage;
