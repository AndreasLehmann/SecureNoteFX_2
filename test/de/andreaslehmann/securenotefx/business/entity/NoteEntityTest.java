/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.entity;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andreas
 */
public class NoteEntityTest {

    /**
     * Test of getTitle method, of class NoteEntity.
     */
    @Test
    public void testGetTitle() {
        System.out.println("getTitle");
        final String TEST_TITLE = "1234";
        NoteEntity instance = new NoteEntity(TEST_TITLE, "");
        String result = instance.getTitle();
        assertEquals(TEST_TITLE, result);
        assertEquals(TEST_TITLE, instance.titleProperty().getValue());
    }

    /**
     * Test of titleProperty method, of class NoteEntity.
     */
    @Test
    public void testTitleProperty() {
        System.out.println("titleProperty");
        final String TEST_TITLE = "1234";
        NoteEntity instance = new NoteEntity(TEST_TITLE, "");
        SimpleStringProperty result = instance.titleProperty();
        assertEquals(TEST_TITLE, result.getValue());
    }

    /**
     * Test of setTitle method, of class NoteEntity.
     */
    @Test
    public void testSetTitle() {
        System.out.println("setTitle");
        final String TEST_TITLE = "12345";
        NoteEntity instance = new NoteEntity();
        instance.setTitle(TEST_TITLE);

        assertEquals(TEST_TITLE, instance.getTitle());
        assertEquals(TEST_TITLE, instance.titleProperty().getValue());
    }

    /**
     * Test of getBody method, of class NoteEntity.
     */
    @Test
    public void testGetBody() {
        System.out.println("getBody");
        final String TEST_BODY = "5678 äöü ' ; { \\ /\"";
        NoteEntity instance = new NoteEntity("", TEST_BODY);
        String result = instance.getBody();
        assertEquals(TEST_BODY, result);
        assertEquals(TEST_BODY, instance.bodyProperty().getValue());
    }

    /**
     * Test of setBody method, of class NoteEntity.
     */
    @Test
    public void testSetBody() {
        System.out.println("setBody");
        final String TEST_BODY = "5678 äöü ' ; { \\ /\"";
        NoteEntity instance = new NoteEntity("A", "B");
        instance.setBody(TEST_BODY);
        assertEquals(TEST_BODY, instance.getBody());
        assertEquals(TEST_BODY, instance.bodyProperty().getValue());
    }

    /**
     * Test of bodyProperty method, of class NoteEntity.
     */
    @Test
    public void testBodyProperty() {
        System.out.println("bodyProperty");
        final String TEST_BODY = "1234";
        NoteEntity instance = new NoteEntity("", TEST_BODY);
        SimpleStringProperty result = instance.bodyProperty();
        assertEquals(TEST_BODY, result.getValue());
    }

    /**
     * Test of getUniqueKey method, of class NoteEntity.
     */
    @Test
    public void testGetUniqueKey() {
        System.out.println("getUniqueKey");

        NoteEntity instance1 = new NoteEntity();
        NoteEntity instance2 = new NoteEntity();
        assertNotNull(instance1.getUniqueKey()); // wurde ein schlüssel erzeugt?
        assertNotNull(instance2.getUniqueKey()); // wurde ein schlüssel erzeugt?
        assertNotSame(instance1.getUniqueKey().toString(), instance2.getUniqueKey().toString()); // hat ein neues Objekt einen anderen schlüssel?

        instance1 = new NoteEntity("A", "B");
        instance2 = new NoteEntity("A", "B");
        assertNotNull(instance1.getUniqueKey()); // wurde ein schlüssel erzeugt?
        assertNotNull(instance2.getUniqueKey()); // wurde ein schlüssel erzeugt?
        assertNotSame(instance1.getUniqueKey().toString(), instance2.getUniqueKey().toString()); // hat ein neues Objekt einen anderen schlüssel?
    }

