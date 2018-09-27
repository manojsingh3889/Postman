package code.manoj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Manoj Singh
 *
 */
public class BulkResponseHandlers {
	private Map<Integer, Class> responseHandlers;
	
	public BulkResponseHandlers(){
		this.responseHandlers = new HashMap<>();
	}
	
	public BulkResponseHandlers addResponseHandler(Integer code, Class handlerClass){
		responseHandlers.put(code,handlerClass);
		return this;
	}

	public Set<Integer> responseHandlerKeySet(){
		return responseHandlers.keySet();
	}

	public Class<?> getResponseHandler(String key){
		return responseHandlers.get(key);
	}

	public Map<Integer, Class> getResponseHandlers() {
		return responseHandlers;
	}
}
