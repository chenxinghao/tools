package com.cxh.tool.Utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class FileUtils {
    File file;
    public FileUtils(){
    }
    public FileUtils(String filepath){
        this.file=new File(filepath);
    }

    public  String  readFileByLine() {
        StringBuffer sb = new StringBuffer();
        try {

            FileReader reader = new FileReader(this.file);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str + "\r\n");

                System.out.println(str);
            }
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public  void  writeFile(String str,boolean append) {
        try {
            FileWriter writer = new FileWriter(this.file,append);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(str);
            bw.close();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void  writeFile2(byte[] data){
        try {
            if (data != null) {
                if (this.file.exists()) {
                    this.file.delete();
                }
                FileOutputStream fos = new FileOutputStream(this.file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                osw.write(new String (data));
                osw.flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public  String   uploadfile(MultipartFile file) {
        return  uploadfile(file,"D:\\work\\JAVA_PROJECT\\tool\\src\\main\\resources\\static");
    }

    public  String   uploadfile(MultipartFile file,String uploadPath) {
        String path ="";
        if(file.isEmpty()){
            return "false";
        }
        String fileName = file.getOriginalFilename();
        int size = (int) file.getSize();
        System.out.println(fileName + "-->" + size);
        if (System.getProperties().getProperty("os.name").equals("Windows 10")){
            path = uploadPath;
        }else{
            path = "/home/cxh/tool/tempfile";
        }

        File dest = new File(path + File.separator + fileName);
        if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest); //保存文件
            return "true";
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        }
    }



    public  String   multifileUploadfile(HttpServletRequest request) {
        String path ="";
        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");

        if(files.isEmpty()){
            return "false";
        }

        if (System.getProperties().getProperty("os.name").equals("Windows 10")){
            path = "D:\\work\\JAVA_PROJECT\\tool\\src\\main\\resources\\static";
        }else{
            path = "/home/cxh/tool/tempfile";
        }

        for(MultipartFile file:files){
            String fileName = file.getOriginalFilename();
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if(file.isEmpty()){
                return "false";
            }else{
                File dest = new File(path + "/" + fileName);
                if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "false";
                }
            }
        }
        return "true";
    }


    public  void   downLoadFile(String filename,String suffix, String filepath,HttpServletResponse response) {
        File file = new File(filepath + File.separator + filename);
        if(file.exists()){ //判断文件父目录是否存在
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;fileName=" + filename+suffix);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public  void   downLoadFile(String filename, String filepath,HttpServletResponse response) {
        downLoadFile(filename,"", filepath, response);
    }

    public  void   deleteAllfile(){
        File currentFile=file;
        if (!file.isDirectory()){
            currentFile=file.getParentFile();
        }
        for (File f:currentFile.listFiles()){
            if (f.isFile()) {
                f.delete();
            }
        }

    }

}
