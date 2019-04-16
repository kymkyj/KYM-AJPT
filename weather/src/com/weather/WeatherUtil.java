package com.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherUtil {
	static JSONObject getJsonObjectByURL(String urlStr) {
		try {
			URL url = new URL(urlStr);
			BufferedReader br;
			String line = "";
			String result = "";
			
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			
			while((line = br.readLine()) != null) {
				result = result.concat(line);
			}
			
			JSONParser parser = new JSONParser();
			
			JSONObject obj = (JSONObject) parser.parse(result);
			br.close();
			
			return obj;
			
		}catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		
	}
	
    static void sendTelegram(String msg){
        try {
            String telegramUrl = "https://api.telegram.org/bot785529566:AAGJgIYz_uzbcogAVrrUEBjkq9gdxXF1JzU/sendmessage?chat_id=639995102&text=";

            URL url = new URL(telegramUrl + URLEncoder.encode(msg, "UTF8"));

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            br.close();
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static String makeMsg(double gangSu, int miseGrade, double miseValue){
        String result = ""; 
        result += "오늘의 강수확률은 " + gangSu + "% 입니다. \n";

        if(gangSu >= 40.0){ 
            result += "우산을 준비하세요! \n";
        }
        
        result += "오늘의 미세먼지 수치는 " + miseValue + "입니다. \n";
        
        if(miseGrade >= 3) { 
        	result += "미세먼지가 나쁘니, 마스크를 준비하세요!";
        }
        
        return result;
    }
}