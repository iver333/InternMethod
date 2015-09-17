package test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Just {

	public static void main(String[] args) throws IOException {	
		String s = "ctx.drawImage(img,10,10);";
		if(s.matches(".*[drawImage][(].*[)].*")){
			System.out.println("cao a ");
		}
		
		
		/*	try{
				CookieManager manager=new CookieManager();
				manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
				CookieHandler.setDefault(manager);
				URL	url=new URL("http://xw.qq.com/index.htm");
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();
				conn.getHeaderFields();
				CookieStore store = manager.getCookieStore();		
				List<HttpCookie> lCookies=store.getCookies();
				System.out.printf("共%s个cookie\n",lCookies.size());
				for (HttpCookie cookie: lCookies) {
					System.out.printf("原:%s  名称:%s  解码值:%s\n", 
						cookie.toString(),
						cookie.getName(),
						URLDecoder.decode(cookie.getValue(), "UTF8"));
				}
			}catch (Exception e){
				
				e.printStackTrace();
			}*/
		}
	
	public void x(){
		String userAgent = "Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
		try {
			URL obj = new URL("http://www.bing.com");
			URLConnection conn = obj.openConnection();
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			conn.setRequestProperty("User-Agent", userAgent);
			List<String>  ss  = conn.getHeaderFields().get("Set-Cookie");
			System.out.println(ss);
			StringBuffer sb = new StringBuffer();
			
			for(String x:ss){
				sb.append(x);
			}
			float size = (float) (sb.toString().getBytes().length/1024.0);	
			System.out.println(size);
			if (size<1) {
				System.out.println("cao");
			}
		} catch (Exception e) {
		
		}
	}
	}
