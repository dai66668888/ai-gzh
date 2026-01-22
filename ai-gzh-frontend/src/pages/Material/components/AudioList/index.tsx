import { PauseCircleOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { useModel, useSearchParams } from '@umijs/max';
import { Button, Card, Empty, List, Space, Typography } from 'antd';
import React, { useEffect } from 'react';
import DeleteButton from '../DeleteButton';
import DownloadButton from '../DownloadButton';
import './index.less';
import useAudioPlay from './useAudioPlay';

/**
 * 音频列表组件
 */
const AudioList: React.FC = () => {
  const { currentWxAccount } = useModel('myWxAccount');
  // 从 model 中获取数据
  const { materialList, fetchMaterialList, totalCount, pagination, loading, changePagination } =
    useModel('myWxMaterial');
  const [searchParams] = useSearchParams();
  const currentMaterialType = searchParams.get('tab');

  // 使用自定义 hook 处理音频播放
  const { playingAudioId, playLoading, togglePlay, audioUrl, setPlayingAudioId } = useAudioPlay();
  useEffect(() => {
    return () => {
      setPlayingAudioId(null);
    };
  }, []);
  return (
    <div className="audio-list-container">
      {/* 音频列表 */}
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
            totalCount > 18
              ? {
                  pageSize: 18,
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
              <Card hoverable className="audio-card" bodyStyle={{ padding: '12px' }}>
                <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                  <div>
                    <Typography.Paragraph
                      ellipsis={{ rows: 1, tooltip: item.name }}
                      className="audio-name"
                    >
                      {item.name}
                    </Typography.Paragraph>
                    <Typography.Text type="secondary" className="audio-time">
                      {item.updateTime
                        ? new Date(item.updateTime).toLocaleDateString()
                        : '未知时间'}
                    </Typography.Text>
                  </div>

                  <div className="audio-controls">
                    {!(playingAudioId && item.mediaId === playingAudioId) && (
                      <Button
                        type="text"
                        shape="circle"
                        loading={playLoading === item.mediaId}
                        icon={
                          playingAudioId === item.mediaId ? (
                            <PauseCircleOutlined style={{ fontSize: '24px', color: '#1890ff' }} />
                          ) : (
                            <PlayCircleOutlined style={{ fontSize: '24px' }} />
                          )
                        }
                        onClick={() => togglePlay(item, currentWxAccount?.appId || '')}
                        className="play-button"
                      />
                    )}
                    <DownloadButton
                      className="download-button"
                      appId={currentWxAccount?.appId || ''}
                      materialId={item.mediaId || ''}
                      fileName={item.name?.endsWith('.mp3') ? item.name : `${item.name}.mp3`}
                      type="voice"
                    />
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
                  </div>
                </Space>
                {playingAudioId && item.mediaId === playingAudioId && (
                  <figure>
                    <figcaption></figcaption>
                    <audio className="audio-control" autoPlay controls src={audioUrl ?? ''}></audio>
                  </figure>
                )}
              </Card>
            </List.Item>
          )}
        />
      ) : (
        <Empty description="暂无音频" />
      )}
    </div>
  );
};

export default AudioList;
