import { useModel, useSearchParams } from '@umijs/max';
import { Card, Empty, Image, List, Typography } from 'antd';
import React from 'react';
import DeleteButton from '../DeleteButton';
import DownloadButton from '../DownloadButton';
import './index.less';

/**
 * 图片列表组件
 */
const ImageList: React.FC = () => {
  const { currentWxAccount } = useModel('myWxAccount');
  // 从 model 中获取数据
  const { materialList, fetchMaterialList, totalCount, pagination, loading, changePagination } =
    useModel('myWxMaterial');
  const [searchParams] = useSearchParams();
  const currentMaterialType = searchParams.get('tab');

  return (
    <div className="image-list-container">
      {/* 图片列表 */}
      {materialList.length > 0 ? (
        <List
          grid={{
            gutter: 16,
            xs: 1,
            sm: 2,
            md: 3,
            lg: 4,
            xl: 6,
            xxl: 6,
          }}
          loading={loading}
          pagination={{
            pageSize: 18,
            total: totalCount,
            current: pagination.current,
            onChange: (page, pageSize) => {
              changePagination(page, pageSize, currentMaterialType || '');
            },
          }}
          dataSource={materialList}
          renderItem={(item) => (
            <List.Item key={item.mediaId}>
              <Card
                hoverable
                className="image-card"
                bodyStyle={{ padding: 0, textAlign: 'center', marginTop: '12px' }}
                cover={
                  <div className="image-container">
                    <Image
                      src={item.url}
                      alt={item.name}
                      referrerPolicy="no-referrer"
                      style={{ objectFit: 'contain' }}
                      placeholder={true}
                      preview={false}
                    />
                    <DownloadButton
                      className="download-button"
                      fileName={item.name}
                      appId={currentWxAccount?.appId || ''}
                      materialId={item.mediaId || ''}
                    />
                    <DeleteButton
                      className="delete-button"
                      appId={currentWxAccount?.appId || ''}
                      materialId={item.mediaId || ''}
                      onSuccess={fetchMaterialList}
                    />
                  </div>
                }
              >
                <Card.Meta
                  title={
                    <Typography.Paragraph
                      ellipsis={{ rows: 1, tooltip: item.name }}
                      className="image-name"
                    >
                      {item.name}
                    </Typography.Paragraph>
                  }
                />
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暂无图片" />
      )}
    </div>
  );
};

export default ImageList;
