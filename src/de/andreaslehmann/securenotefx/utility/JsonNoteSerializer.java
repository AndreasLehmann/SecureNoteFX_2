/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.lang.reflect.Type;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Andreas
 */
public class JsonNoteSerializer {

    private final Gson gson;

    public JsonNoteSerializer() {
        super();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyTypeAdapter());
        gsonBuilder.registerTypeAdapter(SimpleBooleanProperty.class, new SimpleBooleanPropertyTypeAdapter());
        gson = gsonBuilder.create();
    }

    public String serialize(NoteEntity note) {
        String json = gson.toJson(note);
        return json;
    }

    public NoteEntity deserialize(String jsonString) {
        NoteEntity n = (NoteEntity) gson.fromJson(jsonString, NoteEntity.class);
        return n;
    }
    
        private static class SimpleStringPropertyTypeAdapter implements JsonSerializer<SimpleStringProperty>, JsonDeserializer<SimpleStringProperty> {

        @Override
        public JsonElement serialize(SimpleStringProperty t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(t.getValue());
        }

        @Override
        public SimpleStringProperty deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new SimpleStringProperty(je.getAsJsonPrimitive().getAsString());
        }
    }

    private static class SimpleBooleanPropertyTypeAdapter implements JsonSerializer<SimpleBooleanProperty>, JsonDeserializer<SimpleBooleanProperty> {

        @Override
        public JsonElement serialize(SimpleBooleanProperty t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(t.getValue());
        }

        @Override
        public SimpleBooleanProperty deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new SimpleBooleanProperty(je.getAsJsonPrimitive().getAsBoolean());
        }
    }

}
