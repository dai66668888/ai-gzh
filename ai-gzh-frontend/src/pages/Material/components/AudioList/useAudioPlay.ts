import { downloadImgAndVoiceMaterialUsingGET } from '@/services/backend/wxMaterialController';
import { message } from 'antd';
import { useEffect, useRef, useState } from 'react';

// 定义响应类型接口
interface ResponseWithData {
  data: ArrayBuffer;
  response: {
    headers: {
      get: (name: string) => string | null;
    };
  };
}

/**
 * 音频播放自定义 Hook
 */
export const useAudioPlay = () => {
  // 当前播放的音频 ID
  const [playingAudioId, setPlayingAudioId] = useState<string | null>(null);
  // 音频元素引用
  const audioUrlRef = useRef<string | null>(null);
  // 加载状态
  const [playLoading, setPlayLoading] = useState<string | null>(null);

  // 播放或暂停音频
  const togglePlay = async (item: API.WxMaterialFileBatchGetNewsItem, appId: string) => {
    if (playingAudioId === item.mediaId) {
      // 如果当前已经在播放，则暂停

      setPlayingAudioId(null);
    } else {
      // 如果当前未播放，则开始播放
      if (!appId || !item.mediaId) {
        message.error('缺少必要参数，无法播放');
        return;
      }

      try {
        setPlayLoading(item.mediaId);
        // 使用 downloadImgAndVoiceMaterialUsingGET 获取音频数据
        const response = (await downloadImgAndVoiceMaterialUsingGET(
          {
            appId: appId,
            materialId: item.mediaId,
            fileName: item.name,
          },
          {
            getResponse: true,
            responseType: 'arraybuffer',
          },
        )) as unknown as ResponseWithData;

        // 创建 Blob 对象
        const blob = new Blob([response.data], { type: 'audio/mpeg' });
        // 创建 URL
        audioUrlRef.current = URL.createObjectURL(blob);

        setPlayingAudioId(item.mediaId);
      } catch (error) {
        console.error('获取音频失败：', error);
        message.error('获取音频失败，请稍后重试');
      } finally {
        setPlayLoading(null);
      }
    }
  };

  useEffect(() => {
    // 返回清理函数
    return () => {
      if (audioUrlRef.current) {
        URL.revokeObjectURL(audioUrlRef.current);
      }
    };
  }, []);

  return {
    playingAudioId,
    playLoading,
    togglePlay,
    setPlayingAudioId,
    audioUrl: audioUrlRef.current,
  };
};

export default useAudioPlay;
