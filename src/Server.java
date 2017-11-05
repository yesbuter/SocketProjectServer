
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by hecheng on 16/12/1.
 */
public class Server {
	
    public static void main(String[] args) {
    	//读取txt文件（json格式）
    	//如果文件不存在就创建文件并输入第一个学生
    	String text ="";
    	File file = new File("d:\\hechengsocket.txt");
    	if(!file.exists()) {
    		try {
				file.createNewFile();
				OutputStream fileoutput = new FileOutputStream(file);
				String firstStudent = "[{\"id\":\"M201776043\";\"name\":\"何诚\";\"major\":\"SortWareEngineer\";\"classid\":\"1701\"},"
						+ "{\"id\":\"M201776042\";\"name\":\"hecheng\";\"major\":\"SortWareEngineer\";\"classid\":\"1702\"},"
						+ "{\"id\":\"M201776041\";\"name\":\"echo_c\";\"major\":\"SortWareEngineer\";\"classid\":\"1703\"}]";
				byte data[] = firstStudent.getBytes();
				fileoutput.write(data);
				fileoutput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    	}
    	else {
    		try {
				InputStream fileinput = new FileInputStream(file);
	    		byte data[] = new byte[1024];
	    		int byteread = 0;
	    		StringBuffer stringbuff = new StringBuffer();
	    		while((byteread=fileinput.read(data))!=-1) {
	    			stringbuff.append(new String(data, 0, byteread));
	    		}
	    		text = stringbuff.toString();
	    		
	    		System.out.println(text);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    	}
    	//用Gson解析为对象List
    	Gson gson = new Gson();
    	List<Student> studentlist = gson.fromJson(text, new TypeToken<List<Student>>(){}.getType());
    	
    	Map<String, SocketInfo>  dic = new HashMap<String, SocketInfo>();
    	ServerSocket ss = null;
        try{
            System.out.println("create server socket....");
            ss = new ServerSocket(1995);
            System.out.println(ss.getLocalSocketAddress().toString()+"wait for a connection....");
            while(true)     //服务器端一直监听这个端口，等待客户端的连接
            {
            	Socket client = ss.accept();  //当有客户端连接时，产生阻塞
            	SocketInfo clientsocket = new SocketInfo(client);
            	dic.put(client.getRemoteSocketAddress().toString(), clientsocket);
            	//dic.put(client.getInetAddress().toString(), client);
            	System.out.println("客户端" + client.getRemoteSocketAddress().toString() + "连接成功");            	
            	new ReceiveThread(dic, client.getRemoteSocketAddress().toString(), studentlist, text).start();//新建一个socketThread处理这个客户端的socket连接
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        finally{
          try{
              if(ss != null){
                  ss.close();
              }
              //从字典中删除
              for (Map.Entry<String, SocketInfo> entry : dic.entrySet()) {  
            	  if(entry.getValue().getClient()!=null) {
            		  entry.getValue().getClient().close();
            		  entry.setValue(null);
            	  }
              }  
          }
          catch(Exception ex){
              System.out.println(ex.getMessage());
          }
      }
    }
}