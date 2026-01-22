import { downloadImgAndVoiceMaterialUsingGET } from '@/services/backend/wxMaterialController';
import { downloadBinaryFile } from '@/utils';
import { DownloadOutlined } from '@ant-design/icons';
import { Button, message } from 'antd';
import React, { useState } from 'react';

interface DownloadButtonProps {
  appId: string;
  materialId: string;
  fileName?: string;
  buttonText?: string;
  showIcon?: boolean;
  className?: string;
  style?: React.CSSProperties;
  type?: 'image' | 'voice';
}

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
 * 素材下载按钮组件
 */
const DownloadButton: React.FC<DownloadButtonProps> = ({
  appId,
  materialId,
  fileName = 'wx',
  buttonText = '',
  showIcon = true,
  className,
  style,
  type = 'image',
}) => {
  const [loading, setLoading] = useState<boolean>(false);

  // 处理下载
  const handleDownload = async (e: React.MouseEvent) => {
    // 阻止事件冒泡，避免触发卡片的点击事件
    e.stopPropagation();

    if (!materialId || !appId) {
      message.error('缺少必要参数，无法下载');
      return;
    }

    setLoading(true);

    try {
      const response = (await downloadImgAndVoiceMaterialUsingGET(
        {
          appId,
          materialId,
          fileName: fileName,
        },
        {
          getResponse: true,
          responseType: 'arraybuffer',
        },
      )) as unknown as ResponseWithData;
      downloadBinaryFile(response.data, fileName, type);

      message.success('下载成功');
    } catch (error) {
      console.error('下载出错:', error);
      message.error('下载失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Button
      shape="circle"
      type="primary"
      size="small"
      icon={showIcon ? <DownloadOutlined /> : null}
      loading={loading}
      onClick={handleDownload}
      className={className}
      style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', ...style }}
    >
      {buttonText}
    </Button>
  );
};

export default DownloadButton;
