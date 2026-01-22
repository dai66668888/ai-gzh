import { ProColumns } from '@ant-design/pro-components';

/**
 * 表格列定义
 */
export const WX_ACCOUNT_GUIDE_COLUMNS: ProColumns<API.WxAccountVO>[] = [
  {
    title: 'ID',
    dataIndex: 'id',
    valueType: 'text',
    width: 80,
    search: false,
  },
  {
    title: '公众号名称',
    dataIndex: 'name',
    valueType: 'text',
    ellipsis: true,
  },
  {
    title: 'AppID',
    dataIndex: 'appId',
    valueType: 'text',
    ellipsis: true,
    copyable: true,
  },
  {
    title: '密钥',
    dataIndex: 'secret',
    valueType: 'text',
    search: false,
    copyable: true,
  },
  {
    title: 'Token',
    dataIndex: 'token',
    valueType: 'text',
    search: false,
    ellipsis: true,
    copyable: true,
  },
  {
    title: 'AES Key',
    dataIndex: 'aesKey',
    valueType: 'text',
    search: false,
    ellipsis: true,
    copyable: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    valueType: 'dateTime',
    search: false,
    sorter: true,
  },
];
