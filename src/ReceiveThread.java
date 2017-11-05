import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class ReceiveThread extends Thread{
     private String clientip;
     private Map<String, SocketInfo> dic;
     private List<Student> studentlist;
     private String text;
     private InputStream is;
     private OutputStream out;
     public ReceiveThread(Map<String, SocketInfo> dic, String clientip, List<Student> studentlist, String text) {
    	 this.dic = dic;
         this.clientip = clientip;
         this.studentlist = studentlist;
         this.text = text;
     }
     
     
     @Override
     public void run() {
         try {
        	 parseProtocol(clientip, dic, studentlist, text);
         } catch (IOException e) {
             e.printStackTrace();
             System.out.println("1111");
         }
     }
     
     /**
      * 消息解析
      *
      * @param client
      * @throws IOException
      */
     private static void parseProtocol(String clientip ,Map<String, SocketInfo> dic, List<Student> studentlist, String text) throws IOException {
    	 SocketInfo clientinfo = dic.get(clientip);
    	 Socket client = clientinfo.getClient();
    	 DataInputStream dis = clientinfo.getIs();
    	 DataOutputStream out = clientinfo.getOut();
         //DataInputStream dis = new DataInputStream(is); //读取Java标准数据类型的输入流

         //协议解析
         //0:say hi，1:断开连接,2:请求文件，3：改，4.删 5.增
         while (true) {
        	 
             byte type = dis.readByte();               //读取消息类型
             int totalLen = dis.readInt();             //读取消息长度
             byte[] data = new byte[totalLen - 4 - 1]; //定义存放消息内容的字节数组
             dis.readFully(data);                      //读取消息内容
             String msg = new String(data, "utf-8");            //消息内容

             System.out.println("接收消息类型" + type);
             System.out.println("接收消息长度" + totalLen);
             System.out.println("发来的内容是:" + msg);
             if(type == 0) {
            	 System.out.println("hi");
            	 new SendThread(clientinfo, "hello", 0).start();
             }
             if(type == 1) {
            	 System.out.println(client.getRemoteSocketAddress().toString() + "断开连接");
            	 
            	 if(out!=null) {
            		 out.flush();
            		 out.close();
            	 }
            	 if(dis!=null) {
            		 dis.close();
            	 }
            	 if(client!=null) {
            		 client.close();
            		 dic.remove(clientip);
            	 }
            	 
            	 File file = new File("d:\\hechengsocket.txt");
            	 if(!file.exists()) {
            		 file.createNewFile();
            	 }
            	 
            	 OutputStream fileoutput = new FileOutputStream(file);
            	 byte up_data[] = new Gson().toJson(studentlist).getBytes();
            	 fileoutput.write(up_data);
 				 fileoutput.close();
 				 
            	 break;
             }
             if(type == 2) {
            	 String jsonmsg = new Gson().toJson(studentlist);
            	 System.out.println("开始发送"+jsonmsg);
            	 new SendThread(clientinfo, jsonmsg, 2).start();;
             }
             if(type == 3) {
            	 Student up_student = new Gson().fromJson(msg, Student.class);
            	 for(Student student : studentlist) {
            		 if(student.getId().equals(up_student.getId())) {
            			 student.setName(up_student.getName());
            			 student.setMajor(up_student.getMajor());
            			 student.setClassid(up_student.getClassid());
            		 }
            	 }
            	 System.out.println("已更改"+new Gson().toJson(studentlist));
             }
             
             if(type == 4) {
            	 Iterator<Student> it = studentlist.iterator();    
            	    while(it.hasNext()){  
            	        Student stu = (Student)it.next();   
            	        if (stu.getId().equals(msg)) {  
            	            it.remove(); 
            	        }  
            	    }  
            	 System.out.println("已删除"+new Gson().toJson(studentlist));
             }
             if(type == 5) {
            	 Student add_student = new Gson().fromJson(msg, Student.class);
            	 studentlist.add(add_student);
            	 System.out.println("已添加"+new Gson().toJson(studentlist));
             }
             
         }
     }
}