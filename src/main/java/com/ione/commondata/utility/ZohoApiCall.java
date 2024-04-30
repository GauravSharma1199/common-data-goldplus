package com.ione.commondata.utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ZohoApiCall {




//	==========================================PROD=======================================
	public static final String CLIENT_ID = "1000.5J4TZ9FULBE10DW03KB1ZV75WCZHQJ";
	public static final String CLIENT_SECRET = "3440a5904a87cf6d18894c0277f1c075927792f59c";
	//public static final String REFRESH_TOKENN = "1000.def01d1796af6bd50e1c80edba0f077f.855f894aa515de697bebd91d1e9f6d84";

	public static final String REFRESH_TOKENN ="1000.4e14f65e2e893a13d150dece4d1d0fe0.851ea7f8f2f35093eb120ba965fa82a7";
//	public static final String REFRESH_TOKENN ="1000.2f995adc354c94cd9d9d3908a4e27625.405771195f07dc2dde1bf6596ec669e4";

	//public static final String RESOURCE_URL_TPL = "https://www.zohoapis.in/crm/v4/Items/upsert";
	public static final String RESOURCE_URL_TPL ="https://www.zohoapis.in/crm/v5/Items";
	public static final String TOKEN_URL = "https://accounts.zoho.in/oauth/v2/token";
	public static final String RESOURCE_SRM_update ="https://www.zohoapis.in/crm/v5/Sales_Persons";
	public static final String RESOURCE_URL_Dispatch ="https://www.zohoapis.in/crm/v5/Dispatch";
	public static final String RESOURCE_URL_Invoice ="https://www.zohoapis.in/crm/v5/Invoice";
	public static final String RESOURCE_URL_Order ="https://www.zohoapis.in/crm/v5/Deals";
	public static final String RESOURCE_SubForm_Order ="https://www.zohoapis.in/crm/v5/Deals/";
	public static final String RESOURCE_Payment_Creation ="https://www.zohoapis.in/crm/v5/Payment";
	public static final String PDF_UPLOAD_API = "https://creator.zoho.in/api/v2.1/goldplus/gold-plus/form/Customer_Document";
	public static final String CREDIT_DEBIT_NOTE_API ="https://www.zohoapis.in/crm/v4/Credit_Debit_Notes";
	public static final String RESOURCE_Customer_Creation ="https://www.zohoapis.in/crm/v5/Customers";

	public static final String RESOURCE_Stock_Creation ="https://creator.zoho.in/api/v2/goldplus/gold-plus/form/Stocks";
	public static final String RESOURCE_Stock_Deletion ="https://creator.zoho.in/api/v2/goldplus/gold-plus/form/Delete_Stock";



	@Value("${counter.authentication}")
	Integer authenticationCounter;
	@Value("${delay.authentication}")
	Integer authenticationDelay;
	@Value("${token.expiry}")
	Integer tokenExpiry;
	Integer initialTokenCounter = 0;

	private static Map<String, Object> token = new HashMap<>();

	public LocalDateTime addMinutes(LocalDateTime time, int min){
		return time.plusMinutes(min);
	}

	public static void resetTokenMap(){
//		token = new HashMap<>();
	}
	public String getAuthToken() throws OAuthProblemException {

		System.out.println(new Date() + " : Start generating refresh token...");
		OAuthClient client = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest request;
		String accessToken = "";
		if (initialTokenCounter < authenticationCounter) {
			LocalDateTime now = LocalDateTime.now();
			try {
				if (
						token.get("token") != null &&
								token.get("start_time") != null &&
								token.get("end_time") != null &&
								now.isBefore((LocalDateTime) token.get("end_time")) &&
								now.isAfter((LocalDateTime) token.get("start_time"))
				){
					System.out.println("old token: " + token.get("token"));
				}else {
					request = OAuthClientRequest.tokenLocation(TOKEN_URL).setRefreshToken(REFRESH_TOKENN).setClientId(CLIENT_ID)
							.setClientSecret(CLIENT_SECRET).setGrantType(GrantType.REFRESH_TOKEN).buildBodyMessage();
					OAuthJSONAccessTokenResponse oauthResponse = client.accessToken(request, OAuth.HttpMethod.POST);
					token.put("token", oauthResponse.getAccessToken());
					token.put("start_time", LocalDateTime.now());
					token.put("end_time", addMinutes(LocalDateTime.now(), tokenExpiry));
					System.out.println("new token: " + token.get("token"));
				}
				accessToken = (String) token.get("token");
			} catch (OAuthSystemException e) {
				accessToken = (String) token.get("token");
				// TODO Auto-generated catch block
				System.out.println("OAuthSystemException = " + e);
				token = new HashMap<>();
				try {
					TimeUnit.SECONDS.sleep(authenticationDelay);
				}catch (InterruptedException ex){
					accessToken = (String) token.get("token");
					System.out.println("InterruptedException = " + ex);
				}
				System.out.println(new Date() + " : retry count (" + (initialTokenCounter + 1) + ")");
				initialTokenCounter  = initialTokenCounter +  1;
				getAuthToken();
			}
		}else {
			initialTokenCounter = 0;
		}
		accessToken = (String) token.get("token");
		System.out.println("accessToken = " + accessToken);
		System.out.println(new Date() + " : End Generating refresh token !!");
		return accessToken;
	}

	public String getItemCreationPayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started ItemCreationPayload===========");
		System.out.println("URL:- " + RESOURCE_URL_TPL);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_TPL).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended ItemCreationPayload===========");

		return payloadrespone;
	}
	
	
	public String getItemUpdatePayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started ItemUpdatePayload===========");
		String payloadrespone = "{data:[]}";
		try {
			System.out.println("URL:- " + RESOURCE_URL_TPL);
			System.out.println("payload:- " + payloadreq);
			System.out.println("accessToken = " + accessToken);

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_TPL).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();

			System.out.println("response:- " + payloadrespone);
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("===========Ended ItemUpdatePayload===========");

		return payloadrespone;
	}
	
	public String getItemCRMInsertAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started ItemCRMInsertAPI===========");
		System.out.println("URL:- " + RESOURCE_SRM_update);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_SRM_update).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended ItemCRMInsertAPI===========");

		return payloadrespone;
	}
	
	
	
	public String getSalesUpdateAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started SalesUpdateAPI===========");
		System.out.println("URL:- " + RESOURCE_SRM_update);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_SRM_update).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended SalesUpdateAPI===========");

		return payloadrespone;
	}
	
	
	public String getDispatchItemInsertAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started DispatchItemInsertAPI===========");
		System.out.println("URL:- " + RESOURCE_URL_Dispatch);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_Dispatch).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended DispatchItemInsertAPI===========");

		return payloadrespone;
	}

	Integer initialAuthenticationCounter = 0;
	
	public String getInvoiceItemInsertAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started InvoiceItemInsertAPI===========");
		System.out.println("URL:- " + RESOURCE_URL_Invoice);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_Invoice).buildQueryMessage();
		if (initialAuthenticationCounter < authenticationCounter) {
			try {
				bearerClientRequest.setBody(payloadreq);
				bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
				bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
				OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
						OAuth.HttpMethod.POST, OAuthResourceResponse.class);
				payloadrespone = respone.getBody();
			} catch (OAuthSystemException e) {
				System.out.println("OAuthSystemException = " + e);
				resetTokenMap();
				try {
					TimeUnit.SECONDS.sleep(authenticationDelay);
				}catch (InterruptedException ex){
					System.out.println("InterruptedException = " + ex);
				}
				System.out.println(new Date() + " : retry count (" + (initialAuthenticationCounter + 1) + ")");
				initialAuthenticationCounter += 1;
				getInvoiceItemInsertAPI(accessToken, payloadreq);

			}
		}else {
			initialAuthenticationCounter = 0;
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended InvoiceItemInsertAPI===========");

		return payloadrespone;
	}
	
	
	public String getOrderItemInsertAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started OrderItemInsertAPI===========");
		System.out.println("URL:- " + RESOURCE_URL_Order);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_Order).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended OrderItemInsertAPI===========");

		return payloadrespone;
	}

	public String getOrderItemUpdateAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started OrderItemInsertAPI===========");
		System.out.println("URL:- " + RESOURCE_URL_Order);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_Order).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended OrderItemInsertAPI===========");

		return payloadrespone;
	}


	public String getOrderUpdateAPI(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started OrderUpdateAPI===========");
		System.out.println("URL:- " + RESOURCE_URL_Order);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_URL_Order).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended OrderItemInsertAPI===========");

		return payloadrespone;
	}
	
	public String fetchSubformLineRecordIDs(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started fetchSubformLineRecordIDs===========");
		System.out.println("URL:- " + RESOURCE_SubForm_Order);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_SubForm_Order + "" + payloadreq).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.GET, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended fetchSubformLineRecordIDs===========");

		return payloadrespone;
	}

	public String creditDebitCreation(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started paymentCreationPayload===========");
		System.out.println("URL:- " + CREDIT_DEBIT_NOTE_API);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(CREDIT_DEBIT_NOTE_API).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse response = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = response.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			payloadrespone = e.getMessage();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended paymentCreationPayload===========");

		return payloadrespone;
	}

	public String paymentCreationPayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started paymentCreationPayload===========");
		System.out.println("URL:- " + RESOURCE_Payment_Creation);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Payment_Creation).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended paymentCreationPayload===========");

		return payloadrespone;
	}


	public String paymentUpdatePayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started paymentCreationPayload===========");
		System.out.println("URL:- " + RESOURCE_Payment_Creation);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Payment_Creation).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			payloadrespone = e.getMessage();
