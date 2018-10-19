package com.cxh.tool.Controller;
import com.cxh.tool.Utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
public class UpFileController {



    /*
     * 获取file.html页面
     */
    @GetMapping("file")
    public String file(){
        return "/file";
    }

    /**
     * 实现文件上传
     * */
    @PostMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("fileName") MultipartFile file){
        return new FileUtils().uploadfile(file);
    }

    /*
     * 获取multifile.html页面
     */
    @RequestMapping("multifile")
    public String multifile(){
        return "/multifile";
    }

    /**
     * 实现多文件上传
     * */
    @PostMapping(value="multifileUpload")
    public @ResponseBody String multifileUpload(HttpServletRequest request){
        return new FileUtils().multifileUploadfile(request);
    }



    @RequestMapping("download")
    public void downLoad(HttpServletResponse response){
        new FileUtils().downLoadFile("jdk-8u181-linux-x64.tar.gz","D:\\work\\JAVA_PROJECT\\tool\\src\\main\\resources\\static",response);

    }

}