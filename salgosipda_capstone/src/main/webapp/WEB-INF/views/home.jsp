
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<title>간단한 지도 표시하기</title>
<script type="text/javascript"
	src="https://openapi.map.naver.com/openapi/v3/maps.js?ncpClientId=0bmmzwd5sj">
</script>
<style type="text/css">
@charset "EUC-KR";
.green_window {
	display: inline-block;
	width: 300px;
	border: 3px solid #2db400;
}
.input_text {
	width: calc( 100% - 14px );
	margin: 6px 7px;
	border: 0;
	font-weight: bold;
	font-size: 16px;
	outline: none;
}
.sch_smit {
	width: 54px; height: 40px;
	margin: 0; border: 0;
	vertical-align: top;
	background: #22B600;
	color: white;
	font-weight: bold;
	border-radius: 1px;
	cursor: pointer;
}
.sch_smit:hover {
	background: #56C82C;
}
</style>
<link rel="stylesheet" type="text/css" href="style.css">

</head>
<body>
	<div id="filter" style="width:25%; float: left">
		<span class='green_window'> <input type='text'
			class='input_text' />
		</span>
	<button type='submit' class='sch_smit'>검색</button>
	</div>
	<div id="map" style="width: 75%; height: 100%; float: right"></div>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

	<script>
		//매물 객체 배열
		var Estates = new Array();

		//매물 객체 생성자
		var Estate = function(id, name, type, price, address, x_coord, y_coord) {
			this.id = id;
			this.name = name;
			this.type = type;
			this.price = price;
			this.address = address;
			this.x_coord = x_coord;
			this.y_coord = y_coord;
		}

		//JSTL의 객체 배열을 javascript 변수로 바꾸는 과정
		<c:forEach var="estate" items="${estates}">
		var estate = new Estate('<c:out value="${estate.id}"/>',
				'<c:out value="${estate.name}"/>',
				'<c:out value="${estate.type}"/>',
				'<c:out value="${estate.price}"/>',
				'<c:out value="${estate.address}"/>',
				'<c:out value="${estate.x_coord}"/>',
				'<c:out value="${estate.y_coord}"/>');
		Estates.push(estate);
		</c:forEach>

		//지도 생성
		var map = new naver.maps.Map('map', {
			zoom : 10,
			center : new naver.maps.LatLng(37.5880552, 127.005787)
		});

		var CustomOverlay = function(options) {
			this._element = $()
			this.setPosition(options.position);
			this.setMap(options.map || null);
		};

		CustomOverlay.prototype = new naver.maps.OverlayView();
		CustomOverlay.prototype.constructor = CustomOverlay;

		CustomOverlay.prototype.setPosition = function(position) {
			this._position = position;
			this.draw();
		};

		CustomOverlay.prototype.setElement = function(element) {
			this._element = element;
			this.draw();
		};

		CustomOverlay.prototype.getPosition = function() {
			return this._position;
		};

		CustomOverlay.prototype.onAdd = function() {
			var overlayLayer = this.getPanes().overlayLayer;

			this._element.appendTo(overlayLayer);
		};

		CustomOverlay.prototype.draw = function() {
			if (!this.getMap()) {
				return;
			}

			var projection = this.getProjection(), position = this
					.getPosition(), pixelPosition = projection
					.fromCoordToOffset(position);

			this._element.css('left', pixelPosition.x);
			this._element.css('top', pixelPosition.y);
		};

		CustomOverlay.prototype.onRemove = function() {
			var overlayLayer = this.getPanes().overlayLayer;

			this._element.remove();
			this._element.off();
		};

		//마커 배열
		var markerList = new Array();
		var infoList = new Array();

		//지도에 현재 표시되고 있는 구역 바운드
		var rect = new naver.maps.Rectangle({
			strokeOpacity : 0,
			strokeWeight : 0,
			fillOpacity : 0.2,
			fillColor : "#f00",
			visible : false, //보이지 않도록 설정
			bounds : map.getBounds(), // 지도의 bounds와 동일한 크기의 사각형을 그립니다.
			map : map
		});

		//지도에 표시되고있는 구역이 변할시 발동하는 이벤트 리스너
		naver.maps.Event
				.addListener(
						map,
						"bounds_changed",
						function(bounds) {
							window.setTimeout(function() {
								rect.setBounds(bounds);
							}, 500);

							//줌이 15이상이고
							if (map.zoom >= 17) {
								for (let i = 0; i < Estates.length; i++) {
									//바운드 안에 들어있을때만 매물 표시
									if (Estates[i].x_coord < bounds.getMax().x
											&& Estates[i].y_coord < bounds
													.getMax().y
											&& Estates[i].x_coord > bounds
													.getMin().x
											&& Estates[i].y_coord > bounds
													.getMin().y) {
										var x = Number(Estates[i].x_coord);
										var y = Number(Estates[i].y_coord);

										var marker = new naver.maps.Marker({
											position : new naver.maps.Point(x,
													y),
											map : map
										});

										markerList.push(marker);

										var overlay = new CustomOverlay(marker);

										overlay
												.setElement($('<div style="position:absolute;left:0;top:0;height:55px;width:50px;font-size:10px;background-color:#F2F0EA;text-align:center;border:2px solid #6C483B;">'
														+ '<span style="font-weight: bold;"> '
														+ Estates[i].price
														+ '<br>'
														+ Estates[i].type
														+ ' </span>' + '</div>'));

										overlay.setMap(map);

										//infowindow.open(map, marker);
										infoList.push(overlay);
										//infoCoordList.push(info_coord);
									}
								}
								//	for(let i = 0; i < infoList.length; i++){
								//		infoList[i].open(map, infoCoordList[i]);
								//	}

								//바운드를 넘어가면 보이지 않는다.
								for (let i = 0; i < markerList.length; i++) {
									if (markerList[i].position.x > bounds
											.getMax().x
											|| markerList[i].position.y > bounds
													.getMax().y
											|| markerList[i].position.x < bounds
													.getMin().x
											|| markerList[i].position.y < bounds
													.getMin().y) {
										markerList[i].setMap(null);
										infoList[i].setMap(null);
									}
								}
								//줌 아웃을 하면 사라진다
							} else if (map.zoom < 17) {
								for (let i = 0; i < markerList.length; i++) {
									markerList[i].setMap(null);
									delete markerList[i];
									infoList[i].setMap(null);
								}
								markerList = new Array();
								infoList = new Array();
							}

							console.log('zoom: ' + map.zoom + ' Max x: '
									+ bounds.getMax().x + ' Max y: '
									+ bounds.getMax().y + ' Min x: '
									+ bounds.getMin().x + ' Min y: '
									+ bounds.getMin().y);

						});
	</script>
</body>
</html>