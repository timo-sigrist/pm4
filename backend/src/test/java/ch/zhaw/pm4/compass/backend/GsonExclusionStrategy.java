package ch.zhaw.pm4.compass.backend;

import com.nimbusds.jose.shaded.gson.ExclusionStrategy;
import com.nimbusds.jose.shaded.gson.FieldAttributes;

public class GsonExclusionStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		// Exclude fields based on the field name
		return f.getName().equals("date");
	}

	@Override
	public boolean shouldSkipClass(Class<?> aClass) {
		return false;
	}
}