import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.net.Socket;

public class SendThread extends Thread{
 
     private SocketInfo socketinfo;
     private String msg;
     private int type;
     public SendThread(SocketInfo socketinfo, String msg, int type) {
    	 this.type = type;
    	 this.socketinfo = socketinfo;
    	 this.msg = msg;
     }
 
     @Override
     public void run() {        
         try {
        	 genProtocol(socketinfo,msg,type);
         } catch (IOException e) {
             e.printStackTrace();
             System.out.println(e.getMessage());
             System.out.println(socketinfo.getClient().isClosed());
         }
         
     }
     
     /**
      * 构造协议
      *
      * @throws IOException
      */
     //0:say hello,2:文件
     private static void genProtocol(SocketInfo socketinfo, String msg, int type) throws IOException {
    	 Socket client = socketinfo.getClient();
         byte[] bytes = msg.getBytes();         //消息内容
         int totalLen = 1 + 4 + bytes.length;   //消息长度
         DataOutputStream outs = socketinfo.getOut();
         //DataOutputStream outs = new DataOutputStream(out);
         
         System.out.println("-----发送"+String.valueOf(client.isClosed())+"-------");
         outs.writeByte(type);                   //写入消息类型
         outs.flush();
         System.out.println("类型" + String.valueOf(type));
         outs.writeInt(totalLen);                //写入消息长度
         outs.flush();
         System.out.println("长度" + String.valueOf(totalLen));
         outs.write(bytes);                      //写入消息内容
         outs.flush();
         System.out.println("内容" + new String(bytes));
         System.out.println("---------发送完毕-------------");

     }
}