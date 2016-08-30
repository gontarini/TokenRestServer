package com.sotrender.api_server.exceptions;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * Class which handles json validation during post process
 * @author pawel
 *
 */
public class JsonValidation {

	
	public static boolean isJsonValid(JsonSchema jsonSchemaNode,
			JsonNode jsonNode) throws ProcessingException {
		ProcessingReport report = jsonSchemaNode.validate(jsonNode);
		return report.isSuccess();
	} 
	public static boolean isJsonValid(String schemaText, String jsonText)
			throws ProcessingException, IOException {
		final JsonSchema schemaNode = getSchemaNode(schemaText);
		final JsonNode jsonNode = getJsonNode(jsonText);
		return isJsonValid(schemaNode, jsonNode);
	} 
	public static JsonSchema getSchemaNode(String schemaText)
			throws IOException, ProcessingException {
		final JsonNode schemaNode = getJsonNode(schemaText);
		return _getSchemaNode(schemaNode);
	} 
	private static JsonSchema _getSchemaNode(JsonNode jsonNode)
			throws ProcessingException {
		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		return factory.getJsonSchema(jsonNode);
	} 
	public static JsonNode getJsonNode(String jsonText) throws IOException {
		return JsonLoader.fromString(jsonText);
	} 
}
