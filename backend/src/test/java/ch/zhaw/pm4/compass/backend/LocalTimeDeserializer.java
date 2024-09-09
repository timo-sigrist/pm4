package ch.zhaw.pm4.compass.backend;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.json.JsonParseException;

import com.nimbusds.jose.shaded.gson.JsonDeserializationContext;
import com.nimbusds.jose.shaded.gson.JsonDeserializer;
import com.nimbusds.jose.shaded.gson.JsonElement;

public class LocalTimeDeserializer implements JsonDeserializer<LocalTime> {
	@Override
	public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME);
	}
}