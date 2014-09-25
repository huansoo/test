/**  
* @Title:  SimpleSocketClient.java
* @Package com.wugu
* @Description: gochat`s client for java
* @author yangch
* @date  2014-9-23 
* @version V1.0  
* Update Logs:
* ****************************************************
* Name:
* Date:
* Description:
******************************************************
*/
package com.wugu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @ClassName: SimpleSocketClient
 * @Description: main class
 * @author yangch
 * @date 2014-9-23 
 *
 */
public class SimpleSocketClient
{
    public static final int SOCKET_PORT = 7005;
    public static final String SERVER_IP = "121.40.104.140";

    public static Socket socket = null;
    public static InputStream in = null;
    public static OutputStream out = null;

    public static void main(String[] args)
    {
        try
        {
            //建立连接
            socket = new Socket(SERVER_IP, SOCKET_PORT);

            //发送数据
            out = socket.getOutputStream();
            // out.write("hello".getBytes());
            
            //接收数据流
            in = socket.getInputStream();
            byte[] recvData = new byte[1024];
            
            byte[] buff = null;
            
            //存放消息头的数组
            byte[] header = new byte[4];
            
            //存放消息类型的数组
            byte kind = 0;
            
            //存放消息体
            byte[] body = null;
            int i = 0;
            
            // 连接后请求握手
            req_0()

            while(true){
                int n = in.read(recvData);
                System.out.println("-------------");
                buff = plusByteArray(buff,  Arrays.copyOfRange(recvData, 0, n));
                if(buff.length < 5){
                    continue;
                }
                kind = buff[4];
                header = Arrays.copyOfRange(buff, 0, 4);
                //本次消息传输的长度,不包括header的 4bytes, 但是包括kind的1byte
                int length = bytesToInt32(header);
                
                if(buff.length < length + 4){
                    continue;
                }

                //只将本次消息字节数组存放到body中
                body = Arrays.copyOfRange(buff, 5, length);

                //如果此次传输的不仅仅是本次的消息内容，则将接收的下个消息内容保留在buff数组中.
                buff = Arrays.copyOfRange(buff, length+4, buff.length);
                
                // 这里表示解析玩一条完整的消息了,调用消息处理函数
                handle(length, kind, body);
                System.out.println(i++);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());;
        }finally{
            try{
                if(null != in) in.close();
                if(null != out) out.close();
                if(null != socket) socket.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void handle(int length, byte kind, byte[] body) {
       System.out.println("message:", length, kind, body);
       
       if (kind == 0) {
          byte subtype = byte[0]
          if (subtype == 2) {

          }
       }
    }
    /**
     * 
      * @Title: sendData
      * @Description: 发送消息方法
      * @author yangch
      * @date 2014-9-24 
      * @param kind 消息类型
      * @param msg 消息体
      * @throws
     */
    public static void sendData(byte kind, byte[] body) {
        //消息类型和消息体的长度
        int length = 1 + body.length;
        
        //将消息类型和消息体长度组成的整数转化为数组
        byte[] header = int32ToBytes(length);
        
        byte[] sendMsg = plusByteArray(header, new byte[]{kind});
        sendMsg = plusByteArray(sendMsg, body);
        try
        {
            out.write(sendMsg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } 
    }

    public static void req_0() {
      // 握手消息
      sendData(0, new byte[]{0})
    }
    
    /**
     * 
      * @Title: plusByteArray
      * @Description: 将量数组中元素拼接成一个数组
      * @author yangch
      * @date 2014-9-24 
      * @param arr1
      * @param arr2
      * @return
      * @throws
     */
    public static byte[] plusByteArray(byte[] arr1, byte[] arr2){
        
        if(null != arr1 && null != arr2){
            byte[] total = new byte[arr1.length+arr2.length];
            System.arraycopy(arr1, 0, total, 0, arr1.length);
            System.arraycopy(arr2, 0, total, arr1.length, arr2.length);
            return total;
        }
        
        if(null == arr1) return arr2;
        if(null == arr2) return arr1;
        
        return null;
    }
   
    /**
     * 
      * @Title: bytesToInt32
      * @Description: 字节数组转成int整数，该整数表示本次消息的长度
      * @author yangch
      * @date 2014-9-24 
      * @param by
      * @return
      * @throws
     */
    private static int bytesToInt32(byte[] by){
        int t = by[0];

        t = t << 24;
        t = t | (by[1]&0xff) << 16;
        t = t | (by[2]&0xff) << 8;
        t = t | (by[3]&0xff);

        return t;
    }
    /**
     * 
      * @Title: int32ToBytes
      * @Description: 将整数转为byte数组
      * @author yangch
      * @date 2014-9-24 
      * @param num
      * @return
      * @throws
     */
    private static byte[] int32ToBytes(int num){
        byte[] by = new byte[4];
        by[0] = (byte)(num >> 24);
        by[1] = (byte)(num >> 16);
        by[2] = (byte)(num >> 8);
        by[3] = (byte)(num);
        return by;
    }
}
