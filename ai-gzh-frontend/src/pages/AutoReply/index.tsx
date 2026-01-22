import BaseLayout from '@/components/BaseLayout';
import WxReplyRuleForm from '@/components/WxReplyRuleForm';
import {
  deleteWxReplyRuleByIdsUsingPOST,
  listWxMpReplyRuleByPageUsingGET,
} from '@/services/backend/wxReplyRuleController';
import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  QuestionCircleOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Button, Modal, Popconfirm, Tag, Tooltip, message } from 'antd';
import { SortOrder } from 'antd/es/table/interface';
import React, { useCallback, useEffect, useRef, useState } from 'react';

/**
 * 自动回复管理页面
 */
const AutoReplyPage: React.FC = () => {
  // 获取公众号数据
  const { currentWxAccount } = useModel('myWxAccount');
  // 加载状态
  const [loading, setLoading] = useState<boolean>(false);
  const { replyTypeList, matchTypeList, contentTypeList, initEnumData } = useModel('autoReply');

  // 表格 action 引用
  const actionRef = useRef<ActionType>();
  // 控制添加/编辑规则表单的显示
  const [formVisible, setFormVisible] = useState(false);
  // 当前编辑的规则 ID
  const [currentRuleId, setCurrentRuleId] = useState<number | undefined>(undefined);
  // 选中的规则 IDs
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  // 页面枚举数据
  useEffect(() => {
    initEnumData();
  }, []);

  // 当公众号变化时，重新加载数据
  useEffect(() => {
    if (currentWxAccount?.appId) {
      // 如果有选中的公众号，则加载数据
      actionRef.current?.reload();
    }
  }, [currentWxAccount]);

  // 处理添加规则
  const handleAddRule = () => {
    if (!currentWxAccount) {
      message.warning('请先选择公众号');
      return;
    }
    setCurrentRuleId(undefined);
    setFormVisible(true);
  };

  /**
   * 删除回复规则
   * @param ids 要删除的规则 ID 数组
   */
  const deleteReplyRules = useCallback(async (ids: number[]): Promise<boolean> => {
    setLoading(true);
    try {
      const res = await deleteWxReplyRuleByIdsUsingPOST(ids);
      if (res.code === 0 && res.data) {
        message.success('删除成功');
        return true;
      } else {
        message.error(res.message || '删除失败');
        return false;
      }
    } catch (error) {
      console.error('删除回复规则失败：', error);
      message.error('删除回复规则失败');
      return false;
    } finally {
      setLoading(false);
    }
  }, []);

  // 处理编辑规则
  const handleEditRule = (id: number) => {
    setCurrentRuleId(id);
    setFormVisible(true);
  };

  // 处理删除规则
  const handleDeleteRule = async (id: number) => {
    const success = await deleteReplyRules([id]);
    if (success) {
      actionRef.current?.reload();
    }
  };

  // 表单操作成功后的回调
  const handleFormSuccess = () => {
    setFormVisible(false);
    actionRef.current?.reload();
  };

  // 获取回复类型标签颜色
  const getReplyTypeTagColor = (type?: number) => {
    switch (type) {
      case 0:
        return 'blue';
      case 1:
        return 'green';
      case 2:
        return 'purple';
      case 3:
        return 'orange';
      default:
        return 'default';
    }
  };

  // 获取回复类型名称
  const getReplyTypeName = (type?: number) => {
    const found = replyTypeList.find((item) => item.value === type);
    return found?.label || '未知类型';
  };

  // 获取匹配类型名称
  const getMatchTypeName = (type?: number) => {
    const found = matchTypeList.find((item) => item.value === type);
    return found?.label || '未知类型';
  };

  // 获取内容类型名称
  const getContentTypeName = (type?: number) => {
    const found = contentTypeList.find((item) => item.value === type);
    return found?.label || '未知类型';
  };

  // 表格列定义
  const columns: ProColumns<API.WxReplyRuleVO>[] = [
    {
      title: '规则名称',
      dataIndex: 'ruleName',
      width: 180,
      ellipsis: true,
    },
    {
      title: '规则描述',
      dataIndex: 'ruleDescription',
      width: 200,
      ellipsis: true,
    },
    {
      title: '触发类型',
      dataIndex: 'replyType',
      width: 120,
      valueEnum: replyTypeList.reduce((acc, curr) => {
        if (curr.value !== undefined && curr.label) {
          acc[curr.value] = { text: curr.label };
        }
        return acc;
      }, {} as Record<string, { text: string }>),
      render: (_, record) => (
        <Tag color={getReplyTypeTagColor(record.replyType)}>
          {getReplyTypeName(record.replyType)}
        </Tag>
      ),
    },
    {
      title: '匹配关键词',
      dataIndex: 'matchValue',
      width: 200,
      ellipsis: true,
      render: (_, record) => {
        if (!record.matchValue || record.matchValue.length === 0) {
          return '-';
        }

        return (
          <>
            {record.matchValue.map((item, index) => (
              <div key={index}>
                <Tag>{item.matchKeyWords}</Tag>
                <small>({getMatchTypeName(item.matchType)})</small>
              </div>
            ))}
          </>
        );
      },
    },
    {
      title: '回复内容',
      dataIndex: 'replyContent',
      width: 200,
      ellipsis: true,
      render: (_, record) => {
        if (!record.replyContent) {
          return '-';
        }

        const contentType = record.replyContent.contentType;
        const typeName = getContentTypeName(contentType);

        let content = '';
        if (contentType === 0 && record.replyContent.textContent) {
          content = record.replyContent.textContent;
        } else if (
          (contentType === 1 || contentType === 2 || contentType === 3) &&
          record.replyContent.mediaId
        ) {
          content = `素材 ID: ${record.replyContent.mediaId}`;
        } else if (contentType === 4 && record.replyContent.articleId) {
          content = `图文 ID: ${record.replyContent.articleId}`;
        }

        return (
          <Tooltip title={content}>
            <Tag color="cyan">{typeName}</Tag>
            <div className="ellipsis">{content}</div>
          </Tooltip>
        );
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      search: false,
      sorter: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      width: 120,
      render: (_, record) => [
        <Button
          key="edit"
          type="link"
          style={{ padding: 0 }}
          icon={<EditOutlined />}
          onClick={() => handleEditRule(record.id as number)}
        >
          编辑
        </Button>,
        <Popconfirm
          key="delete"
          title="确定要删除此规则吗？"
          icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
          onConfirm={() => handleDeleteRule(record.id as number)}
        >
          <Button style={{ padding: 0 }} type="link" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>,
      ],
    },
  ];

  // 处理批量删除规则
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请至少选择一条规则');
      return;
    }
    // 弹窗确认删除
    Modal.confirm({
      title: '确定要删除这些规则吗？',
      content: `确认删除 ${selectedRowKeys.length} 条规则吗？`,
      onOk: async () => {
        const success = await deleteReplyRules(selectedRowKeys as number[]);
        if (success) {
          setSelectedRowKeys([]);
          actionRef.current?.reload();
        }
      },
    });
  };

  const proTableRequest = async (
    params: {
      pageSize?: number;
      current?: number;
      keyword?: string;
    } & API.WxReplyRuleVO,
    sort: Record<string, SortOrder>,
  ) => {
    if (!currentWxAccount?.appId) {
      return {
        data: [],
        success: true,
        total: 0,
      };
    }

    // 处理排序
    const sortField = Object.keys(sort || {})[0];
    const sortOrder = sortField ? (sort[sortField] === 'ascend' ? 'asc' : 'desc') : undefined;

    // 构建查询参数
    const queryParams: API.listWxMpReplyRuleByPageUsingGETParams = {
      appId: currentWxAccount.appId,
      current: params.current,
      pageSize: params.pageSize,
      ruleName: params.ruleName,
      ruleDescription: params.ruleDescription,
      replyType: params.replyType as number,
      matchValue: params.matchValue as any,
      sortField,
      sortOrder,
    };

    try {
      const result = await listWxMpReplyRuleByPageUsingGET(queryParams);
      return {
        data: result.data?.records || [],
        success: result.code === 0,
        total: result.data?.total || 0,
      };
    } catch (error) {
      message.error('获取回复规则列表失败');
      return {
        data: [],
        success: false,
        total: 0,
      };
    }
  };
  return (
    <BaseLayout title="自动回复管理">
      <ProTable<API.WxReplyRuleVO>
        headerTitle="回复规则列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          defaultColsNumber: 6,
          span: {
            xs: 24,
            sm: 12,
            md: 8,
            lg: 6,
            xl: 6,
            xxl: 6,
          },
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        toolBarRender={() => [
          <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleAddRule}>
            新建规则
          </Button>,
          <Button
            key="batchDelete"
            danger
            icon={<DeleteOutlined />}
            onClick={handleBatchDelete}
            disabled={selectedRowKeys.length === 0 || loading}
          >
            批量删除
          </Button>,
        ]}
        request={proTableRequest}
        columns={columns}
        pagination={{
          showQuickJumper: true,
          showSizeChanger: true,
        }}
      />
      {/* 添加/编辑规则表单 */}
      <WxReplyRuleForm
        visible={formVisible}
        onCancel={() => setFormVisible(false)}
        onSuccess={handleFormSuccess}
        ruleId={currentRuleId}
      />
    </BaseLayout>
  );
};

export default AutoReplyPage;
