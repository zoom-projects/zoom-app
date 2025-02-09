package com.hb0730.zoom.controller;

import com.google.common.collect.ImmutableMap;
import com.hb0730.zoom.base.AppUtil;
import com.hb0730.zoom.base.R;
import com.hb0730.zoom.base.ext.security.SecurityUtils;
import com.hb0730.zoom.base.utils.IdUtil;
import com.hb0730.zoom.oss.core.OssStorage;
import com.hb0730.zoom.oss.util.OssUtil;
import com.hb0730.zoom.sys.biz.system.model.request.task.SysBizTaskCreateRequest;
import com.hb0730.zoom.sys.biz.system.service.SysBizTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

/**
 * @author <a href="mailto:huangbing0730@gmail">hb0730</a>
 * @date 2024/12/25
 */
@RestController
@RequestMapping("/sys/common/task")
@Tag(name = "任务网关接口")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final SysBizTaskService sysBizTaskService;
    private final OssStorage ossStorage;


    /**
     * 导出任务
     *
     * @param request 请求参数
     */
    @Operation(summary = "导出任务", description = "根据页面指定的条件下达导出任务")
    @PostMapping(value = "/exp")
    public R<String> exp(@RequestBody SysBizTaskCreateRequest request) {
        return sysBizTaskService.saveTask(request);
    }

    /**
     * 导入任务
     *
     * @param file     文件
     * @param bizApp   业务类型
     * @param taskType 任务类型
     */
    @Operation(summary = "导入任务", description = "根据页面指定的条件下达导入任务")
    @PostMapping(value = "/imp")
    public R<String> imp(@RequestParam("file") MultipartFile file, @RequestParam("bizApp") String bizApp,
                         @RequestParam(
                                 "taskType") String taskType) {
        R<SysBizTaskCreateRequest> param = getTaskParam(bizApp, taskType, file);
        if (param.isSuccess()) {
            return sysBizTaskService.saveTask(param.getData());
        }
        return R.NG(param.getMessage());
    }

    /**
     * 获取任务参数
     *
     * @param bizApp   业务类型
     * @param taskType 任务类型
     * @param file     文件
     */
    private R<SysBizTaskCreateRequest> getTaskParam(String bizApp, String taskType, @Nonnull MultipartFile file) {
        SysBizTaskCreateRequest param = new SysBizTaskCreateRequest();
        param.setAppName(bizApp);
        param.setType(taskType);
        param.setLotNo(IdUtil.getSnowflakeNextIdStr());
        param.setParam(ImmutableMap.of("imp", Objects.requireNonNull(file.getOriginalFilename())));

        param.setFileName(String.format("%s-%s.%s", taskType, param.getLotNo(),
                FilenameUtils.getExtension(file.getOriginalFilename())));

        try {
            File localFile = new File(AppUtil.getProperty("zoom.path.upload", "/tmp"),
                    param.getFileName());
            file.transferTo(localFile);
            String filePath = SecurityUtils.getLoginUsername().orElseGet(() -> "summary") + "/imp";
            String objectKey = OssUtil.normalize(filePath + "/" + param.getFileName());
            String url = ossStorage.uploadFile(OssUtil.getObjectKey(objectKey), localFile);
            param.setFilePath(filePath);
            param.setFileUrl(url);

        } catch (Exception e) {
            log.error("上传文件保存失败", e);
            return R.NG("上传文件保存失败");
        }
        return R.OK(param);
    }
}
