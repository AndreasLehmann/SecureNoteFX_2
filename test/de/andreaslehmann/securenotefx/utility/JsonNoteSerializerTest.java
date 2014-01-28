package de.andreaslehmann.securenotefx.utility;

import de.andreaslehmann.securenotefx.utility.JsonNoteSerializer;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andreas
 */
public class JsonNoteSerializerTest {

    public JsonNoteSerializerTest() {
    }

    /**
     * Test of serialize method, of class JsonNoteSerializer.
     */
    @Test
    public void testSerialize() {
        JsonNoteSerializer s = new JsonNoteSerializer();

        NoteEntity n = new NoteEntity("A", "B");
        String jsonString = s.serialize(n);
        System.out.println(jsonString);
        // {    "uniqueKey":"28f603e1-528b-44a0-b543-5043b93c469a",
        //      "title":"A",
        //      "body":"B",
        //      "createdOn":1390342303292,
        //      "lastSavedOn":0,
        //      "deletedOn":0,
        //      }
        assertTrue(jsonString.contains("uniqueKey"));
        assertTrue(jsonString.contains("title"));
        assertTrue(jsonString.contains("body"));
        assertTrue(jsonString.contains("deletedOn"));
    }

    /**
     * Test of deserialize method, of class JsonNoteSerializer.
     */
    @Test
    public void testDeserialize() {
        final String JSON1 = "{\"uniqueKey\":\"26c1701e-0d3f-4c05-8cf7-1e460d41ebc9\",\"title\":\"A\",\"body\":\"B\",\"createdOn\":1390342698605,\"lastSavedOn\":0,\"deletedOn\":0}";

        JsonNoteSerializer s = new JsonNoteSerializer();

        NoteEntity n = s.deserialize(JSON1);

        assertEquals("26c1701e-0d3f-4c05-8cf7-1e460d41ebc9", n.getUniqueKey().toString());
        assertEquals("A", n.getTitle());
        assertEquals("B", n.getBody());
        assertFalse(n.isDirty());
        assertFalse(n.isDeleted());

    }

}
