package com.cxh.tool.Controller;


import com.cxh.tool.Utils.EncryptUtils;
import com.cxh.tool.Utils.FileUtils;
import com.cxh.tool.config.EncryptConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;




@Controller
public class EncryptController {


    @Autowired
    private EncryptConfig encryptConfig;


    /*
     * 获取file.html页面
     */
    @GetMapping("encrypt")
    public String encryptfile(){
        return "/encrypt";
    }

    /**
     * 实现文件上传
     * */
    @PostMapping("fileEncrypt")
    @ResponseBody
    public void fileEncrypt(@RequestParam("fileName") MultipartFile file, HttpServletResponse response){
//        new FileUtils().uploadfile(file,"D:\\work\\JAVA_PROJECT\\tool\\src\\main\\resources\\tempfile");

        new FileUtils().uploadfile(file,encryptConfig.getUploadFilePath());

        String content=new FileUtils(encryptConfig.getUploadFilePath()+File.separator+file.getOriginalFilename()).readFileByLine();

        byte[] contentE=EncryptUtils.encrypt(content,"123456");
        new FileUtils(encryptConfig.getUploadFilePath()+File.separator+file.getOriginalFilename()+"1").writeFile2(
                EncryptUtils.bytes2HexStr(contentE).getBytes());

        // 加密
        byte[] epwd = EncryptUtils.encrypt("123456", "cxh920928");
        System.out.println("加密密码后的内容：" + new String(epwd));

        //转16
        String epwds=EncryptUtils.bytes2HexStr(epwd);
        System.out.println("16进制内容：" + epwds);


//        EncryptUtils.decrypt(EncryptUtils.hexStr2Byte(EncryptUtils.bytes2HexStr(contentE)), "123456");


        //下载加密后文件
        new FileUtils().downLoadFile(file.getOriginalFilename()+"1","_"+epwds,encryptConfig.getUploadFilePath(),response);
        new FileUtils(encryptConfig.getUploadFilePath()).deleteAllfile();
    }




    @GetMapping("decrypt")
    public String decryptfile(){
        return "/decrypt";
    }

    @PostMapping("fileDecrypt")
    @ResponseBody
    public void fileDecrypt(@RequestParam("fileName") MultipartFile file, HttpServletResponse response){
        new FileUtils().uploadfile(file,encryptConfig.getUploadFilePath()).trim();
        String s= new FileUtils(encryptConfig.getUploadFilePath()+File.separator+file.getOriginalFilename()).readFileByLine().trim();
        byte[] contentE=EncryptUtils.hexStringToBytes(s);

        String[] filenames=file.getOriginalFilename().split("_");
        String epwds=filenames[filenames.length-1];
        //转byte[]
        byte[] pwds=EncryptUtils.hexStr2Byte(epwds);
        System.out.println("byte[]内容：" + new String(pwds));



        // 解密
        byte[] pwd = EncryptUtils.decrypt(pwds, "cxh920928");
        System.out.println("解密密码后的内容：" + new String(pwd));

        // 解密
        byte[] decrypt = EncryptUtils.decrypt(contentE, new String(pwd));
        System.out.println("解密后的内容：" + new String(decrypt));
        new FileUtils(encryptConfig.getUploadFilePath()+File.separator+file.getOriginalFilename().split("_")[0]).writeFile2(decrypt);
        new FileUtils().downLoadFile(file.getOriginalFilename().split("_")[0],"",encryptConfig.getUploadFilePath(),response);
    }





}
