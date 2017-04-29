import java.util.*;
import java.net.*;
import java.io.*;

/*
 * Sending Ipv4 Packets 
 */
public class Ipv4Client {
	//some global variables - most don't change
	static byte version;
	static byte hlen;
	static byte tos;
	static int dataSize;
	static int headerSize;
	static byte TTL;
	static byte flag;
	static byte protocol;
	static byte[] dst;
	static byte[] src;
	
	
	    /*
	     * just handles  some of the functions calls 
	     * also creates the data to be sent
	     */
		public static void main(String[] args) throws Exception{
			initInfo();
			try(Socket socket = new Socket("codebank.xyz", 38003)){
			for(int i = 0; i < 12; i++){
				if(socket.isClosed()){
					System.out.println("bad packet. Connection closed");
					break;
				}
				byte[] data = new byte[dataSize];
				new Random().nextBytes(data);
				byte[] packet = createPacket(data);
				sendPacket(socket, packet);
				
				dataSize *= 2;

				}
			}
		}
		
		/*
		 * calculates the check sum
		 */
		public static long checkSum(byte[] b){
			long sum = 0;
			long highVal;
			long lowVal;
			long value;
			
			for(int i = 0; i < 19; i+=2){
				highVal = ((b[i] << 8) & 0xFF00); 
				lowVal = ((b[i + 1]) & 0x00FF);
				value = highVal | lowVal;
	
				sum += value;
			    
			    //check for the overflow
			    if ((sum & 0xFFFF0000) > 0) {
			        sum = sum & 0xFFFF;
			        sum += 1;
			      }
			
			}
			
			sum = ~sum;
			sum = sum & 0xFFFF;
			return sum;
		}
		
		
		/*
		 * puts all of the information together in an Ipv4 format
		 */
		private static byte[] createPacket(byte[] data) throws Exception{
			byte[] packet = new byte[20 + dataSize];
			byte finalV = (byte)(version << 4 | hlen);
			packet[0] = finalV;
			
			packet[1] = tos;
			
			int tempLen = headerSize + dataSize;
			System.out.println("Sending packet with data size: " + dataSize);
			packet[2] = (byte) ((tempLen >> 8) & 0xFF);
			packet[3] = (byte) (tempLen & 0xFF);
			
			packet[4] = 0;
			packet[5] = 0;
			packet[6] = flag;
			packet[7] = 0;
			packet[8] = TTL;
			packet[9] = protocol;
			
			for(int i = 12, j = 0; i < 16; i++, j++){
				packet[i] = src[j];
			}
			
			for(int i = 16, j = 0; i < 20; i++, j++) {
				packet[i] = dst[j];
			}
			
			for(int i = 20, j = 0; j < data.length; i++, j++){
				packet[i] = data[j];
			}
			
			short check = (short)checkSum(packet);
			byte[] asArray = new byte[2];
	        asArray[0] = (byte)((check & 0xFF00) >>> 8);
	        asArray[1] = (byte)((check & 0x00FF));
	        
	        packet[10] = asArray[0];
	        packet[11] = asArray[1];
			return packet;
			
			
		}
		
		/*
		 * sends the packets
		 */
		private static void sendPacket(Socket socket, byte[] packet) throws Exception{
			
				OutputStream out = socket.getOutputStream();
				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				out.write(packet);
				String message = "";
				System.out.println( message = br.readLine());
				if(!message.equals("good")){
					socket.close();
					
				}
				System.out.println();
				
			
		}
		
		/*
		 * just some info that will be constant 
		 */
		private static void initInfo() throws Exception{
			version = 4;
			hlen = 5;
			tos = 0;
			dataSize = 2;
			headerSize = 20;
			flag = 2 << 5;
			TTL = 50;
			protocol = 6;
			
			try(Socket socket = new Socket("codebank.xyz", 38003)){
				InetAddress address = socket.getInetAddress();
				dst = address.getAddress();
			}
			
			src = new byte[4];
			src[0] = 127;
			src[1] = 0;
			src[2] = 0;
			src[3] = 1;
			
			
		}		
		
}



