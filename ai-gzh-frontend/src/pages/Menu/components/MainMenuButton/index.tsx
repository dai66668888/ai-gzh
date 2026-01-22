import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Form } from 'antd';
import React from 'react';
import MenuButtonForm from '../MenuButtonForm';
import SubMenuButton from '../SubMenuButton';
import styles from '../index.less';

interface MainMenuButtonProps {
  field: any;
  index: number;
  menuTypes: API.WxMenuButtonTypeEnum[];
  form: any;
  onRemove: (index: number) => void;
  onAddSubButton: (index: number) => void;
  onRemoveSubButton: (mainIndex: number, subIndex: number) => void;
}

/**
 * 一级菜单按钮组件
 */
const MainMenuButton: React.FC<MainMenuButtonProps> = ({
  field,
  index,
  menuTypes,
  form,
  onRemove,
  onAddSubButton,
  onRemoveSubButton,
}) => {
  return (
    <div className={styles.mainMenu}>
      <div className={styles.mainMenuHeader}>
        <span>一级菜单 {index + 1}</span>
        <Button type="text" danger icon={<DeleteOutlined />} onClick={() => onRemove(index)} />
      </div>

      {/* 一级菜单表单项 */}
      <div className={styles.mainMenuContent}>
        <MenuButtonForm field={field} menuTypes={menuTypes} form={form} />
      </div>

      {/* 二级菜单列表 */}
      <Form.List name={[field.name, 'subButtons']}>
        {(subFields) => (
          <div className={styles.subMenuList}>
            {subFields.map((subField, subIndex) => (
              <SubMenuButton
                key={subField.key}
                field={subField}
                index={subIndex}
                mainIndex={index}
                menuTypes={menuTypes}
                form={form}
                onRemove={onRemoveSubButton}
              />
            ))}

            {/* 添加二级菜单按钮 */}
            {subFields.length < 5 && (
              <Button
                type="dashed"
                onClick={() => onAddSubButton(index)}
                icon={<PlusOutlined />}
                className={styles.addSubButton}
              >
                添加二级菜单
              </Button>
            )}
          </div>
        )}
      </Form.List>
    </div>
  );
};

export default MainMenuButton;
