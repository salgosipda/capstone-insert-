package kr.ac.hansung.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.ac.hansung.dao.EstateDao;
import kr.ac.hansung.model.Estate;

@Service
public class EstateService {
	@Autowired
	private EstateDao estateDao;

	// DB 저장
	public void insert() throws IOException, ParseException {

		Estate estate = new Estate();

		for (int j = 1; j <=2; j++) {
			ArrayList<Element> elements = new ArrayList<>();
			ArrayList<String> id = new ArrayList();
			String nl = "";
			String pl = "";
			String il = "";
			String il2 = "";
			String tl = "";
			String al = "";
			String xl = "";
			String yl = "";
			String detailURL = "";
			String spl[];
			BufferedImage img = null;
			String imgl = "";

			// 매물 목록
			String EstateURL_1 = "http://land.mk.co.kr/memul/list.php?bubcode=1129000000&mgroup=G&bdiv=A&page=";
			String EstateURL_2 = String.valueOf(j);
			String EstateURL = EstateURL_1 + EstateURL_2;
			// System.out.println(EstateURL);

			Document doc = Jsoup.connect(EstateURL).get();

			Elements elem = doc.select("#SY > div > div > div.MaemulListContent > div.MaemulList > table > tbody ");
			// 아이디 목록
			Elements idlist = elem.select("td.AlignLeft.Name > div > a:nth-child(1)");
			// 매물 타입
			Elements tylist = elem.select("tr:nth-child(2n-1) >td:nth-child(1) > div");
			// 매물 가격
			Elements pricelist = elem.select("strong");

			for (Element e : elem.select("td:nth-child(6) > div > strong")) {
				elements.add(e);
			}

			for (int i = 0; i < elements.size(); i++) {
				il = idlist.get(i).attr("href");
				spl = il.split("'");
				id.add(spl[1]);
			}

			// 매물 상세 정보
			String EstateURL2 = "http://land.mk.co.kr/memul/detail.php?bubcode=1129000000&mgroup=G&mclass=G01&bdiv=A&mseq=";

			for (int i = 0; i < elements.size(); i++) {
				try {
					il2 = id.get(i);
					tl = tylist.get(i).text(); // 전세
					pl = pricelist.get(i).text(); // 가격

					estate.setId(il2);
					estate.setType(tl);
					estate.setPrice(pl);

					// 건물 주소

					detailURL = EstateURL2 + il2; // url 파라미터 인코딩
					Document doc2 = Jsoup.connect(detailURL).get();
					
					Elements imglist = doc2.select(".imgListLi2> a > img");
					if (imglist != null) {

						imgl = imglist.attr("src");
						System.out.println(imgl);
						URL imgUrl = new URL(imgl);

						img = ImageIO.read(imgUrl);

						FileOutputStream out = new FileOutputStream(
								"C:\\dev\\workspace\\salgosipda_capstone (2)\\salgosipda_capstone\\src\\main\\img\\" + il2 + ".jpg");

						// ImageIO.write(저장할 이미지, 저장할 확장자, 저장할 위치)
						ImageIO.write(img, "jpg", out);
					} else {
						URL imgUrl = new URL("C:\\dev\\workspace\\salgosipda_capstone (2)\\salgosipda_capstone\\src\\main\\img\\no_image.jpg");
						System.out.println("fail");
						img = ImageIO.read(imgUrl);
						FileOutputStream out = new FileOutputStream(
								"C:\\dev\\workspace\\salgosipda_capstone (2)\\salgosipda_capstone\\src\\main\\img\\" + il2 + ".jpg");
						ImageIO.write(img, "jpg", out);
					}
					
					
					
					Elements addresslist = doc2.select("#detailMapTitle > h3");
					al = addresslist.text();
					estate.setAddress(al);

					// 주소->위도, 경도
					String jj = getKakaoApiFromAddress(al);
					String[] xy = jj.split(",");
					estate.setX_coord(xy[0]);
					estate.setY_coord(xy[1]);

					// 건물 이름
					Elements namelist = doc2.select(
							"#SY > div > div.ListContainer > div.DetailContent > div.DetailInfo > div > div.InfoSection > div.AddressZone > div.AddressInfo.AddressInfo2 > h2");
					nl = namelist.text();
					estate.setName(nl);

					estateDao.insert(estate); // DB 저장

				} catch (Exception e) {
					i++;
				}
			}
		}

	}

	// DB에서 가져오기
	public List<Estate> getCurrent() {
		return estateDao.getEstate();
	}

	//카카오 지도 api 이용 주소->위도, 경도
	public String getKakaoApiFromAddress(String roadFullAddr) throws ParseException {
		String apiKey = "9297b7f2d4352d7538cfea5c8f4f0126";
		String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
		String jsonString = null;
		String jj = null;
		try {
			roadFullAddr = URLEncoder.encode(roadFullAddr, "UTF-8");

			String addr = apiUrl + "?query=" + roadFullAddr;

			URL url = new URL(addr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Authorization", "KakaoAK " + apiKey);

			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuffer docJson = new StringBuffer();

			String line;

			while ((line = rd.readLine()) != null) {
				docJson.append(line);
			}

			jsonString = docJson.toString();

			JSONParser parser = new JSONParser();
			JSONObject jsonObject;
			JSONObject jsonObject2;
			JSONArray jsonArray;
			String x = "";
			String y = "";

			jsonObject = (JSONObject) parser.parse(jsonString);
			jsonArray = (JSONArray) jsonObject.get("documents");
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObject2 = (JSONObject) jsonArray.get(i);
				if (null != jsonObject2.get("x")) {
					x = (String) jsonObject2.get("x").toString();
				}
				if (null != jsonObject2.get("y")) {
					y = (String) jsonObject2.get("y").toString();
				}

			}

			jj = x + "," + y;

			rd.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jj;
	}

}
