<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%-- <link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/resources/css/main.css"> --%>
<title>Insert title here</title>
</head>
<body>

     <!-- 크롤링한 정보 출력 -->
	<table>
		<tr >
			<td>매물번호</td>
			<td>이름</td>
			<td>타입</td>
			<td>가격</td>
			<td>주소</td>
			<td>경도</td>
			<td>위도</td>
		</tr>
		
		<c:forEach var="estate" items="${estates}">

			<tr>
				<td><c:out value="${estate.id}"></c:out></td>
				<td><c:out value="${estate.name}"></c:out></td>
				<td><c:out value="${estate.type}"></c:out></td>
				<td><c:out value="${estate.price}"></c:out></td>
				<td><c:out value="${estate.address}"></c:out></td>
				<td><c:out value="${estate.x_coord}"></c:out></td>
				<td><c:out value="${estate.y_coord}"></c:out></td>
			</tr>
			
		</c:forEach>
		
	</table>
	
	 
	
	 </body>
</html>