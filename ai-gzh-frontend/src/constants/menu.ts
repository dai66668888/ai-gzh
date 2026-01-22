export const MENU_FIELDS_CONFIG = [
  {
    name: 'name',
    label: '菜单名称',
    placeholder: '请输入菜单名称（中文最长4个字/英文8个字）',
    type: 'text',
    rules: [{ required: true, message: '菜单名称不能为空' }],
  },
  {
    name: 'type',
    label: '菜单类型',
    placeholder: '请选择菜单类型',
    type: 'select',
    options: [
      { label: '点击事件', value: 'click' },
      { label: '网页跳转', value: 'view' },
      { label: '跳转小程序', value: 'miniprogram' },
    ],
    rules: [{ required: true, message: '菜单类型不能为空' }],
  },
  {
    name: 'url',
    label: '网页跳转链接',
    placeholder: '请输入网页链接（view、miniprogram 类型必填）',
    type: 'text',
  },
  {
    label: '菜单Key',
    name: 'menuKey',
    placeholder: '请输入菜单Key（用于消息接口推送）',
    type: 'text',
  },
];
