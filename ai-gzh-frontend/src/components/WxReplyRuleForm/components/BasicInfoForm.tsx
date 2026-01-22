import { REPLY_TYPE } from '@/constants/reply';
import { Form, Input, Select } from 'antd';
import React from 'react';

const { Option } = Select;
const { TextArea } = Input;

interface BasicInfoFormProps {
  replyTypeList: API.WxReplyRuleTypeEnum[];
  selectedReplyType: number;
  onReplyTypeChange: (value: number) => void;
}

/**
 * 基本信息表单组件
 */
const BasicInfoForm: React.FC<BasicInfoFormProps> = ({
  replyTypeList,
  selectedReplyType,
  onReplyTypeChange,
}) => {
  return (
    <>
      <Form.Item
        name="ruleName"
        label="规则名称"
        rules={[{ required: true, message: '请输入规则名称' }]}
      >
        <Input placeholder="请输入规则名称" />
      </Form.Item>

      <Form.Item name="ruleDescription" label="规则描述">
        <TextArea placeholder="请输入规则描述" rows={2} />
      </Form.Item>

      <Form.Item
        name="replyType"
        label="触发类型"
        rules={[{ required: true, message: '请选择触发类型' }]}
      >
        <Select placeholder="请选择触发类型" onChange={onReplyTypeChange}>
          {replyTypeList.map((type) => (
            <Option key={type.value} value={type.value}>
              {type.label}
            </Option>
          ))}
        </Select>
      </Form.Item>

      {/* 菜单点击事件类型时显示事件 Key 输入 */}
      {selectedReplyType === REPLY_TYPE.MENU_CLICK && (
        <Form.Item
          name="eventKey"
          label="菜单事件Key"
          rules={[{ required: true, message: '请输入菜单事件Key' }]}
        >
          <Input placeholder="请输入菜单事件Key" />
        </Form.Item>
      )}
    </>
  );
};

export default BasicInfoForm;
