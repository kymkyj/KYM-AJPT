package com.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherUtil {
    static String sendTelegram(String msg){
        try {
            String telegramUrl = "https://api.telegram.org/bot785529566:AAGJgIYz_uzbcogAVrrUEBjkq9gdxXF1JzU/sendmessage?chat_id=639995102&text=";

            URL url = new URL(telegramUrl + URLEncoder.encode(msg, "UTF8"));

            StringBuilder result = new StringBuilder();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            br.close();

            return result.toString();
        }catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }

    static String makeMsg(double gangSu){
        String result = ""; // 조건에 따라 던져줄 메시지를 넣을 변수
        result += "오늘의 강수확률은 " + gangSu + "% 입니다. \n";

        if(gangSu >= 40.0){ // 강수량이 40.0 이상일 경우 아래 내용 수행
            result += "우산을 준비하세요! \n";
        }

        return result;
    }

}