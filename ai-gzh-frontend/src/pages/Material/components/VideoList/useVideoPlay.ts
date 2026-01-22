import { getMaterialVideoByMaterialIdUsingGET } from '@/services/backend/wxMaterialController';
import { message } from 'antd';
import { useState } from 'react';

export interface VideoInfo extends API.WxMpMaterialVideoInfoResult {
  id: string;
}

/**
 * 视频播放自定义 Hook
 */
export const useVideoPlay = () => {
  // 视频信息
  const [videoInfo, setVideoInfo] = useState<VideoInfo | null>(null);

  // 播放或暂停视频
  const getVideoInfo = async (item: API.WxMaterialFileBatchGetNewsItem, appId: string) => {
    // 如果当前未播放，则开始播放
    if (!appId || !item.mediaId) {
      message.error('缺少必要参数，无法播放');
      return;
    }
    try {
      // 获取视频详情信息
      const videoInfoResponse = await getMaterialVideoByMaterialIdUsingGET({
        appId: appId,
        materialId: item.mediaId,
      });

      if (videoInfoResponse.code === 0 && videoInfoResponse.data) {
        setVideoInfo({
          ...videoInfoResponse.data,
          id: item.mediaId,
        });
      } else {
        message.error(videoInfoResponse.message || '获取视频信息失败');
      }
    } catch (error) {
      console.error('获取视频失败:', error);
      message.error('获取视频失败，请稍后重试');
    }
  };

  return {
    getVideoInfo,
    videoInfo,
    setVideoInfo,
  };
};

export default useVideoPlay;
