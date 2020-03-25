<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Employee</title>
</head>

<body>
	<h3 style="color: red;">Enter TOTP</h3>

	<div id="addEmployee">
		<form:form action="/validate" method="post" modelAttribute="totp">
			<p>
				<label>Enter OTP</label>
				<form:input path="otp" />
			</p>
			
			<input type="SUBMIT" value="Submit" />
		</form:form>
	</div>
</body>
</html>
