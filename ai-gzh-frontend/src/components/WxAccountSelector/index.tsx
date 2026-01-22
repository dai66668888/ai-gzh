import { useModel } from '@umijs/max';
import { Select, Spin, Typography } from 'antd';
import React from 'react';

const { Option } = Select;
const { Text } = Typography;

interface WxAccountSelectorProps {
  /**
   * 选择器宽度
   */
  width?: number | string;
}

/**
 * 公众号选择器组件
 * 展示公众号列表，支持切换当前选中的公众号
 */
const WxAccountSelector: React.FC<WxAccountSelectorProps> = (props) => {
  const { width = 160 } = props;
  const { wxAccountList, currentWxAccount, setCurrentWxAccount, loading } = useModel('myWxAccount');
  // 切换当前选中的公众号
  const handleChange = (value: number) => {
    const selectedAccount = wxAccountList.find((account: API.WxAccountVO) => account.id === value);
    if (selectedAccount) {
      setCurrentWxAccount(selectedAccount);
    }
  };

  return (
    <Select
      value={currentWxAccount?.id}
      onChange={handleChange}
      style={{ width }}
      placeholder="请选择公众号"
      loading={loading}
      notFoundContent={loading ? <Spin size="small" /> : <Text type="secondary">暂无公众号</Text>}
    >
      {wxAccountList.map((account: API.WxAccountVO) => (
        <Option key={account.id} value={account.id}>
          {account.name}
        </Option>
      ))}
    </Select>
  );
};

export default WxAccountSelector;
