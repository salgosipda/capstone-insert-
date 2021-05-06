package kr.ac.hansung.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Estate {
	
	//아이디
	private String Id;
	//건물이름
	private String Name;
	//전/월세 정보
	private String Type;
	//가격
	private String Price;
	//주소
	private String Address;
	//경도
	private String x_coord;
	//위도
	private String y_coord;
	

	
	
	
}