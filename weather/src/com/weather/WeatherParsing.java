package com.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherParsing {
    public static void main(String[] args) {
    	System.out.println("## 프로그램 시작 ##");
    	
        double gangSu = 0.0;
        
        // 일기예보 확인
        final String CATEGORY_POP = "POP"; // 강수량만 필요함으로 final 변수로 선언

        // 조회를 원하는 지역의 경도 위도 - 서울 중구 기준 60 127
        String nx = "60"; // 경도
        String ny = "127"; // 위도

        // 현재 날짜 가져오는 힘수
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 날짜 데이터 포멧 형태지정
        String todayDate = sdf.format(cal.getTime());

        String alamTime = "0800"; // 조회하고 싶은 시간대 지정
        // 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회 제한)
        
        String serviceKey = "gjacaWQfv4ZtfFSgJrq5FaCGNJrQCVWaila1%2F%2BY66oOEUkrrmMh7YgG57jCakEhXN1C1PA2r5zmISvqkZYqq%2FQ%3D%3D";
        // 공공데이터 포털에서 제공해준 서비스 키
        
        // 정보를 모아서 URL정보를 만들면됩니다. 맨 마지막 "&_type=json"에 따라 반환 데이터의 형태가 정해집니다.
        String gangsuUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?"
                + "serviceKey=" + serviceKey + "&base_date=" + todayDate + "&base_time=" + alamTime + "&nx=" + nx
                + "&ny=" + ny + "&_type=json";
        
        try {
            URL url = new URL(gangsuUrl); // 위 urlStr을 이용해서 URL 객체를 만들어줍니다.
            BufferedReader br;
            String line = "";
            String result = "";

            br = new BufferedReader(new InputStreamReader(url.openStream()));

            while ((line = br.readLine()) != null) {
                result = result.concat(line);
            }

            // Json parser를 만들어 만들어진 문자열 데이터를 객체화 합니다.
            JSONParser parser = new JSONParser();

            JSONObject obj = (JSONObject) parser.parse(result);
            
            // Top레벨 단계인 response 키를 가지고 데이터를 파싱합니다.
            JSONObject wt_response = (JSONObject) obj.get("response");
            // response 로 부터 body 찾아옵니다.
            JSONObject wt_body = (JSONObject) wt_response.get("body");
            // body 로 부터 items 받아옵니다.
            JSONObject wt_items = (JSONObject) wt_body.get("items");

            // items로 부터 itemlist 를 받아오기 itemlist : 뒤에 [ 로 시작하므로 jsonarray이다
            JSONArray wt_item = (JSONArray) wt_items.get("item");

            String category; // 강수량인 POP만 꺼내오려고 사용
            JSONObject weather; // 배열 형태의 구조에서 하나씩 데이터를 꺼내올때 담을 변수

            for (int i = 0; i < wt_item.size(); i++) {
                weather = (JSONObject) wt_item.get(i);
                double value = (Double.parseDouble(weather.get("fcstValue").toString())); // 실수로된 값과 정수로된 값이 둘다 있어서 실수로

                category = (String) weather.get("category");

                if (category.equals(CATEGORY_POP)) {
                    gangSu = value;
                }
            }
            String rs = WeatherUtil.sendTelegram(WeatherUtil.makeMsg(gangSu));
            System.out.println("## 일기예보 종료 ##");

            br.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        
        
        // 미세먼지 체크
        
        String sido = "서울";  
        
        String urlMise = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?sidoName=" + sido + "&pageNo=1&numOfRows=10&ServiceKey=" + serviceKey + "&ver=1.3&_returnType=json";
        
        try {
            URL url = new URL(urlMise); // 위 urlStr을 이용해서 URL 객체를 만들어줍니다.
            BufferedReader br;
            String line = "";
            String result = "";

            br = new BufferedReader(new InputStreamReader(url.openStream()));

            while ((line = br.readLine()) != null) {
                result = result.concat(line);
            }
            
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(result);
            
            JSONArray item = (JSONArray) obj.get("list");
            JSONObject miseDosi = (JSONObject) item.get(0);
            // 공공데이터 포탈에서 던져주는 데이터가 배열형태로 도시대기인 0번째 값을 기준으로 함
            
            int miseGrade = Integer.parseInt((String)miseDosi.get("pm10Grade"));
            double miseValue = Double.parseDouble((String)miseDosi.get("pm10Value"));
            
            String rs = WeatherUtil.sendTelegram(WeatherUtil.makeMsg(miseGrade, miseValue));
            System.out.println("## 미세먼지 종료 ##");

            br.close();


        }catch (Exception e){
            e.printStackTrace();
        }
       
    }

}