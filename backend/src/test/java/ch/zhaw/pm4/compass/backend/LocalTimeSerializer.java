package ch.zhaw.pm4.compass.backend;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonPrimitive;
import com.nimbusds.jose.shaded.gson.JsonSerializationContext;
import com.nimbusds.jose.shaded.gson.JsonSerializer;

public class LocalTimeSerializer implements JsonSerializer<LocalTime> {
	@Override
	public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.format(DateTimeFormatter.ISO_TIME));
	}
}