    /**
     * Test of getCreatedOn() method, of class NoteEntity.
     */
    @Test
    public void testGetCreatedOn() {
        System.out.println("getCreatedOn");

        long now = System.currentTimeMillis();
        NoteEntity instance = new NoteEntity();

        assertTrue(instance.getCreatedOn() > 0); // größer 0

        assertTrue((instance.getCreatedOn() - now) < 5); // Erzeugung innerhalb 5ms

    }
    @Test
    public void testGetDeletedOn() {
        System.out.println("getDeletedOn");

        long now = System.currentTimeMillis();
        NoteEntity instance = new NoteEntity();

        assertSame(0L, instance.getDeletedOn());

        assertTrue((instance.getDeletedOn() - now) < 5); // Löschung innerhalb 5ms

    }
    /**
     * Test of hashCode method, of class NoteEntity.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        NoteEntity instance1 = new NoteEntity();
        NoteEntity instance2 = new NoteEntity();
        assertNotSame(instance2.hashCode(), instance1.hashCode());

        instance1 = new NoteEntity("A", "B");
        instance2 = new NoteEntity("A", "B");
        assertNotSame(instance2.hashCode(), instance1.hashCode());

        assertEquals(instance1.hashCode(), instance1.hashCode());// wiederholbarkeit

        assertEquals(instance1.hashCode(), instance1.cloneElement().hashCode());// gleiches Element, gleicher HashCode
    }

    /**
     * Test of equals method, of class NoteEntity.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        NoteEntity instance1 = new NoteEntity();
        NoteEntity instance2 = new NoteEntity();
        NoteEntity instance3 = instance1.cloneElement();

        assertFalse(instance1.equals(obj)); // false bei Null-Vergleich
        assertFalse(instance1.equals(instance2));
        assertFalse(instance1.equals(instance2)); // wiederholbar

        assertFalse(instance2.equals(instance1));
        assertTrue(instance3.equals(instance1));
        assertTrue(instance1.equals(instance3));

        instance1.setLastSavedOn(1000010L);
        instance3.setLastSavedOn(1000020L);
        assertFalse(instance1.equals(instance3)); // timestamp changes

         instance1 = new NoteEntity();
         instance2 = instance1.cloneElement();
         instance1.delete();
         assertNotSame(instance1,instance2);
         
        
        
    }

    /**
     * Test of cloneElement method, of class NoteEntity.
     */
    @Test
    public void testCloneElement() {
        System.out.println("cloneElement");
        final String TEST_TITLE = "12345";
        final String TEST_BODY = "345566";

        NoteEntity source = new NoteEntity(TEST_TITLE, TEST_BODY);
        NoteEntity dest = source.cloneElement();

        assertTrue(source != dest); // keine Objektgleichheit !
        assertTrue(source.hashCode() == dest.hashCode()); // gleicher HashCode
        assertEquals(source.getUniqueKey(), dest.getUniqueKey()); // gleiche UUID !

        assertEquals(source.getBody(), dest.getBody()); // gleicher Body
        assertEquals(source.getTitle(), dest.getTitle()); // gleicher title

        assertEquals(source.getCreatedOn(), dest.getCreatedOn()); // gleiches Erzeugungsdatum
        assertEquals(source.getLastSavedOn(), dest.getLastSavedOn()); // gleiches Speicherdatum

        assertEquals(source.isDirty(),dest.isDirty());
    
    }

    
    @Test
    public void testisDeleted() {
        System.out.println("isDeleted");
        NoteEntity n = new NoteEntity();
        assertFalse(n.isDeleted());
        n.delete();
        assertTrue(n.isDeleted());
    }
        
