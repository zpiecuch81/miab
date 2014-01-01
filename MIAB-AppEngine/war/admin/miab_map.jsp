<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.GeoPt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <style type="text/css">
      html { height: 100% }
      body { height: 100%; margin: 0; padding: 0 }
      #map-canvas { height: 100%; width: 90% }
    </style>
    <script type="text/javascript"
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDRtJqA45IVaELlnRSP11qIeCGfkr0s3HA&sensor=false">
    </script>
    <script type="text/javascript">
      function initialize() {
        var mapOptions = {
          center: new google.maps.LatLng(50.4547996521, 18.9226970673),
          zoom: 10
        };
        var map = new google.maps.Map(document.getElementById("map-canvas"),
            mapOptions);
<%
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //Key guestbookKey = KeyFactory.createKey("MIAB");
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the MIABs.
    Query query = new Query("MIAB");
    List<Entity> miabs = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(50));
    if (miabs.isEmpty()) {
    } else {
        int i = 0;
        for (Entity miab : miabs) {
            String message = (String)(miab.getProperty("message"));
            message = message.replaceAll("\n", ";");
            pageContext.setAttribute("miabMessage",message);
            GeoPt point = (GeoPt)miab.getProperty("location");
            float lat = point.getLatitude();
            pageContext.setAttribute("strLat", Float.toString(lat));
            float lng = point.getLongitude();
            pageContext.setAttribute("strLng", Float.toString(lng));
            ++i;
            String stringI=Integer.toString(i);
        %>
            var myLatlng = new google.maps.LatLng(${strLat}, ${strLng});
            var marker${stringI} = new google.maps.Marker({
                position: myLatlng,
                map: map,
                title:"${fn:escapeXml(miabMessage)}" });
        <%
      }
    }
%>
      }
      google.maps.event.addDomListener(window, 'load', initialize);
    </script>
  </head>
  <body>
    <div id="map-canvas"/>
  </body>
</html>