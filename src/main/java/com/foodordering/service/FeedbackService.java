package com.foodordering.service;

import com.foodordering.dto.FeedbackReplyRequest;
import com.foodordering.dto.FeedbackRequest;
import com.foodordering.entity.Feedback;
import com.foodordering.mapper.FeedbackMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackMapper feedbackMapper;
    private final OperationLogService logService;

    public FeedbackService(FeedbackMapper feedbackMapper, OperationLogService logService) {
        this.feedbackMapper = feedbackMapper;
        this.logService = logService;
    }

    @Transactional
    public void create(Long userId, FeedbackRequest request) { feedbackMapper.insert(userId, request.content()); }

    public List<Feedback> mine(Long userId) { return feedbackMapper.findByUserId(userId); }

    public List<Feedback> all(String status) { return feedbackMapper.findAll(status); }

    @Transactional
    public void reply(Long id, FeedbackReplyRequest request) {
        if (feedbackMapper.reply(id, request.reply()) == 0) throw new IllegalArgumentException("反馈不存在");
        logService.recordCurrentAdmin("回复用户反馈", "PUT", "/admin/feedback/" + id + "/reply", request.reply());
    }
}
