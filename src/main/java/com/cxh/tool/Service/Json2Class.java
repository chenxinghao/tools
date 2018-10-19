package com.cxh.tool.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cxh.tool.Utils.FileUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.*;

@Service
public class Json2Class {
    private TypeReference<Map<String,Object>> mapType=new TypeReference<Map<String,Object>>(){};//map的泛型类型
    private ArrayList<ArrayList<Object>> queue = new ArrayList<ArrayList<Object>>();//用于存放需要新生成的类名与对象-<<类名，对象>>
    private Set<String> classSet = new HashSet<String>();//用于存放类名
    private StringBuffer sb=new StringBuffer();//用于存放中间结果


    //按照java 规则打印类名与变量名
    public void scanJsonSimple(String jsonStr,String clazzName){
        Map<String,Object> jsonMap = JSON.parseObject(jsonStr,this.mapType);
        sb.append("##########");//用于分割import 与 属性的分隔行
        sb.append("\n");
        for (Map.Entry<String,Object> entry : jsonMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                //当前转换的属性是基础属性---String;
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof Integer) {
                //当前转换的属性是基础属性---Integer
                sb.append("int");
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//int fieldname\n
            } else if (entry.getValue() instanceof BigDecimal) {
                classSet.add(entry.getValue().getClass().toString());
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof Date) {
                classSet.add(entry.getValue().getClass().toString());
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof Boolean) {
                classSet.add(entry.getValue().getClass().toString());
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof Long) {
                classSet.add(entry.getValue().getClass().toString());
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof Timestamp) {
                classSet.add(entry.getValue().getClass().toString());
                sb.append(getLastStr(entry.getValue().getClass().toString(), "\\."));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//class_short_name fieldname\n
            } else if (entry.getValue() instanceof JSONObject) {
                sb.append(this.toUpperCaseFirstOne(entry.getKey()));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//create_class_short_name fieldname\n
                ArrayList<Object> tq = new ArrayList<Object>();//add queue
                tq.add(this.toUpperCaseFirstOne(entry.getKey()));
                tq.add(entry.getValue());
                queue.add(tq);
                classSet.add("class " + this.toUpperCaseFirstOne(entry.getKey()));//add  import
            } else if (entry.getValue() instanceof JSONArray) {
                classSet.add("class java.util.List");//add  import
                String jstr = ((JSONArray) entry.getValue()).toJSONString();
                sb.append(this.parseJSONArray(jstr, entry.getKey()));
                sb.append(" ");
                sb.append(entry.getKey());
                sb.append("\n");//create_list_class_short_name fieldname\n
            }else {
                System.out.println("unfind type:"+entry.getValue().getClass().toString());
            }
        }
        Iterator<String> it = classSet.iterator();
        while (it.hasNext()) {
            String str = it.next();
            sb.insert(0,"\n");
            sb.insert(0,str);//sb 插入class; \n

        }
        classSet.clear();

        createClass(clazzName);

    }

    //生成的JsonArray变量名
    public String  parseJSONArray(String jsonArrayStr,String name){
        List<Object> li=JSON.parseArray(jsonArrayStr, Object.class);
        //if not empty
        if  (li.isEmpty()){
            return  "List<Object>";
        }

        for (Object ob :li){

        }

        Object value=li.get(0);
        if(value instanceof  String){
            return "List<String>";
        }else if (value instanceof  Integer){
            return "List<Integer>";
        }else if (value instanceof  BigDecimal){
            classSet.add(value.getClass().toString());
            return "List<BigDecimal>";
        }else if (value instanceof Date) {
            classSet.add(value.getClass().toString());
            return "List<Date>";
        } else if (value instanceof Boolean) {
            classSet.add(value.getClass().toString());
            return "List<Boolean>";
        } else if (value instanceof Long) {
            classSet.add(value.getClass().toString());
            return "List<Long>";
        } else if (value instanceof Timestamp) {
            classSet.add(value.getClass().toString());
            return "List<Timestamp>";
        } else if (value instanceof JSONObject){
            ArrayList<Object> tq=new ArrayList<Object>();
            tq.add(this.toUpperCaseFirstOne(name));
            tq.add(value);
            queue.add(tq);
            classSet.add("class "+this.toUpperCaseFirstOne(name));
            return "List<"+this.toUpperCaseFirstOne(name)+">";
        }else if (value instanceof JSONArray){
            return "List<"+parseJSONArray(((JSONArray)value).toJSONString(),name)+">";
        }
        return "";
    }

    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public String getLastStr(String s, String delimiter  ){
        String[] ss=s.split(delimiter);
        return ss[ss.length-1];
    }







    public String scanJson(String jsonStr){
        StringBuffer res=new StringBuffer("");
        this.scanJsonSimple(jsonStr,null);
        res.append( sb.toString());
        sb.setLength(0);
        while(!this.queue.isEmpty()){
            List<Object> reslist = this.queue.get(0);
            this.queue.remove(0);
            Object ob=reslist.get(1);
            if (ob  instanceof  JSONObject){
                this.scanJsonSimple(((JSONObject)ob).toJSONString(),(String)reslist.get(0));
                res.append( sb.toString());
                sb.setLength(0);;
            }
        }

        System.out.println(res.toString());
        return res.toString();
    }




    public static void  main (String[] args){
        File file=new File("D:\\work\\JAVA_PROJECT\\tool\\src\\main\\java\\com\\cxh\\tool\\Service\\test.txt");
        String s = "";
        String res="";
        try{
            BufferedReader reader=new BufferedReader(new FileReader(file));

            while((s=reader.readLine())!=null){

                System.out.println(s);
                res+=s;

            }

        }
        catch(Exception e){

            e.printStackTrace();

        }

        FileUtils fileUtils=new FileUtils("D:\\work\\JAVA_PROJECT\\tool\\src\\main\\java\\com\\cxh\\tool\\Service\\write.txt");

        fileUtils.writeFile(new Json2Class().scanJson(res),true);

    }



    public void  createClass (String clazzName){

        String classStr= sb.toString();
        sb.setLength(0);//清空效率高
        if (clazzName==null){
            clazzName="test";
        }
        String[] content=classStr.split("##########\n");
        String[] clazzes=content[0].split("\n");
        String[] field =content[1].split("\n");
        for  (String s : clazzes) {
            if (s.equals("")){
                continue;
            }
            String ts = s.split(" ")[1];
            sb.append("import ");
            sb.append(ts);
            sb.append(";\n");//import  classpath;\n
        }
        sb.append("\n");
        sb.append("public class ");
        sb.append(clazzName);
        sb.append(" {\n");// \n public class {\n


        for  (String s : field) {
            if (s.equals("")){
                continue;
            }
            sb.append("\t");
            sb.append(s);
            sb.append(";\n");//\t class_short_name name \n

        }
        sb.append("}");
        sb.append("\n\n\n");
    }

    //TODO 根据不同的list转成的object    相似度判断都是对象的判断有多少相似的属性名  相似度可作为参数


    public float getSimilarity  (Object ob1,Object ob2) {
        if (!ob1.getClass().toString().equals(ob2.getClass().toString())){
            return 0;
        }
        int same=0;
        int sum;
        Field[] fields1= ob1.getClass().getDeclaredFields();
        Field[] fields2= ob2.getClass().getDeclaredFields();
        for (Field f1 : fields1){
            for (Field f2 : fields2){
                if (f1.getName().equals(f2.getName())){
                    same+=1;
                    continue;
                }
            }

        }
        sum=fields1.length+fields2.length;
        return  2*same/(float)sum;

    }
}