//			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended paymentCreationPayload===========");

		return payloadrespone;
	}


	public String pdfUploadApi(String accessToken, String documentNumber, String documentType, File file)
			throws IOException {
		System.out.println("===========Started pdfUploadApi===========");
		String uploadUrl = "https://creator.zoho.in/api/v2.1/goldplus/gold-plus/form/Customer_Document";

		System.out.println("URL:- " + uploadUrl);
		System.out.println("accessToken = " + accessToken);
		String responseString = "{data:[]}";

		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(uploadUrl);

		// Set headers
		postRequest.setHeader("Authorization", "Zoho-oauthtoken " + accessToken);
//		postRequest.setHeader("Content-Type", "multipart/form-data");


		// Prepare the multipart request body
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("Document_Number", documentNumber);
		builder.addTextBody("Document_Type", documentType);
		builder.addBinaryBody("File_field", file, ContentType.DEFAULT_BINARY, file.getName());

		HttpEntity multipart = builder.build();
		postRequest.setEntity(multipart);

		try {
			HttpResponse response = httpClient.execute(postRequest);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				responseString = EntityUtils.toString(responseEntity);
			}
		} catch (IOException e) {
			System.out.println("Exception = " + e);
			responseString = e.getMessage();
		}

		System.out.println("Response:- " + responseString);
		System.out.println("===========Ended pdfUploadApi===========");

		return responseString;
	}


	public String customerCreationPayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started customerCreationPayload===========");
		System.out.println("URL:- " + RESOURCE_Customer_Creation);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);

		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Customer_Creation).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended customerCreationPayload===========");

		return payloadrespone;
	}
	
	public String customerUpdatePayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started customerUpdatePayload===========");
		System.out.println("URL:- " + RESOURCE_Customer_Creation);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Customer_Creation).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.PUT, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended customerUpdatePayload===========");

		return payloadrespone;
	}
	

	
	public String stockCreationPayload(String accessToken, String payloadreq)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started stockCreationPayload===========");
		System.out.println("URL:- " + RESOURCE_Stock_Creation);
		System.out.println("payload:- " + payloadreq);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Stock_Creation).buildQueryMessage();
			bearerClientRequest.setBody(payloadreq);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended stockCreationPayload===========");

		return payloadrespone;
	}
	
	
	public String stockDeletionPayload(String accessToken,String jsonnn)
			throws OAuthSystemException, OAuthProblemException {

		System.out.println("===========Started stockDeletionPayload===========");
		System.out.println("URL:- " + RESOURCE_Stock_Deletion);
		System.out.println("payload:- " + jsonnn);
		System.out.println("accessToken = " + accessToken);
		String payloadrespone = "{data:[]}";
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			OAuthClientRequest bearerClientRequest = new OAuthBearerClientRequest(RESOURCE_Stock_Deletion).buildQueryMessage();
			bearerClientRequest.setBody(jsonnn);
			bearerClientRequest.setHeader("Authorization", "Bearer " + accessToken);
			bearerClientRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.JSON);
			OAuthResourceResponse respone = oAuthClient.resource(bearerClientRequest,
					OAuth.HttpMethod.POST, OAuthResourceResponse.class);
			payloadrespone = respone.getBody();
		}catch (OAuthSystemException e){
			System.out.println("exception = " + e);
			resetTokenMap();
		}
		System.out.println("response:- " + payloadrespone);
		System.out.println("===========Ended stockDeletionPayload===========");

		return payloadrespone;
	}
	
}
