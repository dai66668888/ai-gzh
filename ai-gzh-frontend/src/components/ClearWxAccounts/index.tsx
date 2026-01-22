import { DeleteOutlined } from '@ant-design/icons';
import { useModel } from '@umijs/max';
import { Button, Modal } from 'antd';
import React from 'react';

interface ClearWxAccountsProps {
  /**
   * 清空成功后的回调函数
   */
  onSuccess?: () => void;
  /**
   * 按钮样式
   */
  style?: React.CSSProperties;
  /**
   * 按钮类名
   */
  className?: string;
}

/**
 * 清空公众号按钮组件
 * 点击后会弹出确认对话框，确认后清空所有公众号
 */
const ClearWxAccounts: React.FC<ClearWxAccountsProps> = (props) => {
  const { onSuccess, style, className } = props;

  // 使用数据流
  const { clearAllWxAccounts, wxAccountList } = useModel('myWxAccount');

  /**
   * 处理清空公众号
   */
  const handleClear = () => {
    // 如果没有公众号，直接返回
    if (wxAccountList.length === 0) {
      Modal.info({
        title: '提示',
        content: '当前没有公众号可清空',
      });
      return;
    }

    // 显示确认对话框
    Modal.confirm({
      title: '确认清空',
      content: `确定要清空所有 ${wxAccountList.length} 个公众号吗？此操作不可恢复！`,
      okText: '确认清空',
      cancelText: '取消',
      okButtonProps: { danger: true },
      onOk: async () => {
        const success = await clearAllWxAccounts();
        if (success && onSuccess) {
          onSuccess();
        }
      },
    });
  };

  return (
    <Button
      type={'default'}
      danger={true}
      onClick={handleClear}
      icon={<DeleteOutlined />}
      style={style}
      className={className}
    >
      清空公众号
    </Button>
  );
};

export default ClearWxAccounts;
