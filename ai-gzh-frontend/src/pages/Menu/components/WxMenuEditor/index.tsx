import useWxMenu from '@/pages/Menu/components/WxMenuEditor/useWxMenu';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Form, Space, Tooltip, message } from 'antd';
import React, { useEffect, useState } from 'react';
import MainMenuButton from '../MainMenuButton';
import styles from '../index.less';

interface WxMenuEditorProps {
  appId?: string;
  onSuccess?: () => void;
}

/**
 * 微信公众号菜单编辑组件
 */
const WxMenuEditor: React.FC<WxMenuEditorProps> = ({ appId, onSuccess }) => {
  const { menuData, menuTypes, saving, fetchMenuData, fetchMenuTypes, updateMenu, deleteAllMenu } =
    useWxMenu();
  const [form] = Form.useForm();
  const [buttons, setButtons] = useState<API.WxMenuButtonDTO[]>([]);

  // 初始化获取菜单类型
  useEffect(() => {
    fetchMenuTypes();
  }, [fetchMenuTypes]);

  // 当 appId 变化时，获取菜单数据
  useEffect(() => {
    if (appId) {
      fetchMenuData(appId);
    }
  }, [appId, fetchMenuData]);

  // 当菜单数据变化时，更新表单
  useEffect(() => {
    if (menuData?.menu?.buttons) {
      setButtons(menuData.menu.buttons);
      form.setFieldsValue({ buttons: menuData.menu.buttons });
    } else {
      setButtons([]);
      form.setFieldsValue({ buttons: [] });
    }
  }, [menuData, form]);

  // 添加一级菜单
  const addMainButton = () => {
    if (buttons.length >= 3) {
      message.warning('一级菜单最多只能添加 3 个');
      return;
    }

    // 先获取当前表单的值，保留已填写的数据
    const currentValues = form.getFieldsValue();
    const currentButtons = currentValues.buttons || [];

    // 添加新菜单
    const newButtons = [
      ...currentButtons,
      { name: '新菜单', type: 'click', url: '', subButtons: [] },
    ];
    setButtons(newButtons);
    form.setFieldsValue({ buttons: newButtons });
  };

  // 添加二级菜单
  const addSubButton = (index: number) => {
    // 先获取当前表单的值，保留已填写的数据
    const currentValues = form.getFieldsValue();
    const currentButtons = currentValues.buttons || [];

    // 创建新的按钮数组，确保不直接修改原数组
    const newButtons = [...currentButtons];

    if (!newButtons[index].subButtons) {
      newButtons[index].subButtons = [];
    }

    if (newButtons[index].subButtons!.length >= 5) {
      message.warning('二级菜单最多只能添加 5 个');
      return;
    }

    newButtons[index].subButtons!.push({ name: '新子菜单', type: 'click', url: '' });
    setButtons(newButtons);
    form.setFieldsValue({ buttons: newButtons });
  };

  // 保存菜单
  const handleSave = async (data?: API.WxMenuButtonDTO[]) => {
    try {
      const values = await form.validateFields();
      if (appId) {
        const success = await updateMenu(appId, data || values.buttons);
        if (success && onSuccess) {
          onSuccess();
        }
      } else {
        message.warning('请先选择公众号');
      }
    } catch (error) {
      console.error('表单验证失败：', error);
    }
  };

  // 删除一级菜单
  const removeMainButton = async (index: number) => {
    // 先获取当前表单的值，保留已填写的数据
    const currentValues = form.getFieldsValue();
    const currentButtons = currentValues.buttons || [];

    // 创建新的按钮数组，确保不直接修改原数组
    const newButtons = [...currentButtons];
    newButtons.splice(index, 1);

    setButtons(newButtons);
    await handleSave(newButtons);
    form.setFieldsValue({ buttons: newButtons });
  };

  // 删除二级菜单
  const removeSubButton = async (mainIndex: number, subIndex: number) => {
    // 先获取当前表单的值，保留已填写的数据
    const currentValues = form.getFieldsValue();
    const currentButtons = currentValues.buttons || [];

    // 创建新的按钮数组，确保不直接修改原数组
    const newButtons = [...currentButtons];

    if (newButtons[mainIndex] && newButtons[mainIndex].subButtons) {
      newButtons[mainIndex].subButtons!.splice(subIndex, 1);
      await handleSave(newButtons);
    }
  };

  // 删除所有菜单
  const handleDelete = async () => {
    if (appId) {
      const success = await deleteAllMenu(appId);
      if (success && onSuccess) {
        onSuccess();
      }
    } else {
      message.warning('请先选择公众号');
    }
  };

  return (
    <>
      <Space style={{ width: '100%', marginBottom: 16, justifyContent: 'flex-end' }}>
        <Button danger onClick={handleDelete} disabled={saving}>
          删除全部菜单
        </Button>
        <Button type="primary" onClick={() => handleSave()} loading={saving}>
          保存菜单
        </Button>
      </Space>
      {!appId ? (
        <div style={{ textAlign: 'center', padding: '20px 0' }}>请先选择公众号</div>
      ) : (
        <Form
          style={{ maxWidth: '700px', margin: '0 auto' }}
          form={form}
          layout="vertical"
          // initialValues={{ buttons: [] }}
        >
          <Form.List name="buttons">
            {(fields) => (
              <div className={styles.menuContainer}>
                {fields.map((field, index) => (
                  <MainMenuButton
                    key={field.key}
                    field={field}
                    index={index}
                    menuTypes={menuTypes}
                    form={form}
                    onRemove={removeMainButton}
                    onAddSubButton={addSubButton}
                    onRemoveSubButton={removeSubButton}
                  />
                ))}

                {/* 添加一级菜单按钮 */}
                {fields.length < 3 && (
                  <Button
                    type="dashed"
                    onClick={addMainButton}
                    icon={<PlusOutlined />}
                    className={styles.addMainButton}
                  >
                    添加一级菜单
                  </Button>
                )}

                {/* 菜单说明 */}
                <div className={styles.menuTips}>
                  <Tooltip title="一级菜单最多3个，二级菜单最多5个。一级菜单名称不超过8个字，二级菜单名称不超过16个字。">
                    <QuestionCircleOutlined /> 菜单说明
                  </Tooltip>
                </div>
              </div>
            )}
          </Form.List>
        </Form>
      )}
    </>
  );
};

export default WxMenuEditor;
