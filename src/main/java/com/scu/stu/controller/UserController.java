package com.scu.stu.controller;

import com.alibaba.fastjson.JSON;
import com.scu.stu.common.RoleEnum;
import com.scu.stu.pojo.DO.LoginInfoDO;
import com.scu.stu.common.Result;
import com.scu.stu.pojo.DO.UserInfoDO;
import com.scu.stu.pojo.VO.*;
import com.scu.stu.pojo.VO.param.*;
import com.scu.stu.service.UserService;
import com.scu.stu.utils.JwtUtils;
import com.scu.stu.utils.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@RestController
@CrossOrigin
public class UserController {

    @Value("${file.upload-path}")
    private String pictureURL;

    @Resource
    private UserService userService;

    @PostMapping(value = "api/login")
    public Result login(@RequestBody LoginParam loginParam) {
        try {
            // 对 html 标签进行转义，防止 XSS 攻击
            String username = loginParam.getUsername();
            username = HtmlUtils.htmlEscape(username);
            String password = loginParam.getPassword();
            password = HtmlUtils.htmlEscape(password);
            LoginInfoDO loginInfo = userService.queryLoginInfo(username);
            if (Objects.isNull(loginInfo)) {
                String message = "账号不存在";
                return Result.error(message);
            } else {
                if (Objects.equals(password, loginInfo.getPassword())) {
                    String token = JwtUtils.sign(username);
                    return Result.success(new TokenVO(token));
                } else {
                    return Result.error("密码错误");
                }
            }
        } catch (Exception e) {
            return Result.error("验证错误");
        }
    }

    @GetMapping("/api/info")
    public Result userInfo(@RequestParam("token") String token) {
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            UserInfoDO userInfoDO = userService.queryUserInfo(userId);
            LoginInfoDO loginInfoDO = userService.queryLoginInfo(userId);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userInfoDO, userVO);
            userVO.setPassword(loginInfoDO.getPassword());
            List<String> roles = new ArrayList<>();
            roles.add(RoleEnum.getDescByRole(userInfoDO.getRole()));
            userVO.setRoles(roles);
            return Result.success(userVO);
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/updateLoginAccount")
    public Result updateLoginAccount(@RequestBody PwdParam pwdParam) {
        String tokenValue = JwtUtils.verity(pwdParam.getToken());
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            LoginInfoDO loginInfoDO = new LoginInfoDO();
            loginInfoDO.setPassword(pwdParam.getPassword());
            loginInfoDO.setUsername(userId);
            boolean result = userService.updateLoginInfo(loginInfoDO);
            if (result) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping(value = "api/updateUserAccount")
    public Result updateUserAccount(@RequestParam("userInfo") String userInfo, @RequestParam("token") String token) {
        UserInfoParam userInfoParam = JSON.parseObject(userInfo,UserInfoParam.class);
        String tokenValue = JwtUtils.verity(token);
        if (tokenValue.startsWith(JwtUtils.TOKEN_SUCCESS)) {
            String userId = tokenValue.replaceFirst(JwtUtils.TOKEN_SUCCESS, "");
            UserInfoDO userInfoDO = new UserInfoDO();
            BeanUtils.copyProperties(userInfoParam,userInfoDO);
            userInfoDO.setUserId(userId);
            boolean result = userService.updateUserInfo(userInfoDO);
            if (result) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } else {
            return Result.error("未查到身份信息");
        }
    }

    @PostMapping("api/upload")
    public Result upload(AvatarParam avatarParam) {
        MultipartFile file = avatarParam.getFile();
        String orgFileName = file.getOriginalFilename();
        String fileName = RandomUtils.generateID() + orgFileName.substring(orgFileName.indexOf("."));
        File saveFile = new File(pictureURL);
        if (!saveFile.exists()) {
            //若不存在该目录，则创建目录
            saveFile.mkdir();
        }
        UploadParam imageurl = new UploadParam();
        UUID uuid = UUID.randomUUID();
        String url = file.getOriginalFilename().replace(".", "") + uuid;
        String name = file.getOriginalFilename();
        imageurl.setPictureURL(url);
        imageurl.setPictureName(name);
        try {
            //将文件保存指定目录
            file.transferTo(new File(pictureURL + fileName));
            return Result.success("img/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("fail save picture");
        }
    }

    @GetMapping("api/remove")
    public Result remove(@RequestParam("url") String url) {
        String fileUrl = pictureURL + url.replace("img/","");
        File removeFile = new File(fileUrl);
        if(removeFile.exists()){
            try{
                if(removeFile.delete()){
                    return Result.success();
                } else {
                    return Result.error("fail remove picture");
                }
            } catch (Exception e){
                return Result.error("remove catch Exception");
            }
        } else {
            return Result.success();
        }
    }

    @GetMapping("api/getFarmerList")
    public Result getFarmerList() {
        List<UserInfoDO> userInfoDOS = userService.queryUserListInfo(RoleEnum.EDITOR.getRole());
        List<FarmerVO> farmerVOList = new ArrayList<>();
        for(UserInfoDO userInfoDO:userInfoDOS){
            FarmerVO farmerVO = new FarmerVO();
            farmerVO.setFarmerId(userInfoDO.getUserId());
            farmerVO.setName(userInfoDO.getName());
            farmerVOList.add(farmerVO);
        }
        return Result.success(farmerVOList);
    }

    @GetMapping("api/getPurchaseList")
    public Result getPurchaseList() {
        List<UserInfoDO> userInfoDOS = userService.queryUserListInfo(RoleEnum.ADMIN.getRole());
        List<PurchasetVO> purchaseVOList = new ArrayList<>();
        for(UserInfoDO userInfoDO:userInfoDOS){
            PurchasetVO purchasetVO = new PurchasetVO();
            purchasetVO.setPurchaseId(userInfoDO.getUserId());
            purchasetVO.setName(userInfoDO.getName());
            purchaseVOList.add(purchasetVO);
        }
        return Result.success(purchaseVOList);
    }
}
