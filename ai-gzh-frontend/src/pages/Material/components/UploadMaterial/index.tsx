import { uploadMaterialUsingPOST } from '@/services/backend/wxMaterialController';
import { PlusOutlined } from '@ant-design/icons';
import { Button, Modal, Upload, UploadFile, message } from 'antd';
import { type RcFile } from 'antd/es/upload';
import React, { useEffect, useState } from 'react';
import './index.less';
import { getAcceptFileTypes, getUploadTips, validateFile } from './utils';

interface UploadMaterialProps {
  appId: string; // 微信 appId
  currentMaterialType: string; // 当前素材类型
  onSuccess?: () => void; // 上传成功回调
  acceptFileTypes?: string; // 接受文件类型
}

/**
 * 素材上传组件
 */
const UploadMaterial: React.FC<UploadMaterialProps> = ({
  appId,
  currentMaterialType,
  onSuccess,
  acceptFileTypes = 'image/*',
}) => {
  // 控制上传弹窗的显示
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  // 上传文件列表
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  // 上传中状态
  const [uploading, setUploading] = useState<boolean>(false);

  // 打开上传弹窗
  const showModal = () => {
    setIsModalVisible(true);
  };

  // 关闭上传弹窗
  const handleCancel = () => {
    // 清理所有创建的 URL
    fileList.forEach((file) => {
      if (file.url?.startsWith('blob:')) {
        URL.revokeObjectURL(file.url);
      }
    });
    setIsModalVisible(false);
    setFileList([]);
  };

  // 上传前检查文件
  const beforeUpload = (file: RcFile) => {
    // 使用公共验证方法验证文件
    const isValid = validateFile(file, currentMaterialType, acceptFileTypes);
    if (isValid) {
      // 添加到文件列表，但不自动上传
      setFileList([file]);
      return;
    }
    // 此时列表中将不展示此文件，参考：https://ant.design/components/upload-cn#api
    return Upload.LIST_IGNORE;
  };

  // 处理文件列表变化
  const handleChange = ({ fileList }: { fileList: UploadFile[] }) => {
    // 释放旧的 URL 以防止内存泄漏
    fileList.forEach((file) => {
      if (file.originFileObj && !file.url) {
        file.url = URL.createObjectURL(file.originFileObj);
      }
    });
    setFileList(fileList);
  };

  // 组件卸载时清理所有创建的 URL
  useEffect(() => {
    return () => {
      // 组件卸载时清理
      fileList.forEach((file) => {
        if (file.url?.startsWith('blob:')) {
          URL.revokeObjectURL(file.url);
        }
      });
    };
  }, []);

  // 监听文件列表变化，清理不再需要的 URL
  useEffect(() => {
    const currentUrls = new Set(fileList.map((file) => file.url));

    // 在文件列表更新时，比较前后状态，清理被移除文件的 URL
    return () => {
      fileList.forEach((file) => {
        if (file.url && file.url.startsWith('blob:') && !currentUrls.has(file.url)) {
          URL.revokeObjectURL(file.url);
        }
      });
    };
  }, [fileList]);

  // 执行上传操作
  const handleUpload = async () => {
    if (fileList.length === 0) {
      message.warning('请先选择要上传的文件');
      return;
    }
    const file = fileList[0].originFileObj as File;
    setUploading(true);
    try {
      const response = await uploadMaterialUsingPOST(
        { appId, materialType: currentMaterialType },
        {}, // 可以在这里传递额外的参数
        file,
      );
      if (response.code === 0 && response.data) {
        message.success('上传成功');
        setFileList([]);
        setIsModalVisible(false);
        // 调用成功回调
        onSuccess?.();
      } else {
        message.error(response.message || '上传失败');
      }
    } catch (error) {
      console.error('上传出错：', error);
      message.error('上传失败，请稍后重试');
    } finally {
      setUploading(false);
    }
  };

  /**
   * 弹窗底部按钮
   */
  const modalFooter = [
    <Button key="cancel" onClick={handleCancel}>
      取消
    </Button>,
    <Button
      key="upload"
      type="primary"
      loading={uploading}
      onClick={handleUpload}
      disabled={fileList.length === 0}
    >
      上传
    </Button>,
  ];
  return (
    <>
      <Button type="primary" icon={<PlusOutlined />} onClick={showModal}>
        上传素材
      </Button>

      <Modal title={'上传素材'} open={isModalVisible} onCancel={handleCancel} footer={modalFooter}>
        <div className="uploadContainer">
          <Upload
            listType="picture-card"
            fileList={fileList}
            beforeUpload={beforeUpload}
            onChange={handleChange}
            maxCount={1}
            accept={getAcceptFileTypes(currentMaterialType, acceptFileTypes)}
          >
            {fileList.length === 0 && (
              <div>
                <PlusOutlined />
                <div style={{ marginTop: 8 }}>选择文件</div>
              </div>
            )}
          </Upload>
          <div className="upload-tips">
            <p>{getUploadTips(acceptFileTypes, currentMaterialType)}</p>
          </div>
        </div>
      </Modal>
    </>
  );
};

export default UploadMaterial;
