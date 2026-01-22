import { PauseCircleOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { useModel, useSearchParams } from '@umijs/max';
import { Button, Card, Empty, List, Modal, Space, Typography } from 'antd';
import React from 'react';
import DeleteButton from '../DeleteButton';
import './index.less';
import useVideoPlay from './useVideoPlay';

/**
 * 视频列表组件
 */
const VideoList: React.FC = () => {
  const { currentWxAccount } = useModel('myWxAccount');
  // 从 model 中获取数据
  const { materialList, fetchMaterialList, totalCount, pagination, loading, changePagination } =
    useModel('myWxMaterial');
  const [searchParams] = useSearchParams();
  const currentMaterialType = searchParams.get('tab');

  // 使用自定义 hook 处理视频播放
  const { videoInfo, getVideoInfo, setVideoInfo } = useVideoPlay();

  return (
    <div className="video-list-container">
      {/* 视频列表 */}
      {materialList.length > 0 ? (
        <List
          grid={{
            gutter: 16,
            xs: 1,
            sm: 2,
            md: 3,
            lg: 3,
            xl: 3,
            xxl: 3,
          }}
          loading={loading}
          pagination={
            totalCount > 12
              ? {
                  pageSize: 12,
                  total: totalCount,
                  current: pagination.current,
                  onChange: (page, pageSize) => {
                    changePagination(page, pageSize, currentMaterialType || '');
                  },
                }
              : undefined
          }
          dataSource={materialList}
          renderItem={(item) => (
            <List.Item key={item.mediaId}>
              <Card hoverable className="video-card" bodyStyle={{ padding: '12px' }}>
                <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                  <div>
                    <Typography.Paragraph
                      ellipsis={{ rows: 1, tooltip: item.name }}
                      className="video-name"
                    >
                      {item.name}
                    </Typography.Paragraph>
                    <Typography.Text type="secondary" className="video-time">
                      {item.updateTime
                        ? new Date(item.updateTime).toLocaleDateString()
                        : '未知时间'}
                    </Typography.Text>
                  </div>
                  <Button
                    type="text"
                    shape="circle"
                    icon={
                      videoInfo?.id === item.mediaId ? (
                        <PauseCircleOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
                      ) : (
                        <PlayCircleOutlined style={{ fontSize: '24px' }} />
                      )
                    }
                    onClick={() => getVideoInfo(item, currentWxAccount?.appId || '')}
                    className="play-button"
                  />
                </Space>
                <DeleteButton
                  className="delete-button"
                  appId={currentWxAccount?.appId || ''}
                  materialId={item.mediaId || ''}
                  onSuccess={() =>
                    fetchMaterialList({
                      AccountId: currentWxAccount?.appId?.toString() || '',
                      materialType: currentMaterialType || '',
                    })
                  }
                />
                {/* 弹窗打开 iframe 渲染视频播放 */}
                <Modal
                  open={videoInfo?.id === item.mediaId}
                  onCancel={() => setVideoInfo(null)}
                  footer={null}
                  width={'80vw'}
                  bodyStyle={{ height: '80vh' }}
                  title={item.name}
                >
                  <iframe
                    src={videoInfo?.downUrl}
                    style={{ border: '1px solid #ccc' }}
                    width="100%"
                    height="100%"
                  />
                </Modal>
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暂无视频" />
      )}
    </div>
  );
};

export default VideoList;
