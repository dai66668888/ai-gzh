import { deleteMaterialUsingPOST } from '@/services/backend/wxMaterialController';
import { DeleteOutlined } from '@ant-design/icons';
import { Button, Modal, message } from 'antd';
import React, { useState } from 'react';

interface DeleteButtonProps {
  appId: string;
  materialId: string;
  onSuccess?: () => void;
  buttonText?: string;
  showIcon?: boolean;
  className?: string;
  style?: React.CSSProperties;
}

/**
 * 素材删除按钮组件
 */
const DeleteButton: React.FC<DeleteButtonProps> = ({
  appId,
  materialId,
  onSuccess,
  buttonText = '',
  showIcon = true,
  className,
  style,
}) => {
  const [loading, setLoading] = useState<boolean>(false);

  // 处理删除
  const handleDelete = async (e: React.MouseEvent) => {
    // 阻止事件冒泡，避免触发卡片的点击事件
    e.stopPropagation();

    if (!materialId || !appId) {
      message.error('缺少必要参数，无法删除');
      return;
    }

    // 显示确认对话框
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除该素材吗？删除后将无法恢复。',
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        setLoading(true);

        try {
          const response = await deleteMaterialUsingPOST({ appId }, { materialId });

          if (response.code === 0 && response.data) {
            message.success('删除成功');
            // 调用成功回调
            onSuccess?.();
          } else {
            message.error(response.message || '删除失败');
          }
        } catch (error) {
          console.error('删除出错:', error);
          message.error('删除失败，请稍后重试');
        } finally {
          setLoading(false);
        }
      },
    });
  };

  return (
    <Button
      shape="circle"
      type="primary"
      danger
      size="small"
      icon={showIcon ? <DeleteOutlined /> : null}
      loading={loading}
      onClick={handleDelete}
      className={className}
      style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.15)', ...style }}
    >
      {buttonText}
    </Button>
  );
};

export default DeleteButton;
