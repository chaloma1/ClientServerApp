<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!doctype html>
<html lang="en">
<head>
<style>
    body{text-align: center; height:100%; width:100%;background-color: darkslategray;}
    #mainDiv {background-color:white;border-radius:10px;width: 90%; min-height:100vh; margin: 0 auto; display: block; }

    #graphs{display: flex; border-top: 2px solid black;flex-wrap: wrap;}

    .child{flex: 1; border: 2px solid black; margin-top: 1%; margin:0.5%;}

    #select {min-width: 10%;}
    input {min-width: 10%;min-height: 10%; margin: 1%;}
    h1 {margin-top:0px;}

    table {
        font-family: arial, sans-serif;
        border-collapse: collapse;
        width: 100%;


    }

    td, th {
        border: 1px solid #dddddd;
        text-align: left;
        padding: 8px;
    }
    #dangerousUsageTable {margin: 0.5%;}



</style>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <c:if test="${systemUsageData.size() > 0}">
    <script type="text/javascript">
        google.charts.load('current', {'packages':['corechart']});
        google.charts.setOnLoadCallback(drawChart);

        function drawChart() {
            var dataCPU = google.visualization.arrayToDataTable([
                ['Time', 'CPU'],
                <c:forEach var="usage" items="${systemUsageData}">
                ['<fmt:formatDate pattern="HH:mm:ss" value="${usage.getDateOfMeasurement()}"/> ' , ${usage.getProcessorUsage()}],
                </c:forEach>
            ]);

            var dataMemory = google.visualization.arrayToDataTable([
                ['Time', 'Memory'],
                <c:forEach var="usage" items="${systemUsageData}">
                ['<fmt:formatDate pattern="HH:mm:ss" value="${usage.getDateOfMeasurement()}"/> ' , ${usage.getUsedMemory()}],
                </c:forEach>
            ]);

            var optionsCPU = {
                title: 'CPU usage in %',
                curveType: 'function',
                backgroundColor: '#E4E4E4',
                legend: { position: 'bottom'}
            };

            var optionsMemory = {
                title: 'Memory usage in GB',
                curveType: 'function',
                backgroundColor: '#E4E4E4',
                legend: { position: 'bottom' }
            };

            var chartCPU = new google.visualization.LineChart(document.getElementById('curve_chart_CPU'));
            var chartMemory = new google.visualization.LineChart(document.getElementById('curve_chart_Memory'));

            chartCPU.draw(dataCPU, optionsCPU);
            chartMemory.draw(dataMemory, optionsMemory);
        }
    </script>
    </c:if>

</head>
<body>



<div id="mainDiv">

    <h1>Toto je test! ;)</h1>

   <!-- <c:forEach var="usage" items="${systemUsageData}">
        ${usage.getLocation()} </br>
        ${usage.getProcessorUsage()} </br>
        ${usage.getFreeMemory()} </br>
        ${usage.getDateOfMeasurement()} </br>
    </c:forEach>
    -->
    <div id="content">

        <form method="GET" action="./showUsage" >
            <label for="select">Choose a hostname:</label>

            <select name="hostName" id="select">
                <option selected disabled hidden>here</option>
                <c:forEach var="hostName" items="${hostNames}">
                <option value="${hostName}">${hostName}</option>
                </c:forEach>
            </select>
            </br>
            <input type="submit" value="Submit">
        </form>

    </div>



    <c:if test="${systemUsageData.size() > 0}">
    <H2>HW usage graphs for ${systemUsageData.get(0).getHostName()}</H2>
    <div id="graphs">


             <div class="child" id="curve_chart_CPU" style="width: 80%; min-height: 500px"></div>
             <div class="child" id="curve_chart_Memory" style="width: 80%; min-height: 500px"></div>


    </div>
    </c:if>


    <div id="dangerousUsageTable">
        <H2>Dangerous HW usage reports</H2>
        <c:if test="${dangerousUsages.size() > 0}">
        <table>
            <tr>
                <th>Device hostname</th>
                <th>Date of measurement</th>
                <th>CPU usage</th>
                <th>Free Memory left</th>
            </tr>
            <c:forEach var="usage" items="${dangerousUsages}">
            <tr>
                <td>${usage.getHostName()}</td>
                <td>${usage.getDateOfMeasurement()}</td>
                <td>${usage.getProcessorUsage()}%</td>
                <td>${usage.getFreeMemory()}GB</td>
            </tr>
            </c:forEach>

        </table>
        </c:if>
    </div>
</div>

</body>
</html>