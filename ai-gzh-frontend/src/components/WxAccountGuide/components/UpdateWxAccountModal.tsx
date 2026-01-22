import { ACCOUNTS_FIELDS_CONFIG } from '@/constants/addWxAccount';
import { updateWxMpAccountUsingPOST } from '@/services/backend/wxAccountController';
import { ModalForm, ProFormText } from '@ant-design/pro-components';
import { message } from 'antd';
import React from 'react';

interface UpdateWxAccountModalProps {
  /**
   * 弹窗是否可见
   */
  visible: boolean;
  /**
   * 取消回调
   */
  onCancel: () => void;
  /**
   * 成功回调
   */
  onSuccess: () => void;
  /**
   * 要更新的公众号数据
   */
  values?: API.WxAccountVO;
}

/**
 * 更新公众号弹窗组件
 */
const UpdateWxAccountModal: React.FC<UpdateWxAccountModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  values,
}) => {
  /**
   * 处理更新公众号
   * @param formValues 表单值
   */
  const handleUpdate = async (formValues: Record<string, any>) => {
    if (!values?.id) {
      message.error('公众号ID不存在');
      return false;
    }

    const hide = message.loading('正在更新');
    try {
      const updateData: API.WxAccountUpdateDTO = {
        id: values.id,
        name: formValues.name,
        appId: formValues.appId,
        secret: formValues.secret,
        token: formValues.token,
        aesKey: formValues.aesKey,
      };

      const result = await updateWxMpAccountUsingPOST(updateData);
      hide();
      if (result.code === 0) {
        message.success('更新成功');
        onSuccess();
        return true;
      } else {
        message.error(result.message || '更新失败');
        return false;
      }
    } catch (error: any) {
      hide();
      message.error('更新失败: ' + error.message);
      return false;
    }
  };

  /**
   * 根据配置生成表单项
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
    <ModalForm
      title="更新公众号"
      open={visible}
      modalProps={{
        destroyOnClose: true,
        onCancel,
      }}
      initialValues={values}
      onFinish={async (formValues) => {
        return await handleUpdate(formValues);
      }}
    >
      {renderFormItems()}
    </ModalForm>
  );
};

export default UpdateWxAccountModal;
