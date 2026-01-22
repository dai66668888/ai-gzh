// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 删除菜单 注意这里是全部删除 POST /api/wx/menu/delete/${param0} */
export async function deleteMenuUsingPOST(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteMenuUsingPOSTParams,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/wx/menu/delete/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 获取公众号菜单 GET /api/wx/menu/get/${param0} */
export async function getMenuUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getMenuUsingGETParams,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseWxMenuDTO_>(`/api/wx/menu/get/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 获取菜单类型 GET /api/wx/menu/get/type */
export async function getMenuTypeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayWxMenuButtonTypeEnum_>('/api/wx/menu/get/type', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 更新菜单 POST /api/wx/menu/update/${param0} */
export async function updateMenuUsingPOST(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateMenuUsingPOSTParams,
  body: API.WxMenuButtonDTO[],
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/wx/menu/update/${param0}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}
