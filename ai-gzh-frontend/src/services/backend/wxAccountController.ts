// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 新增公众号 POST /api/wx/account/add */
export async function addWxMpAccountUsingPOST(
  body: API.WxAccountAddDTO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/wx/account/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据appId删除列表，这样后端处理更方便 POST /api/wx/account/delete */
export async function deleteWxMpAccountByIdsUsingPOST(
  body: string[],
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/wx/account/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 公众号详情 GET /api/wx/account/get/vo */
export async function getWxMpAccountVOByIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getWxMpAccountVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseWxAccountVO_>('/api/wx/account/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 分页查询公众号 GET /api/wx/account/page */
export async function listWxMpAccountByPageUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listWxMpAccountByPageUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageWxAccountVO_>('/api/wx/account/page', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取一个随机的32位token GET /api/wx/account/token */
export async function getTokenUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/wx/account/token', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 更新公众号信息 POST /api/wx/account/update */
export async function updateWxMpAccountUsingPOST(
  body: API.WxAccountUpdateDTO,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/wx/account/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
