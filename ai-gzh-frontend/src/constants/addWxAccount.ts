export const ACCOUNTS_FIELDS_CONFIG = [
  {
    name: 'name',
    rules: [{ required: true, message: '请输入公众号名称' }],
    placeholder: '公众号名称',
  },
  {
    name: 'aesKey',
    rules: [{ required: true, message: '请输入公众号AESKey' }],
    placeholder: '公众号AESKey',
  },
  {
    name: 'secret',
    rules: [{ required: true, message: '请输入公众号Secret' }],
    placeholder: '公众号Secret',
  },
  {
    name: 'token',
    rules: [{ required: true, message: '请输入公众号Token' }],
    placeholder: '公众号Token',
  },
  {
    name: 'appId',
    rules: [{ required: true, message: '请输入公众号AppID' }],
    placeholder: '公众号AppID',
  },
];
