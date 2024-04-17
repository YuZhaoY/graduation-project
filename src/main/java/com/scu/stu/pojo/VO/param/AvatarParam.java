package com.scu.stu.pojo.VO.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AvatarParam {
    /**
     * 图片信息
     */
    private MultipartFile file;
}
