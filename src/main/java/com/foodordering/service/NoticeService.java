package com.foodordering.service;

import com.foodordering.dto.NoticeRequest;
import com.foodordering.entity.Notice;
import com.foodordering.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeService {
    private final NoticeMapper noticeMapper;
    private final OperationLogService logService;

    public NoticeService(NoticeMapper noticeMapper, OperationLogService logService) {
        this.noticeMapper = noticeMapper;
        this.logService = logService;
    }

    public List<Notice> enabled() { return noticeMapper.findEnabled(); }
    public List<Notice> all() { return noticeMapper.findAll(); }

    @Transactional
    public void create(NoticeRequest request) {
        noticeMapper.insert(request);
        logService.recordCurrentAdmin("发布公告", "POST", "/admin/notices", request.title());
    }

    @Transactional
    public void update(Long id, NoticeRequest request) {
        ensure(id);
        noticeMapper.update(id, request);
        logService.recordCurrentAdmin("修改公告", "PUT", "/admin/notices/" + id, request.title());
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        ensure(id);
        if (status != 0 && status != 1) throw new IllegalArgumentException("状态只能是0或1");
        noticeMapper.updateStatus(id, status);
        logService.recordCurrentAdmin(status == 1 ? "上线公告" : "下线公告", "PUT", "/admin/notices/" + id + "/status", "status=" + status);
    }

    private void ensure(Long id) {
        if (noticeMapper.findById(id) == null) throw new IllegalArgumentException("公告不存在");
    }
}
