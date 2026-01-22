import {
  getWxReplyContentTypeUsingGET,
  getWxReplyMatchTypeUsingGET,
  getWxReplyTypeUsingGET,
} from '@/services/backend/wxReplyRuleController';

import { useCallback, useState } from 'react';

/**
 * 自动回复配置数据
 */
export default () => {
  // 回复规则类型列表
  const [replyTypeList, setReplyTypeList] = useState<API.WxReplyRuleTypeEnum[]>([]);
  // 关键字匹配类型列表
  const [matchTypeList, setMatchTypeList] = useState<API.WxReplyMatchTypeEnum[]>([]);
  // 回复内容类型列表
  const [contentTypeList, setContentTypeList] = useState<API.WxReplyContentTypeEnum[]>([]);

  /**
   * 获取回复规则类型列表
   */
  const fetchReplyTypeList = useCallback(async () => {
    try {
      const res = await getWxReplyTypeUsingGET();
      if (res.code === 0 && res.data) {
        setReplyTypeList(res.data);
      }
    } catch (error) {
      console.error('获取回复规则类型列表失败：', error);
    }
  }, []);

  /**
   * 获取关键字匹配类型列表
   */
  const fetchMatchTypeList = useCallback(async () => {
    try {
      const res = await getWxReplyMatchTypeUsingGET();
      if (res.code === 0 && res.data) {
        setMatchTypeList(res.data);
      }
    } catch (error) {
      console.error('获取关键字匹配类型列表失败：', error);
    }
  }, []);

  /**
   * 获取回复内容类型列表
   */
  const fetchContentTypeList = useCallback(async () => {
    try {
      const res = await getWxReplyContentTypeUsingGET();
      if (res.code === 0 && res.data) {
        setContentTypeList(res.data);
      }
    } catch (error) {
      console.error('获取回复内容类型列表失败：', error);
    }
  }, []);

  /**
   * 初始化所有枚举数据
   */
  const initEnumData = useCallback(async () => {
    await Promise.all([fetchReplyTypeList(), fetchMatchTypeList(), fetchContentTypeList()]);
  }, [fetchReplyTypeList, fetchMatchTypeList, fetchContentTypeList]);

  return {
    replyTypeList,
    matchTypeList,
    contentTypeList,
    initEnumData,
  };
};
