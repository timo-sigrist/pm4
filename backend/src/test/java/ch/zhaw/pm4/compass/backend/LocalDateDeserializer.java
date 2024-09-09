package ch.zhaw.pm4.compass.backend;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.json.JsonParseException;

import com.nimbusds.jose.shaded.gson.JsonDeserializationContext;
import com.nimbusds.jose.shaded.gson.JsonDeserializer;
import com.nimbusds.jose.shaded.gson.JsonElement;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_DATE);
	}
}
