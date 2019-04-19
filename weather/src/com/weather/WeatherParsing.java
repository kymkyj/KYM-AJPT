package com.weather;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WeatherParsing {  
	public static void main(String[] args) {
		System.out.println("## 프로그램 시작 ##");  
		double gangSu = 0.0;  

		// 일기예보 확인
		final String CATEGORY_POP = "POP";

		// 서울 중구 기준 60 127
		String nx = "60";
		String ny = "127";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
		String todayDate = sdfDate.format(cal.getTime());

		String alamTime = "0200"; // 조회하고 싶은 시간대 지정
		// 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회 제한)

		String serviceKey = "gjacaWQfv4ZtfFSgJrq5FaCGNJrQCVWaila1%2F%2BY66oOEUkrrmMh7YgG57jCakEhXN1C1PA2r5zmISvqkZYqq%2FQ%3D%3D";

		String gangsuUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData?"
				+ "serviceKey=" + serviceKey + "&base_date=" + todayDate + "&base_time=" + alamTime + "&nx=" + nx
				+ "&ny=" + ny + "&_type=json";

		JSONObject obj = WeatherUtil.getJsonObjectByURL(gangsuUrl);
		
		JSONObject wtResponse = (JSONObject) obj.get("response");
		JSONObject wtBody = (JSONObject) wtResponse.get("body");
		JSONObject wtItems = (JSONObject) wtBody.get("items");
		JSONArray wtItem = (JSONArray) wtItems.get("item");
		System.out.println("## 일기예보 데이터 가져옴 ##");

		String category;
		JSONObject weather;

		for (int i = 0; i < wtItem.size(); i++) {
			weather = (JSONObject) wtItem.get(i);
			double value = (Double.parseDouble(weather.get("fcstValue").toString()));

			category = (String) weather.get("category");

			if (category.equals(CATEGORY_POP)) {
				gangSu = value;
			}
		}

		// 미세먼지 체크
		String sido = "서울";

		try {
			sido = URLEncoder.encode(sido, "UTF8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String urlMise = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?sidoName="
				+ sido + "&pageNo=1&numOfRows=10&ServiceKey=" + serviceKey + "&ver=1.3&_returnType=json";

		JSONObject miseObj = WeatherUtil.getJsonObjectByURL(urlMise);
		
		JSONArray item = (JSONArray) miseObj.get("list");
		JSONObject miseDosi = (JSONObject) item.get(0);

		int miseGrade = Integer.parseInt((String) miseDosi.get("pm10Grade"));
		double miseValue = Double.parseDouble((String) miseDosi.get("pm10Value"));
		System.out.println("## 미세먼지 데이터 가져옴 ##");

		WeatherUtil.sendTelegram(WeatherUtil.makeMsg(gangSu, miseGrade, miseValue));

		System.out.println("## 종료 ##");
	}
}