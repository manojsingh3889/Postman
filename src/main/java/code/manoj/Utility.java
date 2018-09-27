package code.manoj;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class Utility {
	/**
	 * Utility for JSON to bean conversion.
	 *
	 * @param JSON JSON String
	 *
	 * @param type Class type of the java bean to be Constructed form JSON
	 *
	 * @return the java bean of 'type' type
	 *
	 * @throws JsonParseException
	 *
	 * @throws JsonMappingException
	 *
	 * @throws IOException

	 */
	public static <T> T jsonToBean(String JSON, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper=new ObjectMapper();
		//		mapper.setDateFormat(COMMON_DATE_FORMAT);
		//		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		T retVal;
		try {
			retVal=mapper.readValue(JSON, type);
		} catch (JsonMappingException e) {
			// I know i shouldn't do it.
			//			mapper.setDateFormat(new SimpleDateFormat("S"));
			retVal=mapper.readValue(JSON, type);
		}
		return retVal;
	}

	public static <T> T jsonToBean(String JSON, TypeReference<T> type) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper=new ObjectMapper();
		T retVal;
		try {
			retVal=mapper.readValue(JSON, type);
		} catch (JsonMappingException e) {
			retVal=mapper.readValue(JSON, type);
		}
		return retVal;
	}

	public static String beanToJSON(Object bean, Boolean prettyPrint)
			throws JsonGenerationException, JsonMappingException, IOException {
		return beanToJSON(bean, prettyPrint, false, false);
	}

	/**
	 *
	 * Utility for bean to JSON Conversion.
	 *
	 * @param bean java bean to be JSONfied
	 *
	 * @param prettyPrint set true for human readable format.
	 *
	 * @return JSON String
	 *
	 * @throws JsonGenerationException
	 *
	 * @throws JsonMappingException
	 *
	 * @throws IOException
	 */
	public static String beanToJSON(Object bean, Boolean prettyPrint, Boolean sortAlphabetically, Boolean removeNull)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		if (removeNull) {
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
			mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		}

		if (sortAlphabetically) {
			mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
		}

		return prettyPrint ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean)
				: mapper.writeValueAsString(bean);
	}
}