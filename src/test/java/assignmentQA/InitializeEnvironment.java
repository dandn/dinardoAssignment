package assignmentQA;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

public class InitializeEnvironment {
	public static void initializeUsingREST(int numPetItems) {
		RestAssured.baseURI = getWebAppLocation();
		RequestSpecification httpRequest = RestAssured.given();
		
		Response response = RestHelper.restGet(httpRequest, "/api/pets");
		
		Headers headers = response.getHeaders();
		String contentType = headers.getValue("Content-Type");
		String[] contentTypeParts = contentType.split(";");
		assertEquals("application/json", contentTypeParts[0]);
		
		// get status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
		
		// Retrieve the body of the Response
		ResponseBody body = response.getBody();
		String bodyString = body.asString();
		
		// create a jsonObject so that we can retrieve data
		JsonArray jsonArray = new JsonParser().parse(bodyString).getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++){
        		JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
        		String petId = jsonObject.get("id").getAsString();
        		
        		response = RestHelper.restDelete(httpRequest, "/api/pets/" + petId);
        }
        
		for (int i = 0; i < numPetItems; i++) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("name", "PetName" + i);
			jsonObject.addProperty("status", "PetStatus" + i);
			httpRequest = RestAssured.given().body(jsonObject.toString()).contentType(ContentType.JSON);
			
			response = RestHelper.restPost(httpRequest, "/api/pets");
		}
	}

	public static void configureChromeDriver() {
		System.setProperty("webdriver.chrome.driver", "/Users/ddinardo/Documents/ChromeDriver/chromedriver");
	}
	
	public static String getWebAppLocation() {
		return "http://localhost:3000";
	}
}
