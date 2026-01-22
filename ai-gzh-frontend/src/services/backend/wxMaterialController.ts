// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 删除素材 POST /api/wx/material/${param0}/delete */
export async function deleteMaterialUsingPOST(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteMaterialUsingPOSTParams,
  body: API.MaterialDeleteRequest,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean_>(`/api/wx/material/${param0}/delete`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  });
}

/** 下载图片或音频 GET /api/wx/material/${param0}/img_voice/download */
export async function downloadImgAndVoiceMaterialUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadImgAndVoiceMaterialUsingGETParams,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.InputStreamResource>(`/api/wx/material/${param0}/img_voice/download`, {
    method: 'GET',
    params: {
      ...queryParams,
    },
    ...(options || {}),
  });
}

/** 查询素材列表 GET /api/wx/material/${param0}/list */
export async function listAllMaterialUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAllMaterialUsingGETParams,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseWxMpMaterialFileBatchGetResult_>(
    `/api/wx/material/${param0}/list`,
    {
      method: 'GET',
      params: {
        ...queryParams,
      },
      ...(options || {}),
    },
  );
}

/** 上传素材（图片、音频、视频） POST /api/wx/material/${param0}/upload */
export async function uploadMaterialUsingPOST(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadMaterialUsingPOSTParams,
  body: {},
  file?: File,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      formData.append(
        ele,
        typeof item === 'object' && !(item instanceof File) ? JSON.stringify(item) : item,
      );
    }
  });

  return request<API.BaseResponseBoolean_>(`/api/wx/material/${param0}/upload`, {
    method: 'POST',
    params: {
      ...queryParams,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** 查询视频详情 GET /api/wx/material/${param0}/video/info */
export async function getMaterialVideoByMaterialIdUsingGET(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getMaterialVideoByMaterialIdUsingGETParams,
  options?: { [key: string]: any },
) {
  const { appId: param0, ...queryParams } = params;
  return request<API.BaseResponseWxMpMaterialVideoInfoResult_>(
    `/api/wx/material/${param0}/video/info`,
    {
      method: 'GET',
      params: {
        ...queryParams,
      },
      ...(options || {}),
    },
  );
}

/** 获取素材类型 GET /api/wx/material/get/type */
export async function getMaterialTypeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayWxMaterialTypeEnum_>('/api/wx/material/get/type', {
    method: 'GET',
    ...(options || {}),
  });
}
