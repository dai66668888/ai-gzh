import { CONTENT_TO_MATERIAL_TYPE, CONTENT_TYPE, REPLY_TYPE } from '@/constants/reply';
import { listAllMaterialUsingGET } from '@/services/backend/wxMaterialController';
import {
  addWxReplyRuleUsingPOST,
  getWxMpReplyRuleVOByIdUsingGET,
  updateWxReplyRuleUsingPOST,
} from '@/services/backend/wxReplyRuleController';
import { useModel } from '@umijs/max';
import { Form, message } from 'antd';
import { useCallback, useEffect, useState } from 'react';

/**
 * 自动回复表单 Hook
 */
export default function useReplyForm(props: {
  visible: boolean;
  onSuccess: () => void;
  ruleId?: number;
}) {
  const { visible, onSuccess, ruleId } = props;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const { currentWxAccount } = useModel('myWxAccount');
  // 使用 autoReply 模型中的枚举数据
  const { replyTypeList, matchTypeList, contentTypeList, initEnumData } = useModel('autoReply');

  // 选中的类型
  const [selectedReplyType, setSelectedReplyType] = useState<number>(REPLY_TYPE.KEYWORD);
  const [selectedContentType, setSelectedContentType] = useState<number>(CONTENT_TYPE.TEXT);

  // 素材相关状态
  const [materialList, setMaterialList] = useState<API.WxMaterialFileBatchGetNewsItem[]>([]);
  const [loadingMaterial, setLoadingMaterial] = useState(false);
  const [totalCount, setTotalCount] = useState<number>(0);

  /**
   * 获取素材列表
   */
  const fetchMaterialList = useCallback(
    async (contentType: number, page: number = 1, append: boolean = false) => {
      if (!currentWxAccount?.appId) {
        message.warning('请先选择公众号');
        return false;
      }

      // 获取对应的素材类型
      const materialType = CONTENT_TO_MATERIAL_TYPE[contentType];
      if (!materialType) return false;

      setLoadingMaterial(true);
      try {
        const res = await listAllMaterialUsingGET({
          appId: currentWxAccount.appId,
          materialType,
          pageSize: 20, // 每页加载 20 条
          current: page,
        });

        if (res.code === 0 && res.data) {
          // 更新总数
          setTotalCount(res.data.totalCount || 0);

          if (append && page > 1) {
            // 追加模式，合并新数据
            setMaterialList((prevList) => [...prevList, ...(res.data?.items || [])]);
          } else {
            // 替换模式，直接设置新数据
            setMaterialList(res.data.items || []);
          }
          return true;
        } else {
          if (!append) {
            setMaterialList([]);
            setTotalCount(0);
          }
          return false;
        }
      } catch (error) {
        console.error('获取素材列表失败：', error);
        message.error('获取素材列表失败');
        if (!append) {
          setMaterialList([]);
          setTotalCount(0);
        }
        return false;
      } finally {
        setLoadingMaterial(false);
      }
    },
    [currentWxAccount],
  );

  /**
   * 加载更多素材
   */
  const loadMoreMaterial = useCallback(
    async (page: number) => {
      return await fetchMaterialList(selectedContentType, page, true);
    },
    [fetchMaterialList, selectedContentType],
  );

  /**
   * 获取规则详情
   */
  const fetchRuleDetail = useCallback(
    async (id: number) => {
      setLoading(true);
      try {
        const res = await getWxMpReplyRuleVOByIdUsingGET({ id });
        if (res.code === 0 && res.data) {
          const ruleData = res.data;
          setSelectedReplyType(ruleData.replyType || REPLY_TYPE.KEYWORD);

          if (ruleData.replyContent?.contentType !== undefined) {
            setSelectedContentType(ruleData.replyContent.contentType);

            // 如果是媒体类型，加载对应的素材列表
            if (
              ruleData.replyContent.contentType === CONTENT_TYPE.IMAGE ||
              ruleData.replyContent.contentType === CONTENT_TYPE.VOICE ||
              ruleData.replyContent.contentType === CONTENT_TYPE.VIDEO
            ) {
              await fetchMaterialList(ruleData.replyContent.contentType);
            }
          }

          // 将后端数据转换为表单数据格式
          form.setFieldsValue({
            ruleName: ruleData.ruleName,
            ruleDescription: ruleData.ruleDescription,
            replyType: ruleData.replyType,
            eventKey: ruleData.matchValue?.[0]?.matchKeyWords,
            matchValue: ruleData.matchValue || [],
            replyContent: {
              contentType: ruleData.replyContent?.contentType,
              textContent: ruleData.replyContent?.textContent,
              mediaId: ruleData.replyContent?.mediaId,
              articleId: ruleData.replyContent?.articleId,
            },
          });

          return true;
        }
        return false;
      } catch (error) {
        console.error('获取规则详情失败：', error);
        message.error('获取规则详情失败');
        return false;
      } finally {
        setLoading(false);
      }
    },
    [form, fetchMaterialList],
  );

  /**
   * 提交表单
   */
  const handleSubmit = useCallback(async () => {
    try {
      const values = await form.validateFields();

      if (!currentWxAccount?.appId) {
        message.error('请先选择公众号');
        return false;
      }

      setLoading(true);

      // 构建请求数据
      const requestData = {
        ...values,
        appId: currentWxAccount.appId,
      };

      let success = false;

      if (ruleId) {
        // 编辑模式
        const res = await updateWxReplyRuleUsingPOST({
          ...requestData,
          id: ruleId,
        });
        success = res.code === 0 && !!res.data;
      } else {
        // 添加模式
        const res = await addWxReplyRuleUsingPOST(requestData);
        success = res.code === 0 && !!res.data;
      }

      if (success) {
        message.success(`${ruleId ? '更新' : '添加'}规则成功`);
        form.resetFields();
        onSuccess();
        return true;
      } else {
        message.error(`${ruleId ? '更新' : '添加'}规则失败`);
        return false;
      }
    } catch (error) {
      console.error(`${ruleId ? '更新' : '添加'}规则失败:`, error);
      return false;
    } finally {
      setLoading(false);
    }
  }, [currentWxAccount, form, onSuccess, ruleId]);

  /**
   * 处理回复类型变化
   */
  const handleReplyTypeChange = useCallback(
    (value: number) => {
      setSelectedReplyType(value);

      // 清空相关字段
      if (value === REPLY_TYPE.KEYWORD) {
        // 关键词触发，清空事件 Key
        form.setFieldValue('eventKey', undefined);
      } else if (value === REPLY_TYPE.MENU_CLICK) {
        // 菜单点击事件，清空匹配值
        form.setFieldValue('matchValue', []);
      } else {
        // 其他类型，清空匹配值和事件 Key
        form.setFieldValue('matchValue', []);
        form.setFieldValue('eventKey', undefined);
      }
    },
    [form],
  );

  /**
   * 处理内容类型变化
   */
  const handleContentTypeChange = useCallback(
    async (value: number) => {
      setSelectedContentType(value);

      // 清空相关字段
      if (value === CONTENT_TYPE.TEXT) {
        // 文本类型，清空其他字段
        form.setFieldValue(['replyContent', 'mediaId'], undefined);
        form.setFieldValue(['replyContent', 'articleId'], undefined);
      } else if (
        value === CONTENT_TYPE.IMAGE ||
        value === CONTENT_TYPE.VOICE ||
        value === CONTENT_TYPE.VIDEO
      ) {
        // 图片、语音、视频类型，清空其他字段
        form.setFieldValue(['replyContent', 'textContent'], undefined);
        form.setFieldValue(['replyContent', 'articleId'], undefined);

        // 加载对应的素材列表
        await fetchMaterialList(value);
      } else if (value === CONTENT_TYPE.NEWS) {
        // 图文类型，清空其他字段
        form.setFieldValue(['replyContent', 'textContent'], undefined);
        form.setFieldValue(['replyContent', 'mediaId'], undefined);
      }
    },
    [form, fetchMaterialList],
  );

  // 初始化枚举数据
  useEffect(() => {
    initEnumData();
  }, [initEnumData]);

  // 如果是编辑模式，获取规则详情
  useEffect(() => {
    if (visible && ruleId) {
      fetchRuleDetail(ruleId);
    }
  }, [visible, ruleId, fetchRuleDetail]);

  // 重置素材列表
  useEffect(() => {
    if (!visible) {
      setMaterialList([]);
      setTotalCount(0);
    }
  }, [visible]);

  return {
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
  };
}
