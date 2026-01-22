import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Form, Input, Select, Space } from 'antd';
import React from 'react';

const { Option } = Select;

interface KeywordMatchFormProps {
  matchTypeList: API.WxReplyMatchTypeEnum[];
}

/**
 * 关键词匹配表单组件
 */
const KeywordMatchForm: React.FC<KeywordMatchFormProps> = ({ matchTypeList }) => {
  return (
    <Form.List name="matchValue">
      {(fields, { add, remove }) => (
        <>
          {fields.map((field) => (
            <Space key={field.key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
              <Form.Item
                {...field}
                name={[field.name, 'matchKeyWords']}
                rules={[{ required: true, message: '请输入关键词' }]}
              >
                <Input placeholder="请输入关键词" />
              </Form.Item>
              <Form.Item
                {...field}
                name={[field.name, 'matchType']}
                rules={[{ required: true, message: '请选择匹配方式' }]}
              >
                <Select style={{ width: 120 }}>
                  {matchTypeList.map((type) => (
                    <Option key={type.value} value={type.value}>
                      {type.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              {fields.length > 1 && <MinusCircleOutlined onClick={() => remove(field.name)} />}
            </Space>
          ))}
          <Form.Item>
            <Button type="dashed" onClick={() => add({ matchType: 1 })} icon={<PlusOutlined />}>
              添加关键词
            </Button>
          </Form.Item>
        </>
      )}
    </Form.List>
  );
};

export default KeywordMatchForm;
