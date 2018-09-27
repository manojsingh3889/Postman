package code.manoj;

import com.sun.research.ws.wadl.HTTPMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Postman work as a HTTP/HTTPs request wrapper which uses java.net to send request
 */
public class Postman {
	private static final Logger LOGGER = LoggerFactory.getLogger(Postman.class);

	private String url;
	private String message;
	private String method;
	private Integer expectedStatus;
	private Integer connectTimeout;
	private Integer readTimeout;
	private Map<String, String> headers;
	private Map<Integer, Class> responseHandlers;


    /**
     * default construtor.. you need to fill all details in builder pattern or normal
     */
	public Postman() {
		this.headers = new HashMap<>();
		this.responseHandlers = new HashMap<>();
		this.method = HTTPMethods.POST.name();
		this.connectTimeout = 5000;
		this.readTimeout = 40000;
	}

	public String getUrl() {
		return url;
	}

	public Postman setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Postman setMessage(String message) {
		this.message = message;
		return this;
	}

	public Postman getConnectTimeout() {
		return this;

	}

	public Postman setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public String getMethod() {
		return method;
	}

	public Postman setMethod(HTTPMethods method) {
		this.method = method.toString();
		return this;
	}

	public Postman setMethod(String method) {
		this.method = method;
		return this;
	}

	public Integer getExpectedStatus() {
		return expectedStatus;
	}

	public Postman setExpectedStatus(Integer expectedStatus) {
		this.expectedStatus = expectedStatus;
		return this;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Map<Integer, Class> getResponseHandlers() {
		return responseHandlers;
	}

	public void setResponseHandlers(Map<Integer, Class> responseHandlers) {
		this.responseHandlers = responseHandlers;
	}

	public Postman addHeader(String key, String value){
		headers.put(key, value);
		return this;
	}

	public Set<String> headerKeySet(){
		return headers.keySet();
	}

	public String getHeader(String key){
		return headers.get(key);
	}

	public Postman addResponseHandler(Integer code, Class handlerClass){
		responseHandlers.put(code,handlerClass);
		return this;
	}

	public Postman addBulkResponseHandlers(BulkResponseHandlers handlers){
		responseHandlers.putAll(handlers.getResponseHandlers());
		return this;
	}
	
	public Set<Integer> responseHandlerKeySet(){
		return responseHandlers.keySet();
	}

	public Class<?> getResponseHandler(String key){
		return responseHandlers.get(key);
	}

	public Object call() throws Exception{

		if(url.length()<1)
			throw new IllegalArgumentException("URL cannot be empty");

		LOGGER.info("Calling url - "+url);
		InputStream input = null;
		HttpURLConnection conn = null;
		OutputStream output = null;
		try {

			if(this.getUrl()!=null && this.getUrl().contains("https")){
				prepareSSLContext();
				conn = (HttpsURLConnection) new URL(this.getUrl()).openConnection();
			}else{
				conn = (HttpURLConnection) new URL(this.getUrl()).openConnection();
			}

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(this.method);
			conn.setConnectTimeout(this.connectTimeout);
			conn.setReadTimeout(this.readTimeout);
			for(String key: headers.keySet())
				conn.setRequestProperty(key, headers.get(key));

			output = new BufferedOutputStream(conn.getOutputStream());
			if(message != null && message.length()>0) {
				output.write(message.getBytes());
			}

			output.flush();

			// Get the response
			if (conn.getResponseCode() == expectedStatus) {
				input = conn.getInputStream();
			} else {
				input = conn.getErrorStream();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			LOGGER.info("Recieved Response : " + response);

			Class responseHandler = responseHandlers.containsKey(conn.getResponseCode())?
					responseHandlers.get(conn.getResponseCode()):responseHandlers.get(0); 

					if(responseHandler==null)
						return response.toString();
					else
						return Utility.jsonToBean(response.toString(), responseHandler);

		}finally{
			conn.disconnect();
			try {
				if(input!=null)
					input.close();

				if(output!=null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void prepareSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		}};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection
		.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}