    @Test
    public void testDirty_Constr1() {
        System.out.println("Dirty_Constr1");
        NoteEntity n;

        n = new NoteEntity();
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_Constr2() {
        System.out.println("Dirty_Constr2");
        NoteEntity n;

        n = new NoteEntity("A", "B");
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_AfterTitle1() {
        System.out.println("Dirty_AfterTitle1");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.setTitle("C");
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testDirty_AfterTitle2() {
        System.out.println("Dirty_AfterTitle2");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.setTitle("A"); // Title is already "A"
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_AfterTitle3() {
        System.out.println("Dirty_AfterTitle3");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.titleProperty().set("C");
        assertEquals(true, n.isDirty());
    }

    @Test
    public void testDirty_AfterTitle4() {
        System.out.println("Dirty_AfterTitle4");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.titleProperty().setValue("C");
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testDirty_AfterTitle5() {
        System.out.println("Dirty_AfterTitle5");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.titleProperty().setValue("A");
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_AfterBody1() {
        System.out.println("Dirty_AfterBody1");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.setBody("C");
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testDirty_AfterBody2() {
        System.out.println("Dirty_AfterBody2");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.setBody("B");
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_AfterBody3() {
        System.out.println("Dirty_AfterBody3");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.bodyProperty().set("C");
        assertEquals(true, n.isDirty());
    }

    @Test
    public void testDirty_AfterBody4() {
        System.out.println("Dirty_AfterBody4");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.bodyProperty().setValue("C");
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testDirty_AfterBody5() {
        System.out.println("Dirty_AfterBody5");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.bodyProperty().setValue("B");
        assertEquals(false, n.isDirty());
    }

    @Test
    public void testDirty_AfterDelete() {
        System.out.println("testDirty_AfterDelete");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.delete();
        assertEquals(true, n.isDirty());
    }

    @Test
    public void testSetDirty() {
        System.out.println("setDirty");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        // TEST
        n.setDirty();
        assertEquals(true, n.isDirty());
    }

    @Test
    public void testPropertyBind_Body1() {
        System.out.println("PropertyBind_Body1");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        
        Property bodyproperty = new SimpleStringProperty();
        bodyproperty.bindBidirectional(n.bodyProperty());
        // TEST
        n.bodyProperty().setValue("B");
        assertEquals(false, n.isDirty());
    }
    
    @Test
    public void testPropertyBind_Body2() {
        System.out.println("PropertyBind_Body2");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        
        Property bodyproperty = new SimpleStringProperty();
        bodyproperty.bindBidirectional(n.bodyProperty());
        // TEST
        n.bodyProperty().setValue("X");
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testPropertyBind_Body3() {
        System.out.println("PropertyBind_Body3");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        
        Property bodyproperty = new SimpleStringProperty();
        bodyproperty.bindBidirectional(n.bodyProperty());
        // TEST
        bodyproperty.setValue("X");
        
        assertEquals(true, n.isDirty());
    }
    @Test
    public void testPropertyBind_Body4() {
        System.out.println("PropertyBind_Body4");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        
        Property bodyproperty = new SimpleStringProperty();
        bodyproperty.bindBidirectional(n.bodyProperty());
        // TEST
        bodyproperty.setValue("B");
        
        assertEquals(false, n.isDirty());
    }
    
    @Test
    public void testSyncronizedFlag_AfterTitle1() {
        System.out.println("SyncronizedFlag_AfterTitle1");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        assertEquals(true, n.isSyncronized());
        // TEST
        n.setTitle("C");
        assertEquals(false, n.isSyncronized());
    }
    
    @Test
    public void testSyncronizedFlag_AfterBody() {
        System.out.println("SyncronizedFlag_AfterBody");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        assertEquals(true, n.isSyncronized());
        // TEST
        n.setBody("C");
        assertEquals(false, n.isSyncronized());
    }
    @Test
    public void testSyncronizedFlag_AfterDirty() {
        System.out.println("SyncronizedFlag_AfterDirty");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        assertEquals(true, n.isSyncronized());
        // TEST
        n.setDirty();
        assertEquals(false, n.isSyncronized());
    }
    
    @Test
    public void testSyncronizedFlag_AfterSave1() {
        System.out.println("SyncronizedFlag_AfterSave1");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        assertEquals(true, n.isSyncronized());
        // TEST
        n.setLastSavedOn(10000L);
        assertEquals(true, n.isSyncronized());
    }
    @Test
    public void testSyncronizedFlag_AfterSave2() {
        System.out.println("SyncronizedFlag_AfterSave2");
        NoteEntity n;
        n = new NoteEntity("A", "B");
        n.setDirty();
        assertEquals(false, n.isSyncronized());
        // TEST
        n.setLastSavedOn(10000L);
        assertEquals(false, n.isSyncronized());
    }
    
}
