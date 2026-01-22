import { ACCOUNTS_FIELDS_CONFIG } from '@/constants/addWxAccount';
import { addWxMpAccountUsingPOST } from '@/services/backend/wxAccountController';
import { PlusOutlined } from '@ant-design/icons';
import { ModalForm, ProFormText } from '@ant-design/pro-components';
import { Button, message } from 'antd';
import React, { useState } from 'react';

interface AddWxAccountProps {
  /**
   * 新增成功后的回调函数
   */
  onSuccess?: () => void;
  /**
   * 按钮类型
   */
  buttonType?: 'default' | 'primary' | 'link' | 'text' | 'dashed';
  /**
   * 按钮文字
   */
  buttonText?: string;
}

/**
 * 新增公众号按钮组件
 * @param props 组件属性
 * @returns React 组件
 */
const AddWxAccount: React.FC<AddWxAccountProps> = (props) => {
  const { onSuccess, buttonType = 'primary', buttonText = '新增公众号' } = props;
  const [modalVisible, setModalVisible] = useState<boolean>(false);

  /**
   * 处理新增公众号
   * @param values 表单值
   */
  const handleAdd = async (values: API.WxAccountAddDTO) => {
    const hide = message.loading('正在添加');
    try {
      const result = await addWxMpAccountUsingPOST(values);
      hide();
      if (result.code === 0) {
        message.success('新增公众号成功');
        setModalVisible(false);
        onSuccess?.();
        return true;
      } else {
        message.error(result.message || '新增公众号失败');
        return false;
      }
    } catch (error: any) {
      hide();
      message.error('新增公众号失败，' + error.message);
      return false;
    }
  };

  /**
   * 根据配置直接渲染表单项
   */
  const renderFormItems = () => {
    return ACCOUNTS_FIELDS_CONFIG.map((field) => (
      <ProFormText
        key={field.name}
        name={field.name}
        label={field.placeholder}
        placeholder={`请输入${field.placeholder}`}
        rules={field.rules}
      />
    ));
  };

  return (
    <>
      <Button type={buttonType} onClick={() => setModalVisible(true)} icon={<PlusOutlined />}>
        {buttonText}
      </Button>
      <ModalForm
        title="新增公众号"
        open={modalVisible}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => setModalVisible(false),
        }}
        onFinish={async (values) => {
          return await handleAdd(values as API.WxAccountAddDTO);
        }}
      >
        {renderFormItems()}
      </ModalForm>
    </>
  );
};

export default AddWxAccount;
