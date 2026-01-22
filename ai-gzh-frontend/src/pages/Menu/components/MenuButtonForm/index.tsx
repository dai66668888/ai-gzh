import { Form, Input, Select } from 'antd';
import React from 'react';

const { Option } = Select;

interface MenuButtonFormProps {
  field: any;
  isSubMenu?: boolean;
  menuTypes: API.WxMenuButtonTypeEnum[];
  form: any;
}

/**
 * 菜单按钮表单组件
 * 根据菜单类型渲染不同的表单项
 */
const MenuButtonForm: React.FC<MenuButtonFormProps> = ({
  field,
  isSubMenu = false,
  menuTypes,
  form,
}) => {
  const buttonType = form.getFieldValue(['buttons', field.name, 'type']);

  return (
    <>
      {/* 菜单名称 */}
      <Form.Item
        name={[field.name, 'name']}
        rules={[
          { required: true, message: '请输入菜单名称' },
          { max: isSubMenu ? 16 : 8, message: `菜单名称不能超过${isSubMenu ? 16 : 8}个字符` },
        ]}
      >
        <Input placeholder="菜单名称" />
      </Form.Item>

      {/* 菜单类型 */}
      <Form.Item
        name={[field.name, 'type']}
        rules={[{ required: true, message: '请选择菜单类型' }]}
      >
        <Select style={{ width: 120 }}>
          {menuTypes.map((type) => (
            <Option key={type.value} value={type.value}>
              {type.label}
            </Option>
          ))}
        </Select>
      </Form.Item>

      {/* 根据类型显示不同的表单项 */}
      {buttonType === 'click' && (
        <Form.Item
          name={[field.name, 'menuKey']}
          rules={[{ required: true, message: '请输入菜单KEY' }]}
        >
          <Input placeholder="菜单KEY" />
        </Form.Item>
      )}

      {/* 所有类型都添加 url 字段，但只有 view 和 miniprogram 类型是必填的 */}
      <Form.Item name={[field.name, 'url']}>
        <Input
          placeholder={
            buttonType === 'miniprogram'
              ? '备用链接地址'
              : buttonType === 'view'
              ? '链接地址'
              : '菜单KEY'
          }
        />
      </Form.Item>

      {buttonType === 'miniprogram' && (
        <Form.Item name={[field.name, 'pagePath']}>
          <Input placeholder="小程序页面路径" />
        </Form.Item>
      )}
    </>
  );
};

export default MenuButtonForm;
