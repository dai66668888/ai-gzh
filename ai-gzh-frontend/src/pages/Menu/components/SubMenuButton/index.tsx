import { DeleteOutlined } from '@ant-design/icons';
import { Button } from 'antd';
import React from 'react';
import MenuButtonForm from '../MenuButtonForm';
import styles from '../index.less';

interface SubMenuButtonProps {
  field: any;
  index: number;
  mainIndex: number;
  menuTypes: API.WxMenuButtonTypeEnum[];
  form: any;
  onRemove: (mainIndex: number, subIndex: number) => void;
}

/**
 * 二级菜单按钮组件
 */
const SubMenuButton: React.FC<SubMenuButtonProps> = ({
  field,
  index,
  mainIndex,
  menuTypes,
  form,
  onRemove,
}) => {
  return (
    <div className={styles.subMenu}>
      <div className={styles.subMenuHeader}>
        <span>二级菜单 {index + 1}</span>
        <Button
          type="text"
          danger
          icon={<DeleteOutlined />}
          onClick={() => onRemove(mainIndex, index)}
        />
      </div>

      {/* 二级菜单表单项 */}
      <div className={styles.subMenuContent}>
        <MenuButtonForm field={field} isSubMenu={true} menuTypes={menuTypes} form={form} />
      </div>
    </div>
  );
};

export default SubMenuButton;
