// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 新增回复规则 POST /api/wx/reply/add */
export async function addWxReplyRuleUsingPOST(
  body: API.WxReplyRuleAddDTO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/wx/reply/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据id删除列表 POST /api/wx/reply/delete */
export async function deleteWxReplyRuleByIdsUsingPOST(
  body: number[],
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/wx/reply/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取回复内容类型 GET /api/wx/reply/get/content/type */
export async function getWxReplyContentTypeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayWxReplyContentTypeEnum_>('/api/wx/reply/get/content/type', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取关键字匹配类型 GET /api/wx/reply/get/match/type */
export async function getWxReplyMatchTypeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayWxReplyMatchTypeEnum_>('/api/wx/reply/get/match/type', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取回复规则类型 GET /api/wx/reply/get/reply/type */
export async function getWxReplyTypeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayWxReplyRuleTypeEnum_>('/api/wx/reply/get/reply/type', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 回复规则详情 GET /api/wx/reply/get/vo */
export async function getWxMpReplyRuleVOByIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getWxMpReplyRuleVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseWxReplyRuleVO_>('/api/wx/reply/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 分页查询回复规则 GET /api/wx/reply/page */
export async function listWxMpReplyRuleByPageUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listWxMpReplyRuleByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageWxReplyRuleVO_>('/api/wx/reply/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 更新回复规则 POST /api/wx/reply/update */
export async function updateWxReplyRuleUsingPOST(
  body: API.WxReplyRuleUpdateDTO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/wx/reply/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
