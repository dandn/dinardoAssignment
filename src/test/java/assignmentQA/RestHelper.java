package assignmentQA;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestHelper {
	public static Response restGet(RequestSpecification httpRequest, String input) {
		int retryCount = 10;
		Response response = null;
		do {
			try {
				response = httpRequest.get(input);
				break;
			}
			catch (Exception e) {
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				retryCount--;
				if (retryCount == 0) {
					throw e;
				}
			}
		} while (retryCount > 0);
		
		return response;
	}
	
	public static Response restDelete(RequestSpecification httpRequest, String input) {
		int retryCount = 10;
		Response response = null;
		do {
			try {
				response = httpRequest.delete(input);
				break;
			}
			catch (Exception e) {
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				retryCount--;
				if (retryCount == 0) {
					throw e;
				}
			}

		} while (retryCount > 0);
		
		return response;		
	}

	public static Response restPost(RequestSpecification httpRequest, String input) {
		int retryCount = 10;
		Response response = null;
		do {
			try {
				response = httpRequest.post(input);
				break;
			}
			catch (Exception e) {
        			try {
					Thread.sleep(100);
				} 
        			catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
        			retryCount--;
        			if (retryCount == 0) {
        				throw e;
        			}
			}
		} while (retryCount > 0);

		return response;	
	}

	public static Response restPut(RequestSpecification httpRequest, String input) {
		int retryCount = 10;
		Response response = null;
		do {
			try {
				response = httpRequest.put(input);
				break;
			}
			catch (Exception e) {
        			try {
					Thread.sleep(100);
				} 
        			catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
        			retryCount--;
        			if (retryCount == 0) {
        				throw e;
        			}
			}
		} while (retryCount > 0);

		return response;	
	}
}
