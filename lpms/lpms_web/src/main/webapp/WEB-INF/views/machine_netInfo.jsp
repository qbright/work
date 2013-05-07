<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${netInfo_list}" var="net">
  <div class="well">
  		<div class="netInfo_title" >${net.description }</div>
    	<div class="netInfo_container">
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>Mac Adress</dt>
    				<dd>${net.macAdress }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt></dt>
    				<dd></dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>Ip Adress</dt>
    				<dd>${net.ipAddress }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>Net Mask</dt>
    				<dd>${net.netMask }</dd>
    			</dl>
    		</div>
    		
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>rxPackets</dt>
    				<dd>${net.rxPackets }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>txPackets</dt>
    				<dd>${net.txPackets }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>rxBytes</dt>
    				<dd>${net.rxBytes }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>txBytes</dt>
    				<dd>${net.txBytes }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>rxErrors</dt>
    				<dd>${net.rxErrors }</dd>
    			</dl>
    		</div>
    		<div class="container2">
    			<dl class="dl-horizontal">
    				<dt>txErrors</dt>
    				<dd>${net.txErrors }</dd>
    			</dl>
    		</div>
    	</div>
	</div> 
</c:forEach>