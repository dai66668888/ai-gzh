import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = '微信公众号管理系统';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'wechat-docs',
          title: '微信文档',
          href: 'https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Overview.html',
          blankTarget: true,
        },
        {
          key: 'wx-java',
          title: 'WxJava',
          href: 'https://github.com/Wechat-Group/WxJava',
          blankTarget: true,
        },
        {
          key: '宁夏理工学院',
          title: (
            <>
              <GithubOutlined /> 母校
            </>
          ),
          href: 'http://www.nxist.com/',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
