import { CONTENT_TYPE, CONTENT_TYPE_NAME } from '@/constants/reply';
import { Form, Image, Input, Select, Spin } from 'antd';
import React, { useEffect, useState } from 'react';
import './ContentTypeForm.less';

const { Option } = Select;
const { TextArea } = Input;

interface ContentTypeFormProps {
  // 内容类型列表
  contentTypeList: API.WxReplyContentTypeEnum[];
  // 选中的内容类型
  selectedContentType: number;
  // 素材列表
  materialList: API.WxMaterialFileBatchGetNewsItem[];
  // 素材加载状态
  loadingMaterial: boolean;
  // 内容类型变化回调
  onContentTypeChange: (value: number) => void;
  // 加载更多回调
  onLoadMore?: (page: number) => Promise<boolean>;
  // 素材总数
  totalCount?: number;
}

/**
 * 回复内容类型表单组件
 */
const ContentTypeForm: React.FC<ContentTypeFormProps> = ({
  contentTypeList,
  selectedContentType,
  materialList,
  loadingMaterial,
  onContentTypeChange,
  onLoadMore,
  totalCount = 0,
}) => {
  // 获取素材类型名称
  const getMaterialTypeName = (contentType: number) => {
    return CONTENT_TYPE_NAME[contentType] || '素材';
  };

  // 当前页码
  const [currentPage, setCurrentPage] = useState<number>(1);
  // 是否有更多数据
  const hasMore = totalCount > materialList.length;

  // 处理滚动加载更多
  const handlePopupScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { target } = e;
    if (target) {
      const div = target as HTMLDivElement;
      // 当滚动到底部时，加载更多数据
      if (
        div.scrollHeight - div.scrollTop <= div.clientHeight + 50 &&
        !loadingMaterial &&
        hasMore
      ) {
        const nextPage = currentPage + 1;
        if (onLoadMore) {
          onLoadMore(nextPage).then((success) => {
            if (success) {
              setCurrentPage(nextPage);
            }
          });
        }
      }
    }
  };

  // 当内容类型变化时，重置页码
  useEffect(() => {
    setCurrentPage(1);
  }, [selectedContentType]);

  return (
    <div className="reply-content-section" style={{ marginBottom: 24 }}>
      <h3 style={{ marginBottom: 16 }}>回复内容设置</h3>

      <Form.Item
        name={['replyContent', 'contentType']}
        label="回复内容类型"
        rules={[{ required: true, message: '请选择回复内容类型' }]}
      >
        <Select placeholder="请选择回复内容类型" onChange={onContentTypeChange}>
          {contentTypeList.map((type) => (
            <Option key={type.value} value={type.value}>
              {type.label}
            </Option>
          ))}
        </Select>
      </Form.Item>

      {/* 根据内容类型显示不同的表单项 */}
      {selectedContentType === CONTENT_TYPE.TEXT && (
        <Form.Item
          name={['replyContent', 'textContent']}
          label="文本内容"
          rules={[{ required: true, message: '请输入文本内容' }]}
        >
          <TextArea placeholder="请输入文本内容" rows={4} />
        </Form.Item>
      )}

      {/* 图片、语音、视频类型 */}
      {(selectedContentType === CONTENT_TYPE.IMAGE ||
        selectedContentType === CONTENT_TYPE.VOICE ||
        selectedContentType === CONTENT_TYPE.VIDEO) && (
        <Form.Item
          name={['replyContent', 'mediaId']}
          label={`选择${getMaterialTypeName(selectedContentType)}`}
          rules={[{ required: true, message: `请选择${getMaterialTypeName(selectedContentType)}` }]}
        >
          <Select
            placeholder={`请选择${getMaterialTypeName(selectedContentType)}`}
            loading={loadingMaterial}
            showSearch
            optionFilterProp="children"
            notFoundContent={
              loadingMaterial ? (
                <Spin size="small" />
              ) : (
                `暂无${getMaterialTypeName(selectedContentType)}素材`
              )
            }
            dropdownClassName={
              selectedContentType === CONTENT_TYPE.IMAGE ? 'image-select-dropdown' : ''
            }
            optionLabelProp="label"
            onPopupScroll={handlePopupScroll}
            listHeight={400}
          >
            {materialList.map((item) =>
              selectedContentType === CONTENT_TYPE.IMAGE ? (
                <Option key={item.mediaId} value={item.mediaId} label={item.name || item.mediaId}>
                  <div className="image-option-item">
                    <Image
                      className="image-option-image"
                      src={item.url}
                      width={106}
                      height={106}
                      style={{ objectFit: 'contain', margin: '0 auto' }}
                      preview={false}
                      referrerPolicy="no-referrer"
                    />
                    <div className="image-option-name">{item.name || item.mediaId}</div>
                  </div>
                </Option>
              ) : (
                <Option key={item.mediaId} value={item.mediaId}>
                  {item.name || item.mediaId}
                </Option>
              ),
            )}
            {loadingMaterial && hasMore && (
              <Option disabled key="loading" className="loading-option">
                <Spin size="small" /> 加载中...
              </Option>
            )}
          </Select>
        </Form.Item>
      )}

      {/* 图文类型 */}
      {selectedContentType === CONTENT_TYPE.NEWS && (
        <Form.Item
          name={['replyContent', 'articleId']}
          label="图文消息ID"
          rules={[{ required: true, message: '请输入图文消息ID' }]}
        >
          <Input placeholder="请输入图文消息ID" />
        </Form.Item>
      )}
    </div>
  );
};

export default ContentTypeForm;
