package com.yuyuan.wxmp.service;

import com.yuyuan.wxmp.model.entity.AiReplyRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author cq
 * @description 针对表【ai_reply_record(AI 回复内容记录)】的数据库操作Service
 * @createDate 2025-03-14 19:16:00
 */
public interface AiReplyRecordService extends IService<AiReplyRecord> {

    String aiReply(String appId, String fromUser, String message, AiReplyRecord aiReplyRecord);
}
