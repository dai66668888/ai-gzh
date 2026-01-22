import { REPLY_TYPE } from '@/constants/reply';
import { Button, Form, Modal } from 'antd';
import React from 'react';
import BasicInfoForm from './components/BasicInfoForm';
import ContentTypeForm from './components/ContentTypeForm';
import KeywordMatchForm from './components/KeywordMatchForm';
import useReplyForm from './useReplyForm';

interface WxReplyRuleFormProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  ruleId?: number; // 如果有ID，则为编辑模式
}

/**
 * 自动回复规则表单组件
 */
const WxReplyRuleForm: React.FC<WxReplyRuleFormProps> = ({
  visible,
  onCancel,
  onSuccess,
  ruleId,
}) => {
  // 使用自定义 hook 处理表单逻辑
  const {
    form,
    loading,
    replyTypeList,
    matchTypeList,
    contentTypeList,
    selectedReplyType,
    selectedContentType,
    materialList,
    loadingMaterial,
    totalCount,
    handleSubmit,
    handleReplyTypeChange,
    handleContentTypeChange,
    loadMoreMaterial,
  } = useReplyForm({ visible, onSuccess, ruleId });

  return (
    <Modal
      title={`${ruleId ? '编辑' : '添加'}自动回复规则`}
      open={visible}
      onCancel={() => {
        onCancel();
        form.resetFields();
      }}
      footer={[
        <Button key="cancel" onClick={onCancel}>
          取消
        </Button>,
        <Button key="submit" type="primary" loading={loading} onClick={handleSubmit}>
          确定
        </Button>,
      ]}
      width={700}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          replyType: REPLY_TYPE.KEYWORD, // 默认为关键词触发
          matchValue: [{ matchType: 1 }], // 默认添加一个匹配项
          replyContent: { contentType: 0 }, // 默认为文本回复
        }}
      >
        {/* 基本信息表单 */}
        <BasicInfoForm
          replyTypeList={replyTypeList}
          selectedReplyType={selectedReplyType}
          onReplyTypeChange={handleReplyTypeChange}
        />

        {/* 关键词匹配表单 */}
        {selectedReplyType === REPLY_TYPE.KEYWORD && (
          <KeywordMatchForm matchTypeList={matchTypeList} />
        )}

        {/* 回复内容表单 */}
        <ContentTypeForm
          contentTypeList={contentTypeList}
          selectedContentType={selectedContentType}
          materialList={materialList}
          loadingMaterial={loadingMaterial}
          onContentTypeChange={handleContentTypeChange}
          onLoadMore={loadMoreMaterial}
          totalCount={totalCount}
        />
      </Form>
    </Modal>
  );
};

export default WxReplyRuleForm;
